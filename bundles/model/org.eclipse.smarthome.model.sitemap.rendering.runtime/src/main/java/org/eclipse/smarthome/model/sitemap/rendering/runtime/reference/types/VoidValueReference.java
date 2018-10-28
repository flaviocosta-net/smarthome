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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types;

import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * A value reference that points to no data source.
 *
 * @author Flavio Costa - Initial contribution
 */
public class VoidValueReference implements ValueReference<Void, Void> {

    /**
     * Singleton instance for this class - since it is immutable anyway.
     */
    public static final VoidValueReference INSTANCE = new VoidValueReference();

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Void getSource() {
        return null;
    }

    @Override
    public String getSourceType() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public Type getVariableType() {
        return Type.NONE;
    }

}
