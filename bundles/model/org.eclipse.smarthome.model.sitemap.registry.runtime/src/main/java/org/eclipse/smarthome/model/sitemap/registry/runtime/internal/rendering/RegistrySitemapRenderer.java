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
package org.eclipse.smarthome.model.sitemap.registry.runtime.internal.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.smarthome.core.common.registry.Identifiable;
import org.eclipse.smarthome.core.common.registry.Registry;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ExpressionConverter;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the rendering model for 'registry' sitemaps.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = SitemapRenderingProvider.class, immediate = true)
public class RegistrySitemapRenderer implements SitemapRenderingProvider {

    /**
     * Names for the provides registries.
     */
    private static enum RegistryName {
    ITEMS,
    THINGS
    }

    /**
     * Supported sitemap type.
     */
    private static final String SITEMAP_TYPE = "registry";

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(RegistrySitemapRenderer.class);

    /**
     * Map of available registries.
     */
    private final Map<RegistryName, Registry<? extends Identifiable<?>, ?>> registries = new HashMap<>();

    /**
     * Rendering driver instance.
     */
    private SitemapRenderingDriver driver;

    /**
     * Expression converter instance.
     */
    private ExpressionConverter converter;

    @Reference
    public void setItemRegistry(ItemRegistry itemRegistry) {
        registries.put(RegistryName.ITEMS, itemRegistry);
    }

    public void unsetItemRegistry(ItemRegistry itemRegistry) {
        registries.remove(RegistryName.ITEMS, itemRegistry);
    }

    @Reference
    public void setThingRegistry(ThingRegistry thingRegistry) {
        registries.put(RegistryName.THINGS, thingRegistry);
    }

    public void unsetThingRegistry(ThingRegistry thingRegistry) {
        registries.remove(RegistryName.THINGS, thingRegistry);
    }

    @Reference(target = "(sitemap.type=registry)")
    public void setExpressionConverter(ExpressionConverter converter) {
        this.converter = converter;
    }

    public void unsetExpressionConverter(ExpressionConverter converter) {
        this.converter = null;
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
        if (model == null) {
            // load model for the transformation
            Registry<? extends Identifiable<?>, ?> registry = registries
                    .get(RegistryName.valueOf(sitemapName.toUpperCase()));
            if (registry == null) {
                logger.error("Registry '{}' does not exist", sitemapName);
                return null;
            }
            // configure the transformation settings
            Class<?>[] interfaces = registry.getClass().getInterfaces();
            assert interfaces.length == 1;
            RenderingWorkflow workflow = driver.createWorkflow(this, sitemapName, registry);
            workflow.addGlobalVariable(EolFrame.create(VarName.CONVERTER, converter));
            ModuleSettings<RenderingModel> settings = workflow.addModule(interfaces[0].getSimpleName(), "etl");
            // cannot just pass the registry instance because the baseItem of GroupIems
            // will be inadvertently added to the model via reflection
            settings.addModel("Registry", registry);
            model = driver.renderSitemap(workflow);
        }
        return model;
    }

    @Override
    public Set<String> getSitemapNames() {
        return registries.keySet().stream().map(RegistryName::name).map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public String getSitemapType() {
        return SITEMAP_TYPE;
    }
}
