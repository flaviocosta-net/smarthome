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

import java.net.URI;

import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;

/**
 * Actionable Atom model implementation. This class should be used by atoms that
 * can trigger an action based in a URI associated to it.
 *
 * @author Flavio Costa
 */
public class ActionableAtom extends AbstractAtom<URI> {

    /**
     * Default, no-args constructor.
     */
    public ActionableAtom() {
        super();
    }

    /**
     * Constructor that sets a specific type.
     *
     * @param id Component Id.
     * @param type Component type, which must be compatible with the actual implementing class.
     */
    public ActionableAtom(String id, ComponentType type) {
        super(id, type);
    }
}
