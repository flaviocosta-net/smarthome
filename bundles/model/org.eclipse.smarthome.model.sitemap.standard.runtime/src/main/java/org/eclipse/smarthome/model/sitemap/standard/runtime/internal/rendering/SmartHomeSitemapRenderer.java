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
package org.eclipse.smarthome.model.sitemap.standard.runtime.internal.rendering;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.epl.execute.PatternMatchModel;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapDefinitionProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ExpressionConverter;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.models.java.ReflectiveJavaModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.models.java.DeferredJavaModel;
import org.eclipse.smarthome.model.sitemap.standard.definition.DefinitionPackage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the rendering model for 'smarthome' sitemaps.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = SitemapRenderingProvider.class, immediate = true)
public class SmartHomeSitemapRenderer implements SitemapRenderingProvider {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(SmartHomeSitemapRenderer.class);

    /**
     * Definition model provider.
     */
    private SitemapDefinitionProvider provider;

    /**
     * ESH item registry.
     */
    private ItemRegistry itemRegistry;

    /**
     * EPackage for the sitemap definition model (Xtext).
     */
    private EPackage definitionPackage;

    /**
     * Rendering driver instance.
     */
    private SitemapRenderingDriver driver;

    /**
     * Expression converter instance.
     */
    private ExpressionConverter converter;

    @Reference(target = "(sitemap.type=smarthome)")
    public void setSitemapDefinitionProvider(SitemapDefinitionProvider provider) {
        this.provider = provider;
    }

    public void unsetSitemapDefinitionProvider(SitemapDefinitionProvider provider) {
        this.provider = null;
    }

    @Reference(target = "(sitemap.type=smarthome)")
    public void setExpressionConverter(ExpressionConverter converter) {
        this.converter = converter;
    }

    public void unsetExpressionConverter(ExpressionConverter converter) {
        this.converter = null;
    }

    @Reference
    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemProvider() {
        this.itemRegistry = null;
    }

    @Reference
    protected void setSitemapRenderingDriver(SitemapRenderingDriver driver) {
        this.driver = driver;
    }

    protected void unsetSitemapRenderingDriver(SitemapRenderingDriver driver) {
        this.driver = null;
    }

    @Activate
    void activate() {
        // retrieves the EMF package for the definition model (ETL source)
        this.definitionPackage = EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI);
    }

    @Override
    public RenderingModel getRenderingModel(String sitemapName) {
        RenderingModel model = driver.getRenderingModelFromCache(sitemapName);
        if (model == null) {

            // TODO Check where to implement #347 (idea - use CSS style "display: contents")

            // load models for the transformation
            EObject definition = (EObject) provider.getSitemapDefinition(sitemapName);
            if (definition == null) {
                logger.error("Sitemap definition model '{}' does not exist", sitemapName);
                return null;
            }

            // configure the transformation settings
            RenderingWorkflow workflow = driver.createWorkflow(this, sitemapName, definition);
            ModuleSettings<PatternMatchModel> eplSettings = workflow.addModule("GroupItem.epl");
            ModuleSettings<RenderingModel> etlSettings = workflow.addModule("SmartHomeSitemap.etl");

            // source: Definition model
            IModel definitionModel = createEmfModel("Definition", definition.eResource(), definitionPackage);
            eplSettings.addModel(definitionModel);

            // source: Items that are members of Groups
            ReflectiveJavaModel itemsModel = new ReflectiveJavaModel("Items", itemRegistry);
            eplSettings.addModel(itemsModel);

            // GroupItems is generated by EPL and used as an input for ETL
            DeferredJavaModel<PatternMatchModel> groupItemsModel = new DeferredJavaModel<>("GroupItems", eplSettings,
                    "GroupItemMembersItem", itemsModel.allClasses());
            etlSettings.addModel(groupItemsModel);

            // global variables
            workflow.addGlobalVariable(EolFrame.create(VarName.CONVERTER, converter));

            model = driver.renderSitemap(workflow);
        }
        return model;
    }

    /**
     * Create the in-memory EMF model from a loaded Resource.
     *
     * @param name Name of the model.
     * @param eResource EMF resource.
     * @param ePackages EMF packages.
     * @return EOL model instance.
     */
    private IModel createEmfModel(String name, Resource eResource, EPackage... ePackages) {
        InMemoryEmfModel emfModel = new InMemoryEmfModel(name, eResource, Arrays.asList(ePackages), false);
        // does not allow navigating the ResourceSet, as this would retrieve unrelated models
        emfModel.preventLoadingOfExternalModelElements();
        return emfModel;
    }

    @Override
    public Set<String> getSitemapNames() {
        return provider.getSitemapNames();
    }

    @Override
    public String getSitemapType() {
        return provider.getSitemapType();
    }
}
