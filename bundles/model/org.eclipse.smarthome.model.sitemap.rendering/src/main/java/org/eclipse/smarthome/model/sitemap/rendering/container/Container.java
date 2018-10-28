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
package org.eclipse.smarthome.model.sitemap.rendering.container;

import java.util.List;

import org.eclipse.smarthome.model.sitemap.rendering.Component;

/**
 * Defines common methods for all Containers. A Container must contain a
 * non-empty list of components.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 * @param <C> Type of each component contained in this container.
 */
public interface Container<D, C extends Component<?>> extends Component<D> {

    /**
     * Returns the layout for the components in the container.
     *
     * @return Container layout.
     */
    String getLayout();

    /**
     * Returns the components in the container.
     *
     * @return List of components in this container.
     */
    public List<C> getComponents();
}
