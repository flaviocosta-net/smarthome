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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson serializer for object hierarchies with inherited fields (i.e. sitemap Component objects). This custom serializer
 * is needed because, by default, Gson does not handle these cases seamlessly. Gson can serialize private fields, but
 * not if they are defined in superclasses.
 *
 * @author Flavio Costa - Initial contribution.
 */
public class InheritedFieldsSerializer implements JsonSerializer<Object> {
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        List<Field> combinedFields = combineFields(src.getClass());
        for (Field field : combinedFields) {
            if (Modifier.isTransient(field.getModifiers())) {
                // skip transient fields
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(src);
                if (value != null) {
                    jsonObject.add(field.getName(), context.serialize(value, value.getClass()));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * Retrieves the fields for the provided Class and all its superclasses.
     *
     * @param objectClass
     *            Class reference.
     * @return All Fields going up in the class hierarchy.
     */
    private java.util.List<Field> combineFields(Class<?> objectClass) {

        java.util.List<Field> combinedFields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));
        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != Object.class) {
            // recursive call to superclass
            combinedFields.addAll(combineFields(superClass));
        }
        return combinedFields;
    }
}