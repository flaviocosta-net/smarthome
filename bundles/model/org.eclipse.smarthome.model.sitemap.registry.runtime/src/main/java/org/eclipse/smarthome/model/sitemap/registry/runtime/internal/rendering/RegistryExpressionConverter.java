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
package org.eclipse.smarthome.model.sitemap.registry.runtime.internal.rendering;

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ExpressionConverter;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ValueReferenceExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation to convert Registry sitemap expressions into EOL expressions.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = ExpressionConverter.class, property = "sitemap.type=registry")
public class RegistryExpressionConverter implements ExpressionConverter {

    @Override
    public Expression convert(ValueReference<?, ?> valueRef, Object object) throws EolRuntimeException {
        if (object instanceof String) {
            return new StringLiteral((String) object);
        }
        if (object instanceof ValueReference) {
            return new ValueReferenceExpression((ValueReference<?, ?>) object);
        }
        throw new UnsupportedOperationException("Cannot convert object of type " + object.getClass());
    }
}
