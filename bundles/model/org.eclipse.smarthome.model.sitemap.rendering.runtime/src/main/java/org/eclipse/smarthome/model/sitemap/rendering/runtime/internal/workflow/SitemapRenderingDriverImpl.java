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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.workflow;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.dom.Import;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventFilter;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.thing.events.ThingStatusInfoChangedEvent;
import org.eclipse.smarthome.model.core.EventType;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.model.core.ModelRepositoryChangeListener;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state.RenderingModelStateImpl;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ReferenceResolver;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.StateDependencyChangeListener;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.models.DeferredModelReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link SitemapRenderingDriver}, based on {@link EtlModule} for the model transformation.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = { SitemapRenderingDriver.class, EventSubscriber.class })
public class SitemapRenderingDriverImpl
        implements SitemapRenderingDriver, ModelRepositoryChangeListener, EventSubscriber {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(SitemapRenderingDriverImpl.class);

    /**
     * Subscribers for sitemap events.
     */
    private final Set<SitemapEventSubscriber> subscribers = Collections.synchronizedSet(new HashSet<>());

    /**
     * ESH model repository.
     */
    private ModelRepository modelRepo;

    /**
     * Reference resolver instance.
     */
    private ReferenceResolver reference;

    /**
     * Cache for rendering models.
     */
    private Map<String, RenderingModel> renderingModelCache = new ConcurrentHashMap<>();

    /**
     * Set of sitemap extensions loaded into this class.
     */
    private final Set<String> sitemapExtensions = new HashSet<>();

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    protected void addSubscriber(SitemapEventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    protected void removeSubscriber(SitemapEventSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Reference
    protected void setModelRepository(ModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    protected void unsetModelRepository(ModelRepository modelRepo) {
        this.modelRepo = null;
    }

    @Reference
    public void setReferenceResolver(ReferenceResolver reference) {
        this.reference = reference;
    }

    public void unsetReferenceResolver(ReferenceResolver reference) {
        this.reference = null;
    }

    @Activate
    void activate() {
        modelRepo.addModelRepositoryChangeListener(this);
        reference.setSitemapRenderingDriver(this);
    }

    @Override
    public RenderingWorkflow createWorkflow(@NonNull SitemapRenderingProvider renderingProvider, String sitemapName,
            Object source) {
        RenderingWorkflow workflow = new RenderingWorkflowImpl(renderingProvider, sitemapName, source);

        // reference resolver is always available
        workflow.addGlobalVariable(EolFrame.create(VarName.REFERENCE, reference));
        // some initially empty variables to track "where we are"
        workflow.addGlobalVariable(EolFrame.create(VarName.SITEMAP));
        workflow.addGlobalVariable(EolFrame.create(VarName.CONTAINER));
        workflow.addGlobalVariable(EolFrame.create(VarName.VALUE_REF));
        // states are collected during the transformation
        RenderingModelState renderingModelState = new RenderingModelStateImpl(subscribers);
        workflow.addGlobalVariable(EolFrame.create(VarName.STATES, renderingModelState));
        return workflow;
    }

    @Override
    public RenderingModel renderSitemap(RenderingWorkflow workflow) {

        String sitemapName = workflow.getDefinitionModelName();

        try {
            ModuleSettings<?> previousModule = null;
            for (ModuleSettings<?> module : workflow.getModules()) {
                IEolModule libraryModule = module.createInstance(previousModule);
                // add implicit imports
                for (String importName : getImplicitImportNames(module.getModuleType())) {
                    addImplicitImport(libraryModule, importName);
                }

                // parse the main ETL script
                if (!module.parse()) {
                    // each parse problem in one line
                    logger.error("Parse errors occured:{}{}", System.lineSeparator(), libraryModule.getParseProblems()
                            .stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
                    return null;
                }

                // adds the models of the current module to the repository
                org.eclipse.epsilon.eol.models.ModelRepository repository = libraryModule.getContext()
                        .getModelRepository();
                for (IModel m : module.getModels()) {
                    if (m instanceof DeferredModelReference) {
                        // resolve the deferred model reference just before using it
                        ((DeferredModelReference<?>) m).resolve();
                    }
                    repository.addModel(m);
                }

                // execute program
                try {
                    module.execute();
                } catch (EolRuntimeException e) {
                    logger.error("Sitemap rendering failed", e);
                    return null;
                }

                previousModule = module;
            }

            if (previousModule == null) {
                int size = workflow.getModules().size();
                logger.error("No modules executed, the workflow contains {} module{}", size, size == 1 ? "" : "s");
                return null;
            }
            // the last module must return a RenderingModel
            RenderingModel model = (RenderingModel) previousModule.getResult();
            logger.debug("Rendering of sitemap '{}' successful", sitemapName);
            renderingModelCache.put(sitemapName, model);
            sitemapExtensions.add(workflow.getSitemapType());
            subscribers.forEach(s -> s.sitemapChanged(EventAction.LOADED, model.getSitemap()));
            return model;
        } finally {
            workflow.dispose();
        }
    }

    /**
     * Determines the modules to be implicitly imported in the transformation.
     *
     * @param moduleType File extension that indicates the EOL module type.
     * @return List of files names for the files to be implicitly imported.
     */
    private Set<String> getImplicitImportNames(String moduleType) {
        switch (moduleType) {
            case "etl":
                return new HashSet<>(Arrays.asList("Rendering.etl"));
            default:
                return Collections.emptySet();
        }
    }

    /**
     * Imports the provided EOL module into the transformation.
     *
     * @param libraryModule Loaded module, to which the new module need to be imported.
     * @param importName Name of the file to be imported - the file is expected to be a resource in the same package as
     *            the {@link SitemapRenderingDriver} class.
     */
    private void addImplicitImport(IEolModule libraryModule, String importName) {
        Import implicitImport = new Import();
        implicitImport.setParent(libraryModule);
        implicitImport.setPathLiteral(new StringLiteral(importName));
        // right now, only EtlModule is supported
        implicitImport.setImportedModule(new EtlModule());
        try {
            URL importURL = SitemapRenderingDriver.class.getResource(importName);
            if (importURL == null) {
                throw new AssertionError("Import not found: " + importName);
            }
            implicitImport.load(importURL.toURI());
            libraryModule.getImports().add(implicitImport);
        } catch (URISyntaxException e) {
            logger.error("Could not import the {} module: {}", importName, e.getMessage());
        }
    }

    @Override
    public RenderingModel getRenderingModelFromCache(String sitemapName) {
        return renderingModelCache.get(sitemapName);
    }

    @Deactivate
    protected void deactivate() {
        if (modelRepo != null) {
            modelRepo.removeModelRepositoryChangeListener(this);
        }
        renderingModelCache = null;
    }

    @Override
    public void modelChanged(String modelName, EventType type) {

        // separates filename.filextension
        String[] nameParts = modelName.split("\\.");

        if (sitemapExtensions.contains(nameParts[1])) {
            // any changes to the definition model invalidate the rendering model,
            // so it's just removed so it can be re-generated on the next request
            RenderingModel previousModel = renderingModelCache.remove(nameParts[0]);
            if (previousModel != null) {
                subscribers.forEach(s -> s.sitemapChanged(EventAction.REMOVED, previousModel.getSitemap()));
                logger.debug("Rendering model for '{}' invalidated", modelName);
            }
        }
    }

    @Override
    public @NonNull Set<@NonNull String> getSubscribedEventTypes() {
        return StateDependencyChangeListener.SUBSCRIBED_EVENT_TYPES;
    }

    @Override
    public @Nullable EventFilter getEventFilter() {
        return null; // not needed
    }

    @Override
    public void receive(@NonNull Event event) {
        for (RenderingModel r : renderingModelCache.values()) {
            // propagate the event to all cached rendering model states
            RenderingModelState state = r.getState();
            if (event instanceof ItemStateChangedEvent) {
                state.itemStateChanged((ItemStateChangedEvent) event);
            } else if (event instanceof ThingStatusInfoChangedEvent) {
                state.thingStatusChanged((ThingStatusInfoChangedEvent) event);
            } else {
                throw new IllegalArgumentException("Event type not supported: " + event.getClass());
            }
        }
    }
}
