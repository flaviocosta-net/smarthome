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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.models;

import org.eclipse.epsilon.eol.models.EmptyModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;

/**
 * Deferred reference to a model used in a transformation workflow. This is needed in situations where a model is known
 * to be needed at some point during the workflow execution, but it still does not exist when the workflow is setup. The
 * model should eventually be generated during the workflow execution, then it can be resolved so the deferred model
 * that was used in the workflow configuration starts pointing to the underlying model that was generated.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <T> Type of object returned by the execution of the module.
 */
public abstract class DeferredModelReference<T> extends ModelReference {

    /**
     * Empty model reference.
     */
    protected static final IModel EMPTY_MODEL = new EmptyModel();

    /**
     * Settings of the module that will generate the model.
     */
    private final ModuleSettings<T> originModuleSettings;

    /**
     * Create a new deferred model reference.
     *
     * @param name Name of the model.
     * @param originModuleSettings Settings of the module that will generate the model in a deferred fashion (i.e. at
     *            some future point during the workflow execution).
     */
    public DeferredModelReference(String name, ModuleSettings<T> originModuleSettings) {
        super(EMPTY_MODEL);
        setName(name);
        this.originModuleSettings = originModuleSettings;
    }

    /**
     * Method to be implemented by subclasses to resolve (create or retrieve) the target model whenever it needs to be
     * used in the workflow.
     *
     * @return New model instance.
     */
    protected abstract IModel resolveTargetModel();

    /**
     * Returns the result of the last execution of the module associated with this deferred model.
     *
     * @return Module execution result.
     */
    protected T getOriginModuleResult() {
        return originModuleSettings.getResult();
    }

    /**
     * Resolve the target model and copy all its aliases to this model.
     */
    public void resolve() {
        this.target = resolveTargetModel();
        this.aliases.addAll(this.target.getAliases());
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s] => %s", this.getClass().getSimpleName(), getName(),
                target == null ? null : target.toString());
    }
}
