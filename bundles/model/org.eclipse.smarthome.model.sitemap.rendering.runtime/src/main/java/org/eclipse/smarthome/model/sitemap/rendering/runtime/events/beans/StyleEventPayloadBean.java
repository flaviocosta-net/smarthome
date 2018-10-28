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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.events.beans;

import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

/**
 * Bean to hold data from style event payloads. It represents a style property that is updated to a new value.
 *
 * @author Flavio Costa - Initial contribution
 */
public class StyleEventPayloadBean {

    /**
     * Style property name.
     */
    private String styleName;

    /**
     * New style property value.
     */
    private StylePropertyValue newValue;

    /**
     * Public default constructor for deserialization.
     */
    public StyleEventPayloadBean() {
    }

    /**
     * Constructor that sets the bean data.
     *
     * @param component Component instance.
     * @param styleName Style property name.
     */
    public StyleEventPayloadBean(Component<?> component, String styleName) {
        this.styleName = styleName;
        this.newValue = component.getStyleMap().get(styleName);
    }

    /**
     * Returns the property name.
     *
     * @return Style property name.
     */
    public String getStyleName() {
        return styleName;
    }

    /**
     * Returns the property value.
     *
     * @return New value for the style property.
     */
    public StylePropertyValue getNewValue() {
        return newValue;
    }
}
