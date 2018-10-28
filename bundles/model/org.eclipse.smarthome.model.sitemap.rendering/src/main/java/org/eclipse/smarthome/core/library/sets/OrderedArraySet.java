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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Read-only {@link OrderedSet} backed by an ArrayList. Any items provided in the initialization will have the
 * duplicates removed.
 *
 * @author Flavio Costa - Initial contribution and API.
 *
 * @param <E> Element type.
 */
public class OrderedArraySet<E> extends ArrayList<E> implements OrderedSet<E> {

    /**
     * Generated serial Version UID.
     */
    private static final long serialVersionUID = -7078886663721227259L;

    /**
     * Constructor that initializes the contents of the ordered set.
     *
     * @param e Collection of elements, potentially containing duplicate items.
     */
    public OrderedArraySet(Collection<E> elements) {
        // removes duplicates
        super(elements.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }
}
