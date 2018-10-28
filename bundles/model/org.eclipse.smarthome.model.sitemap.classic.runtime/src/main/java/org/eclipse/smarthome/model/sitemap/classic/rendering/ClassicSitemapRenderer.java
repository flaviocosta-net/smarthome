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
package org.eclipse.smarthome.model.sitemap.classic.rendering;

import java.util.Set;

import org.eclipse.smarthome.model.sitemap.SitemapProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the rendering model for Classic sitemaps (with 'sitemap' file extension).
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = SitemapRenderingProvider.class, immediate = true)
public class ClassicSitemapRenderer implements SitemapRenderingProvider {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(ClassicSitemapRenderer.class);

    /**
     * Provides the sitemap definition.
     */
    private SitemapProvider provider;

    /**
     * Driver to generate the rendering model.
     */
    private SitemapRenderingDriver driver;

    @Reference
    protected void setSitemapProvider(SitemapProvider provider) {
        this.provider = provider;
    }

    protected void unsetSitemapProvider(SitemapProvider provider) {
        this.provider = null;
    }

    @Reference
    protected void setSitemapRenderingDriver(SitemapRenderingDriver driver) {
        this.driver = driver;
    }

    protected void unsetSitemapRenderingDriver(SitemapRenderingDriver driver) {
        this.driver = null;
    }

    @Override
    public RenderingModel getRenderingModel(String sitemapName) {

        RenderingModel model = driver.getRenderingModelFromCache(sitemapName);
        if (model != null) {
            return model;
        }

        org.eclipse.smarthome.model.sitemap.Sitemap definition = provider.getSitemap(sitemapName);
        if (definition == null) {
            logger.warn("Sitemap '{}' cannot be found", sitemapName);
            return null;
        }

        RenderingWorkflow workflow = driver.createWorkflow(this, sitemapName, definition);
        ModuleSettings<RenderingModel> settings = workflow.addModule("SitemapRendering.etl");
        settings.addModel("Definition", definition.eResource());
        model = driver.renderSitemap(workflow);
        if (model == null) {
            logger.warn("Sitemap '{}' cannot be rendered", sitemapName);
            return null;
        }
        return model;
    }

    @Override
    public Set<String> getSitemapNames() {
        return provider.getSitemapNames();
    }

    @Override
    public String getSitemapType() {
        return "classic";
    }
}
