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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.smarthome.model.core.EventType;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.model.core.ModelRepositoryChangeListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SitemapDefinitionProvider} implementation that works with sitemap definition provided as EMF models.
 *
 * This class does not use Declarative Services annotations because it needs to be configured by other bundles for the
 * requirements of a specific sitemap implementation - specific instanced need to be configured via an XML file in
 * OSGI-INF.
 *
 * @author Flavio Costa - Initial contribution
 */
public class EMFSitemapDefinitionProvider implements SitemapDefinitionProvider, ModelRepositoryChangeListener {

    /**
     * Component property that determines the sitemap type.
     */
    public static final String PROP_SITEMAP_TYPE = "sitemap.type";

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(EMFSitemapDefinitionProvider.class);

    /**
     * ESH model repository.
     */
    private ModelRepository modelRepo = null;

    /**
     * Cache of definition models.
     */
    private final Map<String, EObject> sitemapModelCache = new ConcurrentHashMap<>();

    /**
     * Sitemap type for this provider.
     */
    private String sitemapType;

    protected void setModelRepository(ModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    protected void unsetModelRepository(ModelRepository modelRepo) {
        this.modelRepo = null;
    }

    @Activate
    protected void activate(Map<String, Object> properties) {
        this.sitemapType = (String) properties.get(PROP_SITEMAP_TYPE);
        if (this.sitemapType == null) {
            logger.error("Configuration error, property '{}' is not defined", PROP_SITEMAP_TYPE);
        } else {
            if (modelRepo == null) {
                logger.error("ModelRepository reference not available, please bind it via the Component description");
            } else {
                refreshSitemapModels();
                modelRepo.addModelRepositoryChangeListener(this);
                logger.debug("Sitemap Definition available for type '{}'", this.sitemapType);
            }
        }
    }

    @Deactivate
    protected void deactivate() {
        if (modelRepo != null) {
            modelRepo.removeModelRepositoryChangeListener(this);
        }
        sitemapModelCache.clear();
    }

    @Override
    public Object getSitemapDefinition(String sitemapName) {
        String filename = sitemapName + getSitemapFileExt();
        EObject sitemap = sitemapModelCache.get(filename);
        if (sitemap == null) {
            logger.warn("Sitemap '{}' cannot be found", sitemapName);
        }
        return sitemap;
    }

    @Override
    public String getSitemapType() {
        return this.sitemapType;
    }

    @Override
    public String getSitemapFileExt() {
        return '.' + getSitemapType();
    }

    @Override
    public Set<String> getSitemapNames() {
        return sitemapModelCache.keySet().stream().map(name -> StringUtils.removeEnd(name, getSitemapFileExt()))
                .collect(Collectors.toSet());
    }

    @Override
    public void modelChanged(String modelName, EventType type) {
        if (modelName.endsWith(getSitemapFileExt())) {
            if (type == EventType.REMOVED) {
                sitemapModelCache.remove(modelName);
            } else {
                putModelOnCache(modelName);
            }
        }
    }

    /**
     * Clear the cache and load all sitemaps of the given type from the model repository into the cache.
     */
    private void refreshSitemapModels() {

        sitemapModelCache.clear();
        Iterable<String> sitemapNames = modelRepo.getAllModelNamesOfType(getSitemapType());
        for (String sitemapName : sitemapNames) {
            putModelOnCache(sitemapName);
        }
    }

    /**
     * Add a model from the repository to the cache on this class.
     *
     * @param sitemapName Sitemap name.
     */
    private void putModelOnCache(String sitemapName) {
        sitemapModelCache.put(sitemapName, modelRepo.getModel(sitemapName));
    }
}
