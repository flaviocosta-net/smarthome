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

import org.eclipse.epsilon.eol.compile.context.EolCompilationContext;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * EOL expression for value references. A value reference is a pointer to a source of data outside of the sitemap
 * definition (e.g. an Item or a Thing).
 *
 * @author Flavio Costa - Initial contribution
 */
public class ValueReferenceExpression extends Expression {

    /**
     * Value reference.
     */
    private final ValueReference<?, ?> valueRef;

    /**
     * Creates a new value reference expression.
     *
     * @param valueRef Value reference.
     */
    public ValueReferenceExpression(ValueReference<?, ?> valueRef) {
        this.valueRef = valueRef;
    }

    /**
     * Returns the value reference on this expression.
     *
     * @return Value reference this expression points to.
     */
    public ValueReference<?, ?> getValueReference() {
        return valueRef;
    }

    @Override
    public Object execute(IEolContext context) throws EolRuntimeException {
        return valueRef.getValue();
    }

    @Override
    public void compile(EolCompilationContext context) {
        resolvedType = EolAnyType.Instance;
    }
}
