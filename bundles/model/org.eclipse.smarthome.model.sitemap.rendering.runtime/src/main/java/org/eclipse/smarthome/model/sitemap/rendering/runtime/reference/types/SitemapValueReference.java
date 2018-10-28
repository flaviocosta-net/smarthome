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

import org.eclipse.smarthome.model.sitemap.rendering.atom.IconAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.TextAtom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;

/**
 * A value reference that points to a {@link Sitemap}.
 *
 * @author Flavio Costa - Initial contribution
 */
public class SitemapValueReference implements ValueReference<Sitemap, String> {

    /**
     * Data source for this value reference.
     */
    private final Sitemap sitemap;

    /**
     * Constructor that receives a Sitemap.
     *
     * @param thing Data source for this value reference.
     */
    public SitemapValueReference(Sitemap sitemap) {
        this.sitemap = sitemap;
    }

    @Override
    public String getId() {
        return sitemap.getId();
    }

    @Override
    public Sitemap getSource() {
        return sitemap;
    }

    @Override
    public String getSourceType() {
        // TODO what if it's registry?
        return "smarthome";
    }

    @Override
    public String getLabel() {
        TextAtom atom = sitemap.getData().getLabel();
        if (atom == null) {
            return null;
        }
        return atom.getData();
    }

    @Override
    public String getCategory() {
        IconAtom atom = sitemap.getData().getIcon();
        if (atom == null) {
            return null;
        }
        return atom.getData().toASCIIString();
    }

    @Override
    public String getValue() {
        return sitemap.getId();
    }

    @Override
    public Type getVariableType() {
        return Type.SITEMAP;
    }

}
