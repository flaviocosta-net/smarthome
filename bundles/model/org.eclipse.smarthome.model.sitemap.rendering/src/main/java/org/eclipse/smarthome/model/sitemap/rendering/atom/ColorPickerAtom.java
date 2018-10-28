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
package org.eclipse.smarthome.model.sitemap.rendering.atom;

import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;

/**
 * Color Picker Atom model implementation.
 *
 * @author Flavio Costa
 */
public class ColorPickerAtom extends AbstractAtom<String> {

    /**
     * Default, no-args constructor.
     */
    public ColorPickerAtom() {
        super();
    }

    /**
     * Constructor that sets a specific type.
     *
     * @param id Component Id.
     * @param type Component type, which must be compatible with the actual implementing class.
     */
    public ColorPickerAtom(String id, ComponentType type) {
        super(id, type);
    }
}
