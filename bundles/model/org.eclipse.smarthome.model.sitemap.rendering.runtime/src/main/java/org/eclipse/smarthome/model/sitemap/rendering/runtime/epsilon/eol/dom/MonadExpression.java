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

import org.eclipse.epsilon.common.module.ModuleMarker;
import org.eclipse.epsilon.common.module.ModuleMarker.Severity;
import org.eclipse.epsilon.eol.compile.context.EolCompilationContext;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.types.EolPrimitiveType;

/**
 * EOL expression for monads. Monads return a given value if its state evaluates to true.
 *
 * @author Flavio Costa - Initial contribution
 */
public class MonadExpression extends Expression {

    /**
     * Monad state.
     */
    private final Expression state;

    /**
     * Monad result.
     */
    private final StringLiteral result;

    /**
     * Creates a new monad expression.
     *
     * @param state Boolean state.
     * @param result Resulting value, if the state is true.
     */
    public MonadExpression(Expression state, StringLiteral result) {
        this.state = state;
        this.result = result;
        state.setParent(this);
        this.getChildren().add(state);
        result.setParent(this);
        this.getChildren().add(result);
    }

    @Override
    public Object execute(IEolContext context) throws EolRuntimeException {
        Object currentState = state.execute(context);
        if (currentState instanceof Boolean) {
            return (Boolean) currentState ? result.getValue() : null;
        }
        throw new EolRuntimeException("Monad state should be Boolean, but " + currentState.toString()
                + " was returned instead: " + currentState.getClass());
    }

    @Override
    public void compile(EolCompilationContext context) {
        state.compile(context);
        if (state.hasResolvedType() && state.getResolvedType() != EolPrimitiveType.Boolean) {
            context.getMarkers().add(new ModuleMarker(state, "State must be a Boolean", Severity.Error));
        }
        resolvedType = EolPrimitiveType.String;
    }

}
