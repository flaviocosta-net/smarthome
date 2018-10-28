package org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.models.IModel;

/**
 * Configuration for the execution of a certain workflow module.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <T> Type of object returned by the execution of this module.
 */
public interface ModuleSettings<T> {

    /**
     * Returns the URI for the transformation script.
     *
     * @return Resource URI.
     */
    URI getScriptURI();

    /**
     * Type of the module.
     *
     * @return Module type, typically identified by the extension of the script file.
     */
    String getModuleType();

    /**
     * Returns the EOL module these settings refer to.
     *
     * @return Module instance.
     */
    IEolModule getModule();

    /**
     * Creates a new EOL module instance based on the settings of the previous module.
     *
     * @param previousModuleSettings Previous module settings, in case data needs to be passed from the previous
     *            workflow module into this one.
     * @return New Module instance.
     */
    IEolModule createInstance(ModuleSettings<?> previousModuleSettings);

    /**
     * Returns the list of all models.
     *
     * @return All models available for the transformation.
     */
    List<IModel> getModels();

    /**
     * Adds an existing model to the transformation.
     *
     * @return Model available during the transformation.
     */
    IModel addModel(IModel model);

    /**
     * Adds a new model to the transformation.
     *
     * @param name Name of the model.
     * @param classes Classes included in the model.
     * @return Model available during the transformation.
     */
    IModel addModel(String name, Collection<? extends Class<?>> classes);

    /**
     * Adds a new model to the transformation.
     *
     * @param name Name of the model.
     * @param rootObject Root object in the model.
     * @return Model available during the transformation.
     */
    IModel addModel(String name, Object rootObject);

    /**
     * Adds a new model to the transformation.
     *
     * @param name Name of the model.
     * @param objects Objects in the model.
     * @param classes Classes included in the model.
     * @return Model available during the transformation.
     */
    IModel addModel(String name, Collection<? extends Object> objects, Collection<? extends Class<?>> classes);

    /**
     * Loads the module with the given script.
     *
     * @return True if the script was parsed successfully, false if problems occurred.
     *
     * @see #getScriptURI()
     */
    boolean parse();

    /**
     * Executes the module.
     *
     * @return Object resulting from the module execution.
     * @throws EolRuntimeException In case any EOL exceptions occurred during the execution.
     */
    T execute() throws EolRuntimeException;

    /**
     * Returns the result of the last module execution.
     *
     * @return Object resulting from the module execution.
     * @throws IllegalStateException If the module has not been executed yet.
     */
    T getResult();

    /**
     * Free up resources allocated for this module.
     */
    void dispose();
}
