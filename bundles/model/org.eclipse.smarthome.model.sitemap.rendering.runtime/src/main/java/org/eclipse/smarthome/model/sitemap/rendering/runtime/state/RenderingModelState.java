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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.state;

import java.util.Collection;

/**
 * Represents the rendering model state as a collection of the states of every component that is part of such rendering
 * model. The rendering model state propagates dependency changes to each of the component states.
 *
 * @author Flavio Costa - Initial contribution
 */
public interface RenderingModelState extends StateDependencyChangeListener {

    /**
     * Adds a component state to this rendering model state.
     *
     * @param state State to be added.
     */
    void add(ComponentState<?> state);

    /**
     * Removes a component state to this rendering model state.
     *
     * @param state State to be removed.
     */
    void remove(ComponentState<?> state);

    /**
     * Returns a collection of all component states.
     *
     * @return All component states in this rendering model.
     */
    Collection<ComponentState<?>> getComponentStates();
}
