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
package org.eclipse.smarthome.io.json;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 * JSON data binding service.
 *
 * @author Flavio Costa - Initial contribution.
 */
public interface JsonBindingService {

    /**
     * Serialize an object to Json, returning the result as a String.
     *
     * @param object Object instance to be serialized.
     * @return Json output.
     */
    String toJson(Object object);

    /**
     * Serialize an object to Json, sending the output to the provided {@link Writer}.
     *
     * @param object Object instance to be serialized.
     * @param genericType Generic type definition.
     * @param writer Writer where the Json output will be written to.
     */
    void toJson(Object object, Type genericType, Writer writer);

    /**
     * Deserializes an object from a Json provided as a String.
     *
     * @param json String with the serialized Json.
     * @param genericType Generic type definition.
     * @return Object instance that was deserialized.
     */
    <T> T fromJson(String json, Type genericType);

    /**
     * Deserializes an object from a Json provided as a String.
     *
     * @param json String with the serialized Json.
     * @param type Class of the object to be returned.
     * @return Object instance that was deserialized.
     */
    <T> T fromJson(String json, Class<T> type);

    /**
     * Deserializes an object from a Json provided by a {@link Reader}.
     *
     * @param reader Reader that provides the serialized Json.
     * @param genericType Generic type definition.
     * @return Object instance that was deserialized.
     */
    <T> T fromJson(Reader reader, Type genericType);

    /**
     * Deserializes an object from a Json provided by a {@link Reader}.
     *
     * @param reader Reader that provides the serialized Json.
     * @param type Class of the object to be returned.
     * @return Object instance that was deserialized.
     */
    <T> T fromJson(Reader reader, Class<T> type);

    /**
     * Defines whether the serialized output will be formatted or not.
     *
     * @param prettyPrinting True if formatted output is expected (optimized for human reading), false if the output
     *            Json should be streamlined.
     */
    void setFormattedOutput(boolean prettyPrinting);

    /**
     * Defines the {@link Locale} to be used to produce formatted output.
     *
     * @param locale Locale to be used for locale-specific formatting (e.g. numbers and dates).
     */
    void setLocale(Locale locale);

    /**
     * Defines the date/time format pattern.
     *
     * @param pattern Pattern to format date/time values.
     */
    void setDateFormat(String pattern);
}
