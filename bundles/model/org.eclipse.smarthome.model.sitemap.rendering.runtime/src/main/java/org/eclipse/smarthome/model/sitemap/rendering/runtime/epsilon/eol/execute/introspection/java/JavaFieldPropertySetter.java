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
import org.eclipse.epsilon.eol.execute.introspection.IReflectivePropertySetter;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;
import org.eclipse.epsilon.eol.execute.introspection.java.ObjectField;

/**
 * Sets a Java property by access the field directly, rather than {@link JavaPropertySetter} that relies on an explicit
 * setter method.
 *
 * @author Flavio Costa - Initial contribution
 */
public class JavaFieldPropertySetter extends JavaPropertySetter implements IReflectivePropertySetter {

    /**
     * Looks for a matching field for the property on the target object class or any of its superclasses.
     *
     * @param objectClass Class to inspect for any declared fields.
     * @return Reference to the matching field, or null if none can be found in the class hierarchy.
     * @throws EolInternalException Thrown instead of any underlying SecurityException.
     */
    private ObjectField getPropertyFieldFor(Class<?> objectClass) throws EolInternalException {
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
            return getPropertyFieldFor(objectClass.getSuperclass());

        } catch (SecurityException e) {
            // security violation
            throw new EolInternalException(e);
        }
    }

    @Override
    public void invoke(Object value) throws EolRuntimeException {

        ObjectField field = getPropertyFieldFor(object.getClass());
        if (field == null) {
            // if couldn't find a matching field for the property, defaults to a setX() call
            super.invoke(value);
        } else {
            // try to set the value directly on the field
            try {
                field.setValue(value);
            } catch (Exception e) {
                // exception setting the field value
                throw new EolInternalException(e);
            }
        }
    }
}
