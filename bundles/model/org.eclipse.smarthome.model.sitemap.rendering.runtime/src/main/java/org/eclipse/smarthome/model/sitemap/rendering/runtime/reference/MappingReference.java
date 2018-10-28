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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.reference;

import java.util.Map;

/**
 * A mapping reference is a reference on a sitemap for some source of data that has mapping information associated with
 * it. The object contains any mapping defined, and a {@link ValueReference} that indicates which map element is active
 * or selected.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <E> Type of the value source.
 * @param <V> Type of value.
 */
public interface MappingReference<E, V> {

    /**
     * Returns a value reference associated with the mapping.
     *
     * @return Active or selected value.
     */
    ValueReference<E, V> getValue();

    /**
     * Returns the defined mapping.
     * 
     * @return Map with elements (possible options).
     */
    Map<String, ?> getMappings();

}
