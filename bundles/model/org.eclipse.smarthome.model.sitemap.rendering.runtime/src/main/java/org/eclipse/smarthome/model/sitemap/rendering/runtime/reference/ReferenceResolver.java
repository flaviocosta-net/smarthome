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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.reference;

import java.util.Map;
import java.util.Set;

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.ItemValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.VoidValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.ComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.SitemapRenderingDriver;

/**
 * Resolves references inside an ETL module, providing implementation for operations that do not need to be customized
 * for each transformation.
 *
 * The methods here are named with simplicity in mind, to be concise and to neatly integrate with the sitemap
 * transformation syntax.
 *
 * @author Flavio Costa - Initial contribution.
 */
public interface ReferenceResolver {

    /**
     * Stateful builder for the transformation. While the {@link ReferenceResolver} is retrieved as a stateless
     * component, its method {@link #builder()} is used to obtain an instance of this class that will hold state
     * specific for the current transformation.
     */
    public interface Builder {

        /**
         * Returns an automatic Id for a component and increase the counter.
         *
         * @param type Type of the component.
         * @return Generated Id.
         */
        String autoId(ComponentType type);

        /**
         * Creates an atom instance based on the component type.
         *
         * @param typeName ComponentType name of the Atom to be created.
         * @return New Atom instance.
         */
        Atom<?> atom(String typeName);
    }

    /**
     * Returns an Item reference instance for a given Item Id.
     *
     * @param itemId Id of the Item.
     * @return Value reference.
     * @throws ItemNotFoundException If there is no Item for the provided Id.
     */
    ValueReference<?, ?> item(String itemId) throws ItemNotFoundException;

    /**
     * Returns a Thing reference instance for a given Thing UID.
     *
     * @param thingUID UID of the Thing.
     * @return Value reference.
     */
    ValueReference<?, ?> thing(String thingUID);

    /**
     * Returns a Sitemap reference instance for a given Sitemap Id.
     *
     * @param sitemapId Id of the Sitemap .
     * @return Value reference.
     */
    ValueReference<?, ?> sitemap(String sitemapId);

    /**
     * Returns a void reference instance.
     *
     * @return Value reference.
     */
    VoidValueReference none();

    /**
     * Returns the formatted state for an item.
     *
     * @param item Item instance.
     * @return State as it needs to be used in the rendering model (e.g. to be displayed to the user).
     */
    String formatting(ValueReference<?, ?> variableRef);

    /**
     * Returns the mapping options associated with an Item.
     *
     * @param item Item instance.
     * @return Tagged union with the respective options.
     */
    MappingReference<Item, State> options(ItemValueReference itemRef);

    /**
     * Converts definition model mappings into a Map of Strings.
     *
     * @param mappings Mappings from the definition model
     * @return Tagged union with plain String keys and values.
     */
    MappingReference<Item, State> options(ItemValueReference itemRef, Map<String, ?> mappings);

    /**
     * Returns value references for the member items included in a provided {@link GroupItem}.
     *
     * @param variableRef Variable reference for a GroupItem.
     * @return Set of member references.
     */
    Set<ItemValueReference> members(ItemValueReference variableRef);

    /**
     * Returns a new Builder instance.
     *
     * @return Newly created Builder.
     */
    Builder builder();

    /**
     * Creates a {@link ComponentState} for the provided {@link Component}.
     *
     * @param atom Sitemap Atom component.
     * @param expression EOL expression that calculates the state value.
     * @param style Map of all available styles for the current container.
     * @param context EOL execution context.
     * @return Component state instance.
     */
    <T> ComponentState<T> state(Atom<T> atom, Expression expression, Map<String, Expression> style,
            IEtlContext context);

    /**
     * Creates a {@link ComponentState} for the provided {@link Component}.
     *
     * @param container Sitemap Container component.
     * @param style Map of all available styles for the current container.
     * @param context EOL execution context.
     * @return Component state instance.
     */
    <T> ComponentState<T> state(Container<T, ?> container, Map<String, Expression> style, IEtlContext context);

    /**
     * Applies a transformation of a given type with some function to a value.
     *
     * @param type Transformation type, e.g. "regex" or "map".
     * @param transformation Transformation function to call, this value depends on the transformation type.
     * @param value Value to apply the transformation to.
     * @return Transformed value or the original one, if there was no service registered for the
     *         given type or a transformation exception occurred.
     */
    String transformation(String type, String transformation, String value);

    /**
     * Passes the sitemap rendering driver to this reference resolver.
     *
     * @param driver Driver being used for the sitemap transformation.
     */
    void setSitemapRenderingDriver(SitemapRenderingDriver driver);
}
