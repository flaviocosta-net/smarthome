/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.events.ThingStatusInfoChangedEvent;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapTranslationProvider;
import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ValueReferenceExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.ComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.style.StringStylePropertyValue;
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of component states based on EOL expressions.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <D> Component data type.
 */
public abstract class AbstractComponentState<D> implements ComponentState<D> {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(AbstractComponentState.class);

    /**
     * Translation provider for labels.
     */
    private final SitemapTranslationProvider translator;

    /**
     * Component associated with the variable.
     */
    protected Component<D> component;

    /**
     * ETL context.
     */
    protected final IEtlContext context;

    /**
     * Convenient access to ETL frame stack variables.
     */
    protected final EolFrame frame;

    /**
     * Item dependencies of this component.
     */
    private final Set<Item> itemDependencies = new HashSet<>();

    /**
     * Thing dependencies of this component.
     */
    private final Set<Thing> thingDependencies = new HashSet<>();

    /**
     * Sitemap dependencies of this component.
     */
    private final Set<Sitemap> sitemapDependencies = new HashSet<>();

    /**
     * Event subscribers for state changes.
     */
    protected Set<SitemapEventSubscriber> subscribers = Collections.emptySet();

    /**
     * Sitemap where this variable is referenced.
     */
    protected final Sitemap sitemap;

    /**
     * Container where this variable is referenced.
     */
    protected final Container<?, ?> container;

    /**
     * Style applicable to this component.
     */
    private final Map<String, Expression> style;

    /**
     * Base constructor that can be used from subclasses.
     *
     * @param translator Translation provider for labels.
     * @param component Sitemap component.
     * @param expression EOL expression to calculate the state.
     * @param style All styles defined in the current container.
     * @param context Execution context.
     */
    protected AbstractComponentState(SitemapTranslationProvider translator, Component<D> component,
            Map<String, Expression> style, IEtlContext context) {
        this.translator = translator;
        this.component = component;
        this.context = context;
        // TODO stop using global variables instead of method arguments?
        this.frame = EolFrame.on(context);
        this.sitemap = (Sitemap) getVarValue(EolFrame.VarName.SITEMAP);
        this.container = (Container<?, ?>) getVarValue(EolFrame.VarName.CONTAINER);
        if (style == null) {
            this.style = null;
        } else {
            this.style = filterStyle(component, style);
            for (Entry<String, Expression> s : this.style.entrySet()) {
                retrieveDependencies(s.getValue());
                setStyle(s.getKey(), recalculateStyle(s.getKey()));
            }
        }
    }

    /**
     * Changes a style property value of the component.
     *
     * @param name Property name.
     * @param newValue New property value.
     */
    private void setStyle(String name, StylePropertyValue newValue) {
        component.setStyle(name, newValue);
    }

    /**
     * Filter the provided styles by removing the styles that do not apply to the provided component, and removing the
     * style prefix that identifies the component that the style refers to.
     *
     * @param component Component to which styles should apply directly.
     * @param style Map will all style definitions.
     * @return Filtered style definitions.
     */
    private Map<String, Expression> filterStyle(Component<D> component, Map<String, Expression> style) {
        String componentStylePrefix;
        switch (component.getType()) {
            case FRAME:
            case WIDGET:
            case GROUP:
            case PAGE:
            case SITEMAP:
                componentStylePrefix = null;
                break;
            case ICON:
                componentStylePrefix = "icon.";
                break;
            case LABEL:
                componentStylePrefix = "label.";
                break;
            case COLORPICKER:
            case MAPVIEW:
            case SELECTION:
            case SLIDER:
            case SWITCH:
            case TEXT:
                componentStylePrefix = "value.";
                break;
            default:
                throw new AssertionError("Missing case " + component.getType().name());
        }

        Predicate<Map.Entry<String, Expression>> pred = e -> (componentStylePrefix == null
                && e.getKey().indexOf('.') == -1)
                || componentStylePrefix != null && e.getKey().startsWith(componentStylePrefix);

        return style.entrySet().stream().filter(pred).collect(
                Collectors.toMap(e -> stripStylePrefix(componentStylePrefix, e.getKey()), Map.Entry::getValue));
    }

