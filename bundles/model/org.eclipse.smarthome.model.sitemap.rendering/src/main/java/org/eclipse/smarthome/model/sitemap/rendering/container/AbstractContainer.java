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

import org.eclipse.smarthome.model.sitemap.rendering.AbstractComponent;
import org.eclipse.smarthome.model.sitemap.rendering.Component;

/**
 * Abstract Container implementation. It provides a standard implementation for
 * Container subclasses.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 * @param <C> Type of each component contained in this container.
 */
public abstract class AbstractContainer<D, C extends Component<?>> extends AbstractComponent<D>
        implements Container<D, C> {

    /**
     * Data associated to this container.
     */
    private D data;

    /**
     * Container layout.
     */
    private String layout;

    /**
     * Components in the Container.
     */
    protected List<C> components;

    @Override
    public D getData() {
        return data;
    }

    @Override
    public String getLayout() {
        return layout;
    }

    @Override
    public List<C> getComponents() {
        return components;
    }
}
