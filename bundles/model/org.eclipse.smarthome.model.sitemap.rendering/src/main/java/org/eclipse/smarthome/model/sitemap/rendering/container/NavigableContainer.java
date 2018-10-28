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

import org.eclipse.smarthome.model.sitemap.rendering.atom.IconAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.TextAtom;
import org.eclipse.smarthome.model.sitemap.rendering.container.NavigableContainer.Data;

/**
 * Defines a Container subtype which is navigable, meaning that it contains the
 * methods (actually the specific Data definition) with specific information
 * that may be displayed when the user "navigates" into it (e.g. a label).
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 * @param <C> Type of each component contained in this container.
 */
public interface NavigableContainer<D extends Data, C extends Container<?, ?>> extends Container<D, C> {

    /**
     * Returns the data defined for navigable containers.
     */
    public interface Data {

        /**
         * Returns the label for the container.
         *
         * @return Display label.
         */
        TextAtom getLabel();

        /**
         * Returns the icon for the container.
         *
         * @return Icon URI.
         */
        IconAtom getIcon();
    }
}
