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

import java.lang.reflect.Type;

import org.eclipse.smarthome.model.sitemap.rendering.style.StringStylePropertyValue;
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson serializer (and deserializer) for StylePropertyValues.
 *
 * @author Flavio Costa - Initial contribution.
 */
public class StylePropertyValueSerializer
        implements JsonSerializer<StylePropertyValue>, JsonDeserializer<StylePropertyValue> {

    @Override
    public JsonElement serialize(StylePropertyValue src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.asString());
    }

    @Override
    public StylePropertyValue deserialize(JsonElement elem, Type typeOfDest, JsonDeserializationContext context)
            throws JsonParseException {
        return new StringStylePropertyValue(elem.getAsString());
    }
}