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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.reference;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.IllegalFormatConversionException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.transform.actions.Transformation;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapTranslationProvider;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state.AtomComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state.ContainerComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.MappingReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ReferenceResolver;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.ItemMappingReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.ItemValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.SitemapValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.ThingValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.VoidValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.ComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component implementing the {@link ReferenceResolver} for sitemap rendering.
 *
 * @author Flavio Costa - Initial contribution.
 */
@Component(service = ReferenceResolver.class)
public class ReferenceResolverImpl implements ReferenceResolver {

    /**
     * Nested implementation of the {@link Builder}.
     */
    public static class BuilderImpl implements Builder {

        /**
         * Pattern for automatically generated component ids.
         */
        private static final String AUTO_ID_PATTERN = "~%.2s%04d";

        /**
         * How many components without a defined name (id) have been processed so far.
         */
        private final int[] namelessCount = new int[ComponentType.values().length];

        @Override
        public String autoId(ComponentType type) {
            return String.format(AUTO_ID_PATTERN, type.name().toLowerCase(), ++namelessCount[type.ordinal()]);
        }

        @Override
        public Atom<?> atom(String typeName) {
            try {
                ComponentType type = ComponentType.valueOf(typeName);
                @SuppressWarnings("unchecked")
                Class<? extends Atom<?>> atomClass = (Class<? extends Atom<?>>) type.getImplementingClass();
                Atom<?> atom = atomClass.cast(
                        atomClass.getConstructor(String.class, ComponentType.class).newInstance(autoId(type), type));
                return atom;
            } catch (ClassCastException | InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(ReferenceResolverImpl.class);

    /**
     * Translation provider for labels.
     */
    private SitemapTranslationProvider translator;

    /**
     * ESH item registry.
     */
    private ItemRegistry itemRegistry;

    /**
     * ESH Thing registry.
     */
    private ThingRegistry thingRegistry;

    /**
     * Sitemap rendering driver.
     */
    private SitemapRenderingDriver driver;

    @Override
    public void setSitemapRenderingDriver(SitemapRenderingDriver driver) {
        // TODO This prevents a circular reference, but still indicates
        // a possible need to refactor the relationship between the
        // reference resolver and the rendering driver
        this.driver = driver;
    }

    @Reference
    public void setSitemapTranslationProvider(SitemapTranslationProvider translator) {
        this.translator = translator;
    }

    public void unsetSitemapTranslationProvider() {
        this.translator = null;
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry() {
        this.itemRegistry = null;
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    public void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

    /**
     * Used for transformation debugging purposes - it outputs an object to the debug log; the developer may also use a
     * breakpoint on this method to inspect the object at runtime.
     *
     * @param obj Object to be inspected.
     * @return Same object instance passed as a parameter.
     */
    public Object debug(Object object) {
        if (object != null) {
            logger.debug("{}: {}", object.getClass().getSimpleName(), object.toString());
        }
        return object;
    }

    @Override
    public Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public VoidValueReference none() {
        return VoidValueReference.INSTANCE;
    }

    @Override
    public ValueReference<?, ?> item(String itemId) throws ItemNotFoundException {
        if (itemId == null) {
            return none();
        }
        return new ItemValueReference(itemRegistry.getItem(itemId));
    }

    @Override
    public ValueReference<?, ?> thing(String thingUID) {
        if (thingUID == null) {
            return none();
        }
        Thing thing = thingRegistry.get(new ThingUID(thingUID));
        if (thing != null) {
            return new ThingValueReference(thing);
        }
        throw new IllegalArgumentException("Thing not found: " + thingUID);
    }

    @Override
    public ValueReference<?, ?> sitemap(String sitemapId) {
        if (sitemapId == null) {
            return none();
        }
        RenderingModel model = driver.getRenderingModelFromCache(sitemapId);
        if (model != null) {
            return new SitemapValueReference(model.getSitemap());
        }
        throw new IllegalArgumentException("Sitemap not found: " + sitemapId);
    }

    @Override
    public String formatting(ValueReference<?, ?> variableRef) {
        switch (variableRef.getVariableType()) {
            case ITEM:
                Item item = (Item) variableRef.getSource();
                State state = item.getState();
                if (state instanceof UnDefType) {
                    return null;
                }
                StateDescription sd = item.getStateDescription();
                if (sd == null) {
                    return state.toFullString();
                }
                try {
                    return state.format(sd.getPattern());
                } catch (IllegalFormatConversionException e) {
                    logger.error("Conversion error on item " + item.getName(), e);
                    throw e;
                }
            case NONE:
                return null;
            case SITEMAP:
            case THING:
                return variableRef.getValue().toString();
            default:
                throw new UnsupportedOperationException(
                        "Unsupported VariableReference type: " + variableRef.getClass());
        }
    }

    @Override
    public MappingReference<Item, State> options(ItemValueReference itemRef, Map<String, ?> mappings) {
        return new ItemMappingReference(itemRef, mappings);
    }

    @Override
    public MappingReference<Item, State> options(ItemValueReference itemRef) {
        return options(itemRef, Collections.emptyMap());
    }

    @Override
    public String transformation(String type, String transformation, String value) {
        return Transformation.transform(type.toUpperCase(), transformation, value);
    }

    @Override
    public Set<ItemValueReference> members(ItemValueReference variableRef) {
        // TODO is this currently used anywhere?
        return ((GroupItem) variableRef.getSource()).getMembers().stream().map(ItemValueReference::new)
                .collect(Collectors.toSet());
    }

    @Override
    public <T> ComponentState<T> state(Atom<T> atom, Expression expression, Map<String, Expression> style,
            IEtlContext context) {
        if (expression == null) {
            throw new IllegalArgumentException("Null expression for " + atom);
        }
        return new AtomComponentState<T>(translator, atom, expression, style, context);
    }

    @Override
    public <T> ComponentState<T> state(Container<T, ?> container, Map<String, Expression> style, IEtlContext context) {
        return new ContainerComponentState<T>(translator, container, style, context);
    }
}
