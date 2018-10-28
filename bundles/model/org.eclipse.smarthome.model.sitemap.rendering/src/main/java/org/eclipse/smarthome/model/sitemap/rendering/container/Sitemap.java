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

import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap.Data;

/**
 * Sitemap model implementation. The sitemap container holds the containers
 * displayed in the root view of a sitemap.
 *
 * @author Flavio Costa
 */
public class Sitemap extends AbstractNavigableContainer<Data, Container<?, ?>> {

    /**
     * Stores data for the Sitemap.
     */
    public static class Data extends AbstractNavigableContainer.Data {

        /**
         * TODO Implementation to be reviewed.
         */
        // private String lang;
    }

    /**
     * Stores the entity tag for this sitemap.
     */
    private final transient int eTag = Long.hashCode(System.nanoTime());

    @Override
    public int hashCode() {
        return eTag;
    }
}
