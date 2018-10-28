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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types;

import java.util.Collections;
import java.util.Map;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.MappingReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * A value reference that points to a {@link Item} woth associated mappings.
 *
 * @author Flavio Costa - Initial contribution
 */
public class ItemMappingReference implements MappingReference<Item, State> {

    /**
     * Reference to the selected (active) mapping.
     */
    private final ItemValueReference value;

    /**
     * Mappings associated with this reference.
     */
    private final Map<String, ?> mappings;

    /**
     * Constructor that receives an Item reference and associated mappings.
     *
     * @param value Reference to the selected (active) mapping.
     * @param mappings Mappings associated with this reference.
     */
    public ItemMappingReference(ItemValueReference value, Map<String, ?> mappings) {
        this.value = value;
        this.mappings = Collections.unmodifiableMap(mappings);
    }

    @Override
    public ValueReference<Item, State> getValue() {
        return value;
    }

    @Override
    public Map<String, ?> getMappings() {
        return mappings;
    }

}
