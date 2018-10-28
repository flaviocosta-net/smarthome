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

import org.eclipse.smarthome.model.sitemap.rendering.Component;

/**
 * Defines common methods for all Atoms. An Atom must be an indivisible
 * component, which cannot contain any other elements, other than a single data
 * attribute.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 */
public interface Atom<D> extends Component<D> {

    /**
     * Sets the atom data.
     *
     * @param data Atom data.
     */
    void setData(D data);

    /**
     * Returns the data of the atom as a given Java type.
     *
     * @param type Type to be returned.
     * @return Converted data value.
     * @throws IllegalArgumentException If the data value cannot be converted to the requested type.
     */
    public <T> T as(Class<T> type) throws IllegalArgumentException;
}
