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
package org.eclipse.smarthome.io.json.gson;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.io.json.JsonBindingService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson implementation of a {@link JsonBindingService}.
 *
 * @author Flavio Costa - Initial contribution.
 *
 * @param <T> Type to be serialized or deserialized.
 */
public class GsonBindingService implements JsonBindingService {

    /**
     * Builder for Gson.
     */
    private GsonBuilder builder;

    /**
     * Gson instance used for (de)serialization.
     */
    private Gson gson;

    @Override
    public void toJson(Object object, Type genericType, Writer writer) {
        getGson().toJson(object, genericType, writer);
    }

    @Override
    public String toJson(Object object) {
        return getGson().toJson(object);
    }

    @Override
    public <T> T fromJson(Reader reader, Type genericType) {
        return getGson().fromJson(reader, genericType);
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) {
        return getGson().fromJson(reader, type);
    }

    @Override
    public <T> T fromJson(String json, Type genericType) {
        return getGson().fromJson(json, genericType);
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) {
        return getGson().fromJson(json, type);
    }

    @Override
    public void setFormattedOutput(boolean prettyPrinting) {
        if (prettyPrinting) {
            builder = getGsonBuilder().setPrettyPrinting();
            gson = null;
        }
    }

    @Override
    public void setLocale(Locale locale) {
        // as explained on https://github.com/google/gson/issues/281
        // this is currently not supported by Gson and it would require
        // a custom Type Adapter to be registered here
        throw new UnsupportedOperationException("setLocale() not supported by " + this.getClass());
    }

    @Override
    public void setDateFormat(String pattern) {
        builder = getGsonBuilder().setDateFormat(pattern);
        gson = null;
    }

    /**
     * Registers a set of type adapters into the GsonBuilder.
     *
     * @param entries Entries to be registered.
     */
    protected void registerTypeAdapters(Set<Map.Entry<Class<?>, Object>> entries) {
        entries.forEach(e -> getGsonBuilder().registerTypeAdapter(e.getKey(), e.getValue()));
    }

    /**
     * Checks if the Gson instance has been created, then return a new instance or reuse the existing one.
     *
     * @return Created or previously existing Gson instance.
     */
    protected Gson getGson() {
        if (gson == null) {
            gson = getGsonBuilder().create();
        }
        return gson;
    }

    /**
     * Checks if the GsonBuilder instance has been created, then return a new instance or reuse the existing one.
     *
     * @return Created or previously existing GsonBuilder instance.
     */
    protected GsonBuilder getGsonBuilder() {
        if (builder == null) {
            builder = new GsonBuilder();
        }
        return builder;
    }
}
