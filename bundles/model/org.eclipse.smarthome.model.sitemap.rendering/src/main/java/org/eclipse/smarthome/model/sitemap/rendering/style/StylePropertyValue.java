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
 * Possible or assigned value of a style property.
 *
 * @author Flavio Costa - Initial implementation and API
 */
public interface StylePropertyValue {

    /**
     * Returns the value of the property as a String.
     *
     * @return String representation of the value.
     */
    public String asString();
}
