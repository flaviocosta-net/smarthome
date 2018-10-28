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
package org.eclipse.smarthome.model.sitemap.rendering.container;

import java.net.URI;

import org.eclipse.smarthome.model.sitemap.rendering.atom.ActionableAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;

/**
 * Widget model implementation. A widget only contains atoms, but not other
 * containers. Its action is determined by the first {@link ActionableAtom} it
 * contains.
 *
 * @author Flavio Costa
 */
public class Widget extends AbstractContainer<URI, Atom<?>> {

    @Override
    public URI getData() {
        // URI of the first component that is an Actionable Atom
        return components.stream().filter(ActionableAtom.class::isInstance).findFirst().map(ActionableAtom.class::cast)
                .map(ActionableAtom::getData).orElse(null);
    }
}
