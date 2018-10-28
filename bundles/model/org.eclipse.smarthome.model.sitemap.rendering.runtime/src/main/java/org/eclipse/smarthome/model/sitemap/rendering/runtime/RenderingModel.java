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
package org.eclipse.smarthome.model.sitemap.rendering.runtime;

import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;

/**
 * Rendering model consisting of a sitemap structure and its state.
 *
 * @author Flavio Costa - Initial contribution
 */
public interface RenderingModel {

    /**
     * Returns the sitemap structure for this rendering model.
     *
     * @return Sitemap model.
     */
    Sitemap getSitemap();

    /**
     * Returns the state for this rendering model.
     *
     * @return Model state.
     */
    RenderingModelState getState();
}
