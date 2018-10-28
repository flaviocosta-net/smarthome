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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.gson;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.library.sets.OrderedSet;
import org.eclipse.smarthome.io.json.gson.GsonTypeAdapterProvider;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.IconAtom;
import org.eclipse.smarthome.model.sitemap.rendering.atom.TextAtom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of a type adapter provider for Gson.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component(service = GsonTypeAdapterProvider.class, immediate = true)
public class GsonTypeAdapterImpl implements GsonTypeAdapterProvider {

    /**
     * Map from the type to an instance of its serializer.
     */
    private final Map<Class<?>, Object> serializerMap = new HashMap<>();

    @Activate
    void activate(Map<String, Object> properties) {
        Object cs = new InheritedFieldsSerializer();
        serializerMap.put(org.eclipse.smarthome.model.sitemap.rendering.Component.class, cs);
        serializerMap.put(Sitemap.class, cs);
        serializerMap.put(Container.class, cs);
        serializerMap.put(Atom.class, cs);
        serializerMap.put(TextAtom.class, cs);
        serializerMap.put(IconAtom.class, cs);

        Object cts = new ComponentTypeSerializer();
        serializerMap.put(ComponentType.class, cts);

        Object tue = new TaggedUnionElementSerializer();
        serializerMap.put(OrderedSet.class, tue);

        Object spic = new StylePropertyValueSerializer();
        serializerMap.put(StylePropertyValue.class, spic);
    }

    @Override
    public Map<Class<?>, Object> getTypeAdapters() {
        return serializerMap;
    }
}
