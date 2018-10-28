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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;
import org.eclipse.smarthome.core.library.sets.TaggedUnion;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapTranslationProvider;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Atom component states based on EOL expressions.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <D> Component data type.
 */
public class AtomComponentState<D> extends AbstractComponentState<D> {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(AtomComponentState.class);

    /**
     * EOL Expression associated with this component's data.
     */
    private final Expression dataExpression;

    /**
     * Value reference the component is associated with.
     */
    private final ValueReference<?, ?> valueRef;

    /**
     * Constructor that sets the atom state parameters from the sitemap.
     *
     * @param translator Translation provider for labels.
     * @param atom Sitemap atom.
     * @param expression EOL expression to calculate the state.
     * @param style Styles defined in the container.
     * @param context Execution context.
     */
    public AtomComponentState(SitemapTranslationProvider translator, Atom<D> atom, Expression expression,
            Map<String, Expression> style, IEtlContext context) {
        super(translator, atom, style, context);
        this.dataExpression = expression;
        this.valueRef = (ValueReference<?, ?>) getVarValue(EolFrame.VarName.VALUE_REF);

        // retrieve the dependencies for the data
        retrieveDependencies(expression);
        setData(recalculateData());
    }

    @Override
    protected void handleDependencyChange() {
        synchronized (context) {
            handleStyleDependencyChange();

            // and then handle atom data dependency change
            final D oldValue = component.getData();
            final D newValue = recalculateData();
            // verify any value changes while avoiding a NPE
            if (isValueChanged(oldValue, newValue)) {
                setData(newValue);
                Atom<D> atom = (Atom<D>) component;
                subscribers.forEach(t -> t.atomChanged(EventAction.CHANGED, sitemap, container, atom, oldValue));
            }
        }
    }

    /**
     * Recalculates the data value.
     *
     * @return The data value.
     */
    private D recalculateData() {
        synchronized (context) {
            try {
                frame.put(EolFrame.VarName.COMPONENT, this);
                Object calculatedValue = dataExpression.execute(context);
                if (calculatedValue != null) {
                    Field dataField = combineFields(component.getClass()).stream()
                            .filter(f -> f.getName().equals("data")).findFirst().get();
                    Type dataFieldType = getFieldType(component, dataField);
                    Type calculatedType = calculatedValue.getClass();
                    return convertValue(calculatedValue, calculatedType, dataFieldType);
                }
            } catch (EolRuntimeException | SecurityException e) {
                logger.error("Error executing expression", e);
            }
            return null;
        }
    }

    /**
     * Changes the data value of the component.
     *
     * @param newValue New value to be set - assumes the component to be an Atom.
     */
    private void setData(D newValue) {
        if (component instanceof Atom) {
            // only atoms can have the data changed
            ((Atom<D>) component).setData(newValue);
        } else {
            throw new IllegalStateException("Component must be of Atom type");
        }
    }

    /**
     * Since state values are typically returned as String or some other non-specific data type, this method converts
     * such value into the actual target Java type.
     *
     * @param value Calculated value.
     * @param sourceType Type of the provided value.
     * @param targetType Type the value must be converted to.
     * @return Value converted to the target type.
     * @throws EolRuntimeException If the conversion is not possible or not supported.
     */
    private D convertValue(Object value, Type sourceType, Type targetType) throws EolRuntimeException {
        // TODO remove those ugly typecasts, somehow...
        if (sourceType.equals(targetType)) {
            return (D) value;
        }
        if (value instanceof UnDefType) {
            return null;
        }
        State state = null;
        if (valueRef.getVariableType() == ValueReference.Type.ITEM) {
            state = (State) valueRef.getValue();
        }
        switch (component.getType()) {
            case ICON:
                return (D) iconURL((String) value, state);
            case SWITCH:
            case SELECTION:
                // TODO on switch, the value will rather be org.eclipse.smarthome.core.library.types.OnOffType
                return (D) taggedUnion((TaggedUnion<String, String>) value, state);
        }
        if (value instanceof State) {
            if (targetType.equals(String.class)) {
                return (D) ((State) value).toFullString();
            }
            if (targetType.equals(Double.class)) {
                return (D) Double.valueOf(((DecimalType) value).doubleValue());
            }
        }
        if (value instanceof ThingStatusInfo) {
            if (targetType.equals(String.class)) {
                return (D) ((ThingStatusInfo) value).getStatus().toString();
            }
        }
        if (value instanceof String) {
            if (targetType.equals(Double.class)) {
                return (D) Double.valueOf((String) value);
            }
        }
        throw new EolRuntimeException(
                "I don't know what to do with the calculated " + sourceType + " when " + targetType + " is expected");
    }

