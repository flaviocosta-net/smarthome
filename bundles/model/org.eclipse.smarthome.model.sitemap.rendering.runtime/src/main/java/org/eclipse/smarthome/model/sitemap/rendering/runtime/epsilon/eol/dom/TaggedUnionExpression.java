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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.epsilon.eol.compile.context.EolCompilationContext;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.sets.ArraySetTaggedUnion;
import org.eclipse.smarthome.core.library.sets.TaggedUnion;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.core.types.StateOption;
import org.eclipse.smarthome.core.types.UnDefType;

/**
 * EOL expression for tagged unions. A tagged union consists of a set where one element may be selected.
 *
 * @author Flavio Costa - Initial contribution
 */
public class TaggedUnionExpression extends Expression {

    /**
     * Value reference that indicates the tagged element.
     */
    private final ValueReferenceExpression valueReference;

    /**
     * Mappings on the tagged union.
     */
    private final Map<Expression, Expression> mappings;

    /**
     * Creates a new tagged union expression.
     *
     * @param valueReference Reference to the selected key.
     * @param valueReference Elements consisting of key/value pairs.
     */
    public TaggedUnionExpression(ValueReferenceExpression valueReference, Map<Expression, Expression> mappings) {
        this.valueReference = valueReference;
        valueReference.setParent(this);
        this.getChildren().add(valueReference);
        this.mappings = mappings;
        for (Map.Entry<Expression, Expression> e : mappings.entrySet()) {
            Expression key = e.getKey();
            Expression value = e.getKey();
            key.setParent(this);
            this.getChildren().add(key);
            value.setParent(this);
            this.getChildren().add(value);
        }
    }

    @Override
    public Object execute(IEolContext context) throws EolRuntimeException {

        TaggedUnion<String, String> tu = new ArraySetTaggedUnion<String, String>(updateMappings(context));

        // indicate the selected element
        State state = (State) valueReference.execute(context);
        if (!(state instanceof UnDefType)) {
            tu.setSelectedKey(state.toFullString());
        }

        return tu;
    }

    /**
     * Update the tagged union elements from the provided mapping sources.
     *
     * @param context Execution context.
     * @return Map of key/value pairs.
     */
    private Map<String, String> updateMappings(IEolContext context) {
        if (!this.mappings.isEmpty()) {
            // use the mappings explicitly defined on the sitemap
            return this.mappings.entrySet().stream()
                    .collect(Collectors.toMap(e -> execute(e, context, true), e -> execute(e, context, false)));
        }

        Item item = (Item) this.valueReference.getValueReference().getSource();
        StateDescription sd = item.getStateDescription();
        if (sd != null) {
            // use mappings from the state description
            List<StateOption> options = sd.getOptions();
            if (options != null && !options.isEmpty()) {
                // options defined in the StateDescription
                return options.stream().collect(Collectors.toMap(StateOption::getLabel, StateOption::getValue));
            }
        }
        State state = item.getState();
        if (state instanceof UnDefType) {
            return Collections.emptyMap();
        }
        if (state instanceof Enum) {
            // options given by an Enum state type
            Enum<?> stateEnum = (Enum<?>) state;
            return Arrays.stream(stateEnum.getDeclaringClass().getEnumConstants())
                    .collect(Collectors.toMap(Enum::name, Enum::toString));
        }
        throw new UnsupportedOperationException(
                String.format("State '%s' of item '%s' does not support options", state, item.getName()));
    }

    /**
     * Execute the expression for a key or value of an entry.
     *
     * @param entry Entry providing the expression to be executed.
     * @param context Execution context.
     * @param key True if the key expression should be executed, false if the value expression should be executed.
     * @return Resulting String.
     */
    private String execute(Map.Entry<Expression, Expression> entry, IEolContext context, boolean key) {
        try {
            return (String) (key ? entry.getKey() : entry.getValue()).execute(context);
        } catch (EolRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void compile(EolCompilationContext context) {
        resolvedType = EolAnyType.Instance;
    }

}
