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
package org.eclipse.smarthome.model.sitemap.definition.runtime;

import java.util.Set;

/**
 * Provides the definition model for a given sitemap type.
 *
 * @author Flavio Costa - Initial contribution
 */
public interface SitemapDefinitionProvider {

    /**
     * Retrieves a sitemap definition model.
     *
     * @param sitemapName Name of the sitemap.
     * @return Object model tree, null if it is not found.
     */
    Object getSitemapDefinition(String sitemapName);

    /**
     * Returns the names of all available sitemaps for that sitemap type.
     *
     * @return Names of provided sitemaps.
     */
    Set<String> getSitemapNames();

    /**
     * Returns the type of the sitemaps provided by this sitemap definition provider.
     *
     * @return Type of sitemap.
     */
    String getSitemapType();

    /**
     * Returns the file extension for models loaded into this provider.
     *
     * @return File extension for sitemaps of this type.
     */
    String getSitemapFileExt();
}
