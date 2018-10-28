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

import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;

/**
 * Bean to hold data from atom event payloads. It represents an atom data change, from an previous (old) value to a new
 * one.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <D> Type of atom data.
 */
public class AtomEventPayloadBean<D> {

    /**
     * Previous atom data.
     */
    private D previousData;

    /**
     * New atom data.
     */
    private D newData;

    /**
     * Public default constructor for deserialization.
     */
    public AtomEventPayloadBean() {
    }

    /**
     * Constructor that sets the bean data.
     *
     * @param newData Previous atom data.
     * @param previousData New atom data.
     */
    public AtomEventPayloadBean(D newData, D previousData) {
        this.newData = newData;
        this.previousData = previousData;
    }

    /**
     * Constructor that sets the bean data.
     *
     * @param atom Atom instance.
     * @param previousData New atom data.
     */
    public AtomEventPayloadBean(Atom<D> atom, D previousData) {
        this(atom.getData(), previousData);
    }

    /**
     * Returns the previous atom data.
     *
     * @return Previous atom data.
     */
    public D getPreviousData() {
        return previousData;
    }

    /**
     * Returns the new atom data.
     *
     * @return New atom data.
     */
    public D getNewData() {
        return newData;
    }
}
