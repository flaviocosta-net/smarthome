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

import java.util.Map;

import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

/**
 * Defines common methods for all Components. This is the most general entity
 * contained in a sitemap.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 */
public interface Component<D> {

    /**
     * Returns the Id of the component.
     *
     * @return Component Id.
     */
    String getId();

    /**
     * Returns the style for the component.
     *
     * @return Map of properties and their assigned values.
     */
    Map<String, StylePropertyValue> getStyleMap();

    /**
     * Sets the value for a style property.
     *
     * @param name Property name.
     * @param value New property value.
     */
    void setStyle(String name, StylePropertyValue value);

    /**
     * Returns the defined type for the component.
     *
     * @return Component type.
     */
    ComponentType getType();

    /**
     * Returns the data defined for the component.
     *
     * @return Component data.
     */
    D getData();
}
