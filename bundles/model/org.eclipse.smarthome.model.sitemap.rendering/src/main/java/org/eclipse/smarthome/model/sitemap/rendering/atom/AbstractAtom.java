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
package org.eclipse.smarthome.model.sitemap.rendering.atom;

import org.eclipse.smarthome.model.sitemap.rendering.AbstractComponent;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;

/**
 * Abstract Atom implementation. It provides a standard implementation for Atom
 * subclasses.
 *
 * @author Flavio Costa`
 *
 * @param <D>Type of the data field.
 */
public abstract class AbstractAtom<D> extends AbstractComponent<D> implements Atom<D> {

    /**
     * Data associated to this atom - it is marked as transient because the data needs to be sent separately via the SSE
     * mechanism.
     */
    private transient D data;

    /**
     * Default, no-args constructor.
     */
    public AbstractAtom() {
        super();
    }

    /**
     * Constructor that sets a specific type.
     *
     * @param id Component Id.
     * @param type Component type, which must be compatible with the actual implementing class.
     */
    public AbstractAtom(String id, ComponentType type) {
        super(id, type);
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    public void setData(D data) {
        this.data = data;
    }

    @Override
    public <T> T as(Class<T> type) throws IllegalArgumentException {
        D data = getData();
        if (data == null) {
            return null;
        }
        if (String.class.equals(type)) {
            return type.cast(data.toString());
        }

        throw new IllegalArgumentException("Unsupported type conversion from " + getData().getClass() + " to " + type);
    }
}