    /**
     * Remove the style prefix from the style property name. It assumes that the property name does start with the
     * provided prefix. For instance "label.color" should be returned just as "color".
     *
     * @param componentStylePrefix Prefix to be removed from the property name.
     * @param key Style property name.
     * @return Property name without the given prefix.
     */
    private String stripStylePrefix(String componentStylePrefix, String key) {
        if (componentStylePrefix == null) {
            return key;
        }
        assert key.startsWith(componentStylePrefix);
        return key.substring(componentStylePrefix.length());
    }

    @Override
    public String getDisplayValue(Locale locale) {
        D data = component.getData();
        if (data == null) {
            return null;
        }
        if (component.getType() == ComponentType.LABEL && data.equals(component.getId())) {
            // when the label has its own ID as the defined data, we may need to look for a translation
            return translator.getText(sitemap.getId(), component.getId(), data.toString(), locale);
        }
        return data.toString();
    }

    /**
     * Convenience method to get the value of a variable in the stack frame.
     *
     * @param name Variable name.
     * @return Variable value.
     */
    protected Object getVarValue(VarName name) {
        return this.frame.get(name).getValue();
    }

    /**
     * Retrieve all dependencies for the state calculation.
     *
     * @param currentExpression Expression to retrieve the dependencies for.
     */
    protected void retrieveDependencies(ModuleElement currentExpression) {
        if (currentExpression instanceof ValueReferenceExpression) {
            addDependency(((ValueReferenceExpression) currentExpression).getValueReference().getSource());
        }
        // process children recursively
        currentExpression.getChildren().stream().forEach(this::retrieveDependencies);
    }

    /**
     * Add the dependency to the respective list depending on the dependency type.
     *
     * @param source Object that the state calculation is dependent on.
     */
    private void addDependency(Object source) {
        if (source instanceof Item) {
            itemDependencies.add((Item) source);
        } else if (source instanceof Thing) {
            thingDependencies.add((Thing) source);
        } else if (source instanceof Sitemap) {
            sitemapDependencies.add((Sitemap) source);
        } else {
            throw new IllegalArgumentException("Unsupported dependency type: " + source.getClass());
        }
    }

    @Override
    public Component<D> getComponent() {
        return component;
    }

    @Override
    public void setSubscribers(Set<SitemapEventSubscriber> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Verifies whether the state has changed, and if it has then it updates the changed value and notifies the
     * subscribers about the change. This change can be anything on the state (data or style).
     */
    protected abstract void handleDependencyChange();

    /**
     * Verifies whether the style of the state has changed, and if it has then it updates the changed value and notifies
     * the subscribers about the style change.
     */
    protected void handleStyleDependencyChange() {
        if (component.getStyleMap() == null) {
            return;
        }
        synchronized (context) {
            for (Entry<String, StylePropertyValue> entry : component.getStyleMap().entrySet()) {
                final String styleName = entry.getKey();
                final StylePropertyValue oldValue = entry.getValue();
                final StylePropertyValue newValue = recalculateStyle(styleName);
                if (isValueChanged(oldValue, newValue)) {
                    component.setStyle(styleName, newValue);
                    subscribers.forEach(t -> t.styleChanged(EventAction.CHANGED, sitemap, component, styleName));
                }
            }
        }
    }

    private StylePropertyValue recalculateStyle(String styleName) {
        try {
            Object calculatedValue = style.get(styleName).execute(context);
            if (calculatedValue != null) {
                return new StringStylePropertyValue(calculatedValue.toString());
            }
        } catch (EolRuntimeException | SecurityException e) {
            logger.error("Error executing expression", e);
        }

        return null;
    }

    /**
     * Verifies any value changes while avoiding a {@link NullPointerException}.
     *
     * @param oneValue One value to be compared.
     * @param anotherValue Another value to be compared.
     * @return Whether the values are the same or not.
     */
    protected boolean isValueChanged(Object oneValue, Object anotherValue) {
        return (anotherValue != null && !anotherValue.equals(oneValue))
                || (oneValue != null && !oneValue.equals(anotherValue));
    }

    @Override
    public void itemStateChanged(ItemStateChangedEvent event) {
        if (this.itemDependencies.stream().anyMatch(i -> i.getName().equals(event.getItemName()))) {
            // the changed item is in the list of dependencies of this component
            handleDependencyChange();
        }
    }

    @Override
    public void thingStatusChanged(ThingStatusInfoChangedEvent event) {
        if (this.thingDependencies.stream().anyMatch(t -> t.getUID().equals(event.getThingUID()))) {
            // the changed thing is in the list of dependencies of this component
            handleDependencyChange();
        }
    }
}
