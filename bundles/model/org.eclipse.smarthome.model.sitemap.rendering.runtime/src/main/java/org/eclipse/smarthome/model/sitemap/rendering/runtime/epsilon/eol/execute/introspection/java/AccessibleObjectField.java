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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.introspection.java;

import java.lang.reflect.Field;

import org.eclipse.epsilon.eol.execute.introspection.java.ObjectField;

/**
 * This implementation is needed because {@link ObjectField} does not expose public methods or constructions to set
 * the object and field references, but also because we want to make the field accessible (e.g. in case it has
 * private visibility).
 *
 * @author Flavio Costa - Initial contribution
 */
class AccessibleObjectField extends ObjectField {

    /**
     * Create a new instance.
     *
     * @param object Object containing the field.
     * @param field Field declaration.
     */
    AccessibleObjectField(Object object, Field field) {
        this.object = object;
        this.field = field;
        this.field.setAccessible(true);
    }
}