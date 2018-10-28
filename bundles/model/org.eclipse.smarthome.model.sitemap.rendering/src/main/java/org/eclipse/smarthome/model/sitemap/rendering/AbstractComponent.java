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
package org.eclipse.smarthome.model.sitemap.rendering;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

/**
 * Abstract Component implementation. It provides a standard implementation for
 * Component subclasses.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 */
public abstract class AbstractComponent<D> implements Component<D> {

    /**
     * Sitemap Id.
     */
    private String id;

    /**
     * Component type.
     */
    private ComponentType type;

    /**
     * Component style.
     */
    private transient Map<String, StylePropertyValue> style;

    /**
     * Default, no-args constructor.
     */
    public AbstractComponent() {
        // used for certain purposes such as deserialization
    }

    /**
     * Constructor that sets a specific id and type.
     *
     * @param id Component Id.
     * @param type Component type, which must be compatible with the actual implementing class.
     */
    public AbstractComponent(String id, ComponentType type) {
        if (id == null) {
            throw new NullPointerException();
        }
        if (!type.getImplementingClass().equals(this.getClass())) {
            throw new IllegalArgumentException("Incompatible type provided: " + type);
        }
        this.id = id;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public Map<String, StylePropertyValue> getStyleMap() {
        if (style == null) {
            // avoids an NPE below
            return null;
        }
        return Collections.unmodifiableMap(style);
    }

    @Override
    public void setStyle(String name, StylePropertyValue value) {
        if (style == null) {
            style = new HashMap<>();
        }
        style.put(name, value);
    }

    @Override
    public String toString() {
        return String.format("%s(%s) id=%s data={%s}", this.getClass().getSimpleName(), getType(), getId(), getData());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbstractComponent)) {
            return false;
        }
        AbstractComponent<?> other = (AbstractComponent<?>) obj;
        if (this.id == null || other.id == null) {
            return this == other;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
