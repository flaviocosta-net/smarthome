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

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * Calculates expressions inside an EOL module, allowing the expressions in the definition model to be converted to the
 * respective final values in the rendering model.
 *
 * @author Flavio Costa - Initial contribution.
 */
public interface ExpressionConverter {

    /**
     * Convert an implementation-specific object from into an EOL expression.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param object Input object.
     * @return Resulting expression.
     * @throws EolRuntimeException
     */
    Expression convert(ValueReference<?, ?> valueRef, Object object) throws EolRuntimeException;
}
