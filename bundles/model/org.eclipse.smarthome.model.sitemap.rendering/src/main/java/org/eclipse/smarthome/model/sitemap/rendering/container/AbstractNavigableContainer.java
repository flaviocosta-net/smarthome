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
import org.eclipse.smarthome.model.sitemap.rendering.container.AbstractNavigableContainer.Data;

/**
 * Abstract Navigable Container implementation. It provides a standard
 * implementation for NavigableContainer subclasses.
 *
 * @author Flavio Costa
 *
 * @param <D> Type of the data field.
 * @param <C> Type of each component contained in this container. Navigable
 *            containers can only contain other containers, but not atoms.
 */
public abstract class AbstractNavigableContainer<D extends Data, C extends Container<?, ?>>
        extends AbstractContainer<D, C> implements NavigableContainer<D, C> {

    /**
     * Implementation of the Data interface for navigable containers. This is made a
     * static class to avoid an "infinite recursion" on Java generics in subclass
     * definitions.
     */
    public static class Data implements NavigableContainer.Data {

        /**
         * Label for the container.
         */
        private TextAtom label;

        /**
         * Icon for the container.
         */
        private IconAtom icon;

        @Override
        public TextAtom getLabel() {
            return label;
        }

        @Override
        public IconAtom getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            if (icon != null) {
                str.append("icon=").append(icon);
            }
            if (label != null) {
                if (str.length() > 0) {
                    str.append(' ');
                }
                str.append("label=").append(label);
            }
            return str.toString();
        }
    }
}
