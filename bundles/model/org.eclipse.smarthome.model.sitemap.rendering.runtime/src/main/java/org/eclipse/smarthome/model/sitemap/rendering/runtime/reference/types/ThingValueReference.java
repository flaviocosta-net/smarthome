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

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * A value reference that points to a {@link Thing}.
 *
 * @author Flavio Costa - Initial contribution
 */
public class ThingValueReference implements ValueReference<Thing, ThingStatusInfo> {

    /**
     * Data source for this value reference.
     */
    private final Thing thing;

    /**
     * Constructor that receives a Thing.
     *
     * @param thing Data source for this value reference.
     */
    public ThingValueReference(Thing thing) {
        this.thing = thing;
    }

    @Override
    public String getId() {
        return thing.getUID().getAsString();
    }

    @Override
    public Thing getSource() {
        return thing;
    }

    @Override
    public String getSourceType() {
        return "Thing";
    }

    @Override
    public String getLabel() {
        return thing.getLabel();
    }

    @Override
    public ThingStatusInfo getValue() {
        return thing.getStatusInfo();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public Type getVariableType() {
        return Type.THING;
    }

}
