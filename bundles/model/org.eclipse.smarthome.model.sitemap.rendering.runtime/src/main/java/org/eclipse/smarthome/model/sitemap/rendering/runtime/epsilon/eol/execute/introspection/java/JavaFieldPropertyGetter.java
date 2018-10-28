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

import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.java.ObjectField;

/**
 * Gets a Java property by access the field directly, rather than {@link JavaPropertyGetter} that relies on an explicit
 * getter method.
 *
 * @author Flavio Costa - Initial contribution
 */
public class JavaFieldPropertyGetter extends JavaPropertyGetter {

    /**
     * Looks for a matching field for the property on the target object class or any of its superclasses.
     *
     * @param object Object to inspect for any declared fields.
     * @param objectClass Class or superclass of the object being inspected.
     * @return Reference to the matching field, or null if none can be found in the class hierarchy.
     * @throws EolInternalException Thrown instead of any underlying SecurityException.
     */
    private ObjectField getPropertyFieldFor(Object object, Class<?> objectClass, String property)
            throws EolInternalException {
        try {
            // search for a match on this class
            for (Field field : objectClass.getDeclaredFields()) {
                if (field.getName().equals(property)) {
                    return new AccessibleObjectField(object, field);
                }
            }

            // reached the topmost type, field is not found
            if (objectClass.equals(Object.class)) {
                return null;
            }

            // look for the field on its superclass
            return getPropertyFieldFor(object, objectClass.getSuperclass(), property);

        } catch (SecurityException e) {
            // security violation
            throw new EolInternalException(e);
        }
    }

    @Override
    public Object invoke(Object object, String property) throws EolRuntimeException {

        ObjectField field = getPropertyFieldFor(object, object.getClass(), property);
        if (field == null) {
            // if couldn't find a matching field for the property, defaults to a getX() call
            return super.invoke(object, property);
        } else {
            // try to set the value directly on the field
            try {
                return field.getValue();
            } catch (Exception e) {
                // exception setting the field value
                throw new EolInternalException(e);
            }
        }
    }
}
