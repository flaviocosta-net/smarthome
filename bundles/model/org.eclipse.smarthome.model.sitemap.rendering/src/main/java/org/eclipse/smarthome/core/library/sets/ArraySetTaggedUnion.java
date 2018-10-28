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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tagged union implementation backed by an {@link OrderedArraySet}.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class ArraySetTaggedUnion<K, V> implements TaggedUnion<K, V> {

    /**
     * Element of a {@link ArraySetTaggedUnion}.
     *
     * @param <K> Key type.
     * @param <V> Value type.
     */
    public static class ArraySetElement<K, V> implements Element<K, V> {

        /**
         * Element key.
         */
        private final K key;

        /**
         * Element value.
         */
        private final V value;

        /**
         * Constructor that takes a Map entry as an argument.
         *
         * @param entry Entry used to initialize this element.
         */
        private ArraySetElement(Map.Entry<K, V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + ":" + value;
        }
    }

    /**
     * Set of elements in the tagged union.
     */
    private final OrderedSet<Element<K, V>> elements;

    /**
     * Index of the selected element.
     */
    private int index = NONE;

    /**
     * Constructor that takes a Map as an argument.
     *
     * @param map Map used to initialize this tagged union.
     */
    public ArraySetTaggedUnion(Map<K, V> map) {

        Stream<Element<K, V>> stream = map.entrySet().stream().map(ArraySetElement::new);
        elements = new OrderedArraySet<>(stream.collect(Collectors.toList()));
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        if (index < NONE || index >= elements.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.index = index;
    }

    @Override
    public Element<K, V> getSelected() {
        if (index == NONE) {
            return null;
        }
        return elements.get(index);
    }

    @Override
    public OrderedSet<Element<K, V>> getElements() {
        return elements;
    }

    @Override
    public void setSelectedKey(K key) {
        for (int i = 0; i < elements.size(); i++) {
            K elementKey = elements.get(i).getKey();
            if (elementKey.equals(key)) {
                setIndex(i);
                return;
            }
        }
        throw new IllegalArgumentException("Key " + key + " not found");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ArraySetTaggedUnion)) {
            return false;
        }
        ArraySetTaggedUnion<?, ?> other = (ArraySetTaggedUnion<?, ?>) obj;
        return this.index == other.index || elements.equals(other.elements);
    }

    @Override
    public String toString() {
        return "index=" + index + " elements=" + elements;
    }
}