    /**
     * If a field type is defined as a generic one, go up to the generic superclass
     * to determine the actual type argument. It is assumed here that the type of
     * generic fields corresponds to the first and only the type argument of the
     * generic superclass.
     *
     * @param instance
     *            Object instance.
     * @param field
     *            Field reference.
     * @return Inferred field type.
     */
    private Type getFieldType(Object instance, Field field) {

        // TODO this is repeated on the client
        // For this and the method below, we can check the following candidates:
        // org.eclipse.epsilon.common.util.ReflectionUtil
        // org.eclipse.xtext.util.ReflectionUtil
        // org.reflections.ReflectionUtils
        // io.swagger.util.ReflectionUtils
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof TypeVariable) {
            // field has a generic type definition, find the actual type on the superclass
            ParameterizedType superClassType = (ParameterizedType) component.getClass().getGenericSuperclass();
            Type[] superClassArgs = superClassType.getActualTypeArguments();
            TypeVariable<?>[] superClassTypes = ((Class<?>) superClassType.getRawType()).getTypeParameters();

            assert superClassArgs.length == superClassTypes.length;
            for (int i = 0; i < superClassArgs.length; i++) {
                TypeVariable<?> var = superClassTypes[i];

                // TODO just checking the name may not be enough if it changes somewhere in the
                // hierarchy
                if (genericFieldType.getTypeName().equals(var.getTypeName())) {
                    // type of generic field found in the superclass type arguments
                    return superClassArgs[i];
                }
            }

            throw new AssertionError("Generic field type could not be determined: " + field);
        }

        // just return the type for the field
        return field.getType();
    }

    /**
     * Retrieves the fields for the provided Class and all its superclasses.
     *
     * @param objectClass
     *            Class reference.
     * @return All Fields going up in the class hierarchy.
     */
    private List<Field> combineFields(Class<?> objectClass) {

        // TODO this is repeated on the client
        List<Field> combinedFields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));
        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != Object.class) {
            // recursive call to superclass
            combinedFields.addAll(combineFields(superClass));
        }
        return combinedFields;
    }

    /**
     * Returns an icon URI given a certain icon category id and an Item Id so the icon can reflect the state of such
     * Item.
     *
     * @param iconId Id of the icon category.
     * @param state Id of the item.
     * @return URI to retrieve the relevant icon.
     */
    private URI iconURL(String category, State state) {
        if (category == null) {
            return null;
        }
        String path = "/icon/" + category;
        String query = null;
        if (state != null) {
            try {
                if (!state.equals(UnDefType.NULL)) {
                    query = "state=" + URLEncoder.encode(state.toFullString(), StandardCharsets.UTF_8.toString());
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("Unsupported encoding", e);
                return null;
            }
        }
        try {
            return new URI(null, null, path, query, null);
        } catch (URISyntaxException e) {
            logger.error("Invalid URI syntax", e);
            return null;
        }
    }

    /**
     * Returns a tagged union after selecting an element that matches the given state.
     *
     * @param tu Tagged union.
     * @param state State value to be selected.
     * @return Tagged union with the state selected.
     */
    private Object taggedUnion(TaggedUnion<String, String> tu, State state) {
        if (state instanceof UnDefType) {
            tu.setIndex(TaggedUnion.NONE);
        } else {
            tu.setSelectedKey(state.toFullString());
        }
        return tu;
    }
}
