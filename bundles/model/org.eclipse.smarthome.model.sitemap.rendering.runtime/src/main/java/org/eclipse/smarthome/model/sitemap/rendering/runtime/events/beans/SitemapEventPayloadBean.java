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

import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;

/**
 * Bean to hold data from sitemap event payloads.
 *
 * @author Flavio Costa - Initial contribution
 */
public class SitemapEventPayloadBean {

    /**
     * Sitemap id.
     */
    private String id;

    /**
     * Public default constructor for deserialization.
     */
    public SitemapEventPayloadBean() {
    }

    /**
     * Constructor that sets the bean data.
     *
     * @param sitemap Sitemap instance.
     */
    public SitemapEventPayloadBean(Sitemap sitemap) {
        this.id = sitemap.getId();
    }

    /**
     * Returns the sitemap Id.
     *
     * @return Id of the sitemap.
     */
    public String getId() {
        return id;
    }
}
