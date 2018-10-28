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

/**
 * A value reference is a reference on a sitemap for some source of data outside of that sitemap definition. The nested
 * emum {@link Type} identifies what the possible reference types are.
 *
 * This interface abstracts each specific implementation, so the framework can retrieve such values in a generic way,
 * without having to know what type of data source provides them.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <E> Type of the value source.
 * @param <V> Type of value.
 */
public interface ValueReference<E, V> {

    /**
     * Value reference types.
     */
    public static enum Type {
    SITEMAP,
    THING,
    ITEM,
    NONE
    }

    /**
     * Returns the type of this value reference.
     *
     * @return Value reference type.
     */
    Type getVariableType();

    /**
     * Returns the id of the data source.
     *
     * @return Id of the source of the value.
     */
    String getId();

    /**
     * Returns the source of the data value.
     *
     * @return Source instance.
     */
    E getSource();

    /**
     * Returns a source-specific type.
     *
     * @return Returns a type that is meaningful to the data source. It may be some fixed string if the source has no
     *         specific type information.
     */
    String getSourceType();

    /**
     * Returns the icon category associated with that value reference, if any.
     *
     * @return Icon category, or null if the source does not provide this information.
     */
    String getCategory();

    /**
     * Returns the label associated with that value reference, if any.
     *
     * @return Textual label, or null if the source does not provide this information.
     */
    String getLabel();

    /**
     * Returns the value provided by the data source.
     *
     * @return Current value of the reference.
     */
    V getValue();
}
