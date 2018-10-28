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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.models.java;

import java.util.Collection;

import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.models.java.ReflectiveJavaModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.models.DeferredModelReference;

/**
 * Implementation of a deferred Java model.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <T> Type of object returned by the execution of the module (it should be a model).
 */
public class DeferredJavaModel<T extends IModel> extends DeferredModelReference<T> {

    /**
     * Object type from the origin model that will be included in the target model.
     */
    private final String type;

    /**
     * Classes to be included in the resolved (target) model.
     */
    private final Collection<Class<?>> classes;

    /**
     * Create a new deferred Java model reference.
     *
     * @param name Name of the model.
     * @param originModuleSettings Settings of the module that will generate the model in a deferred fashion (i.e. at
     *            some future point during the workflow execution).
     * @param type Object type from the origin model that will be included in the target model.
     * @param classes Classes to be added to the model. It may be just the class of the provided objects, or
     *            more class references if they are not all discoverable from the objects' class.
     */
    public DeferredJavaModel(String name, ModuleSettings<T> originModuleSettings, String type,
            Collection<Class<?>> classes) {
        super(name, originModuleSettings);
        this.type = type;
        this.classes = classes;
    }

    @Override
    protected IModel resolveTargetModel() {
        if (!this.target.equals(EMPTY_MODEL)) {
            throw new IllegalStateException("Target model already resolved");
        }
        try {
            return new ReflectiveJavaModel(name, getOriginModuleResult().getAllOfType(type), classes);
        } catch (EolModelElementTypeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
