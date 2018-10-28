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
package org.eclipse.smarthome.model.sitemap.rendering.style;

/**
 * Possible or assigned String value of a style property.
 *
 * @author Flavio Costa - Initial implementation
 */
public class StringStylePropertyValue implements StylePropertyValue {

    /**
     * Assigned value of the property.
     */
    private final String value;

    /**
     * Creates a new property value from a String.
     *
     * @param value Assigned value of the property.
     */
    public StringStylePropertyValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringStylePropertyValue) {
            return value.equals(((StringStylePropertyValue) obj).value);
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public String asString() {
        return value;
    }
}
