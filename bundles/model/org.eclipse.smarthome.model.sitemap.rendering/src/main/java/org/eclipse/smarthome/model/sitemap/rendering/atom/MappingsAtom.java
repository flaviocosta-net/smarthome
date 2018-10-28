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

import org.eclipse.smarthome.core.library.sets.TaggedUnion;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;

/**
 * Mappings Atom model implementation. This class should be used by atoms whose
 * data is represented by {"key": "value"} mappings, one of which may be
 * selected. The entries must be ordered according to their sequence in the
 * rendering model.
 *
 * @author Flavio Costa
 */
public class MappingsAtom extends AbstractAtom<TaggedUnion<String, String>> {

    /**
     * Default, no-args constructor.
     */
    public MappingsAtom() {
        super();
    }

    /**
     * Constructor that sets a specific type.
     *
     * @param id Component Id.
     * @param type Component type, which must be compatible with the actual implementing class.
     */
    public MappingsAtom(String id, ComponentType type) {
        super(id, type);
    }
}
