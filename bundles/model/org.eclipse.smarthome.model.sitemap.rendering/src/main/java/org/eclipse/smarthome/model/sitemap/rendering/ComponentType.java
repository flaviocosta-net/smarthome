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
package org.eclipse.smarthome.model.sitemap.rendering;

import org.eclipse.smarthome.model.sitemap.rendering.atom.ActionableAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.ColorPickerAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.IconAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.MapViewAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.MappingsAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.SliderAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.TextAtom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Frame;
import org.eclipse.smarthome.model.sitemap.rendering.container.Page;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.container.Widget;

/**
 * Enumerates all supported Component types, mapping each one of them to the
 * class that implements it on the Java model.
 *
 * @author Flavio Costa
 */
public enum ComponentType {

    SITEMAP(Sitemap.class),
    FRAME(Frame.class),
    GROUP(ActionableAtom.class),
    ICON(IconAtom.class),
    PAGE(Page.class),
    LABEL(TextAtom.class),
    COLORPICKER(ColorPickerAtom.class),
    MAPVIEW(MapViewAtom.class),
    SELECTION(MappingsAtom.class),
    SLIDER(SliderAtom.class),
    SWITCH(MappingsAtom.class),
    TEXT(TextAtom.class),
    WIDGET(Widget.class);

    /**
     * Class implementing this component type.
     */
    private Class<? extends Component<?>> implementingClass;

    /**
     * Constructor that defines the implementing class.
     *
     * @param ic
     *            Implementing class reference.
     */
    ComponentType(Class<? extends Component<?>> ic) {
        this.implementingClass = ic;
    }

    /**
     * Returns the implementing class.
     *
     * @return Class that implements this component type in the model.
     */
    public Class<? extends Component<?>> getImplementingClass() {
        return implementingClass;
    }
}