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

import java.util.Collection;
import java.util.List;

import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;

/**
 * Configuration settings for a transformation from the definition model into the rendering model.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @see SitemapRenderingDriver
 */
public interface RenderingWorkflow {

    /**
     * Returns the name of the source object model to be rendered.
     *
     * @return Name of the source instance.
     */
    String getDefinitionModelName();

    /**
     * Returns the source object model to be rendered.
     *
     * @return Source instance.
     */
    Object getDefinitionObject();

    /**
     * Adds a new global variable to the transformation.
     *
     * @return Variable available during the transformation.
     */
    void addGlobalVariable(Variable variable);

    /**
     * Returns the list of all global variables.
     *
     * @return All variables available during the transformation.
     */
    Collection<Variable> getGlobalVariables();

    /**
     * Returns the reference to a global variable.
     *
     * @param name Variable name.
     * @return Variable object instance.
     */
    Variable getGlobalVariable(VarName name);

    /**
     * Returns the type of the sitemap.
     *
     * @return Sitemap type as returned by the {@link SitemapRenderingProvider}.
     */
    String getSitemapType();

    /**
     * Adds a new module to this workflow.
     *
     * @param scriptPath Module script path, including the file extension.
     * @return Module settings that can be configured for this workflow execution.
     * @throws IllegalArgumentException If the provided script name is invalid or otherwise cnanot be found.
     */
    <T> ModuleSettings<T> addModule(String scriptPath) throws IllegalArgumentException;

    /**
     * Adds a new module to this workflow.
     *
     * @param scriptPath Module script path.
     * @param extension Module extension (script type).
     * @throws IllegalArgumentException If the provided script name is invalid or otherwise cnanot be found.
     */
    <T> ModuleSettings<T> addModule(String scriptName, String extension) throws IllegalArgumentException;

    /**
     * Returns the settings for all modules added to this workflow.
     *
     * @return List of module settings.
     */
    List<ModuleSettings<?>> getModules();

    /**
     * Free up resources allocated for every module added to this workflow.
     */
    void dispose();
}
