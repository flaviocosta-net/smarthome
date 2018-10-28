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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;

/**
 * Auxiliary class that implements the common logic to generate a rendering model. As indicated by its name, it "drives"
 * a workflow to transform the definition model (or another sitemap structure/data source) into a rendering model that
 * is served to clients.
 *
 * @author Flavio Costa - Initial contribution
 */
public interface SitemapRenderingDriver {

    /**
     * Retrieves a rendering model currently stored in cache.
     *
     * @param sitemapName Sitemap name.
     * @return Module instance, or null if it's currently not in the cache.
     */
    RenderingModel getRenderingModelFromCache(String sitemapName);

    /**
     * Generates the rendering model for a sitemap, and stores the result into the cache if the rendering operation is
     * successful.
     *
     * @param workflow Settings to be used for the rendering.
     * @return Module instance, or null if it cannot be rendered.
     */
    RenderingModel renderSitemap(RenderingWorkflow workflow);

    /**
     * Create the settings used to control the rendering operation.
     *
     * @param renderingProvider Provider for the sitemap rendering model.
     * @param sitemapName Name of the source model.
     * @param source Source model instance.
     * @return Default rendering settings.
     */
    RenderingWorkflow createWorkflow(@NonNull SitemapRenderingProvider renderingProvider, String sitemapName,
            Object source);
}
