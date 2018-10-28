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

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * A value reference that points to a {@link Item}.
 *
 * @author Flavio Costa - Initial contribution
 */
public class ItemValueReference implements ValueReference<Item, State> {

    /**
     * Data source for this value reference.
     */
    private final Item item;

    /**
     * Constructor that receives an Item.
     *
     * @param item Data source for this value reference.
     */
    public ItemValueReference(Item item) {
        this.item = item;
    }

    @Override
    public String getId() {
        return item.getUID();
    }

    @Override
    public Item getSource() {
        return item;
    }

    @Override
    public String getSourceType() {
        return item.getType();
    }

    @Override
    public String getLabel() {
        return item.getLabel();
    }

    @Override
    public State getValue() {
        return item.getState();
    }

    @Override
    public String getCategory() {
        return item.getCategory();
    }

    @Override
    public Type getVariableType() {
        return Type.ITEM;
    }

}
