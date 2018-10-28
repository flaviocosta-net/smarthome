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
package org.eclipse.smarthome.io.rest.sitemap.internal;

import java.util.Map;

import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

/**
 * TODO document
 *
 * @author Flavio Costa - Initial implementation
 *
 * @param <D> Component data type.
 */
public class ComponentStateDTO<D> {

    public String id;

    public D data;

    public Map<String, StylePropertyValue> style;

    public ComponentStateDTO() {
    }

    @Override
    public String toString() {
        return String.format("id=%s, data=%s", id, data);
    }
}
