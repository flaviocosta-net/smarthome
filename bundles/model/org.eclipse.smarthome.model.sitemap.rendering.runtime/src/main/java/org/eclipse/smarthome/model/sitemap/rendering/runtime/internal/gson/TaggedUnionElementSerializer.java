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

import org.eclipse.smarthome.core.library.sets.OrderedSet;
import org.eclipse.smarthome.core.library.sets.TaggedUnion.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson serializer for TaggedUnion elements.
 *
 * @author Flavio Costa - Initial contribution.
 */
public class TaggedUnionElementSerializer implements JsonSerializer<OrderedSet<Element<String, String>>> {

    @Override
    public JsonElement serialize(OrderedSet<Element<String, String>> src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        src.forEach(e -> jsonObject.addProperty(e.getKey(), e.getValue()));
        return jsonObject;
    }
}