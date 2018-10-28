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
package org.eclipse.smarthome.core.library.sets;

/**
 * Data structure that consists of an ordered set of elements, one of which that may be selected (tagged) at a given
 * moment.
 *
 * @author Flavio Costa - Initial implementation and API
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public interface TaggedUnion<K, V> {

    /**
     * Value that indicates that no element is currently selected.
     */
    public static final int NONE = -1;

    /**
     * Element of the tagged union.
     *
     * @param <K> Key type.
     * @param <V> Value type.
     */
    public interface Element<K, V> {

        /**
         * Returns the key of the element.
         *
         * @return Key assigned to this element.
         */
        K getKey();

        /**
         * Returns the value of the element.
         *
         * @return Value assigned to this element.
         */
        V getValue();
    }

    /**
     * Returns the index of the currently selected element.
     *
     * @return Index (position) of the tagged element, or {@link NONE} if there is no element tagged.
     */
    int getIndex();

    /**
     * Defines the index of the currently selected element.
     *
     * @param index New index (position) of the tagged element.
     */
    void setIndex(int index);

    /**
     * Returns the selected element.
     *
     * @return Selected element, or null if there is no tagged element.
     */
    Element<K, V> getSelected();

    /**
     * Returns the set of elements.
     *
     * @return Ordered set of elements.
     */
    OrderedSet<Element<K, V>> getElements();

    /**
     * Defines the currently selected element based on the key value.
     *
     * @param key Key of the element to be tagged.
     * @throws IllegalArgumentException If no matching key currently exists on this tagged union.
     */
    void setSelectedKey(K key);
}
