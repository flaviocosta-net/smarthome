package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.workflow;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.epsilon.common.module.IModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.models.java.ReflectiveJavaModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;

/**
 * Base implementation for workflow module settings.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <T> Type of object returned by the execution of this module.
 */
public abstract class AbstractModuleSettings<T> implements ModuleSettings<T> {

    /**
     * Models available during the transformation.
     */
    private final List<IModel> models = new ArrayList<>();

    /**
     * URI for the transformation script.
     */
    private final URI scriptURI;

    /**
     * Rendering workflow.
     */
    private final RenderingWorkflow workflow;

    /**
     * Module execution result.
     */
    protected T result;

    /**
     * Returns the rendering workflow.
     *
     * @return Workflow containing this module.
     */
    protected RenderingWorkflow getWorkflow() {
        return workflow;
    }

    /**
     * Convenience constructor for subclasses.
     *
     * @param workflow Workflow containing this module
     * @param scriptURI URI for the transformation script.
     */
    public AbstractModuleSettings(RenderingWorkflow workflow, URI scriptURI) {
        this.workflow = workflow;
        this.scriptURI = scriptURI;
    }

    /**
     * Convenience method for subclasses to implement {@link #createInstance(ModuleSettings)}. It received an EOL module
     * created on the subclass and sets its module repository to be the repository of the previous module in the
     * workflow, if any.
     *
     * @param previousModuleSettings Previous module settings, in case data needs to be passed from the previous
     *            workflow module into this one.
     * @param newModule New EOL module instance.
     * @return
     */
    protected IEolModule createInstance(ModuleSettings<?> previousModuleSettings, IEolModule newModule) {
        if (previousModuleSettings != null) {
            newModule.getContext()
                    .setModelRepository(previousModuleSettings.getModule().getContext().getModelRepository());
        }
        return newModule;
    }

    @Override
    public boolean parse() {
        IModule module = getModule();
        try {
            module.parse(scriptURI);
            return module.getParseProblems().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public T getResult() {
        if (result == null) {
            throw new IllegalStateException("No successful call to execute() happened yet");
        }
        return result;
    }

    @Override
    public IModel addModel(IModel model) {
        models.add(model);
        return model;
    }

    @Override
    public IModel addModel(String name, Object rootObject) {
        IModel model;
        if (rootObject instanceof IModel) {
            model = (IModel) rootObject;
            model.setName(name);
        } else {
            model = new ReflectiveJavaModel(name, rootObject);
        }
        return addModel(model);
    }

    @Override
    public IModel addModel(String name, Collection<? extends Class<?>> classes) {
        return addModel(new ReflectiveJavaModel(name, classes));
    }

    @Override
    public IModel addModel(String name, Collection<? extends Object> objects, Collection<? extends Class<?>> classes) {
        return addModel(new ReflectiveJavaModel(name, objects, classes));
    }

    @Override
    public List<IModel> getModels() {
        return Collections.unmodifiableList(models);
    }

    @Override
    public URI getScriptURI() {
        return scriptURI;
    }

    @Override
    public void dispose() {
        IEolModule module = getModule();
        if (module != null) {
            module.getContext().getModelRepository().dispose();
        }
    }
}
