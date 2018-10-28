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

import java.util.List;
import java.util.Set;
import java.util.Spliterator;

/**
 * Data structure that consists of an ordered set of elements. It can be though of a list where the insertion order of
 * elements is preserved, and at the same time the elements are guaranteed to be unique.
 *
 * @author Flavio Costa - Initial implementation and API
 * 
 * @param <E> Element type.
 */
public interface OrderedSet<E> extends Set<E>, List<E> {

    @Override
    default Spliterator<E> spliterator() {
        // this is needed to resolve the conflict of the default
        // method, present in both super interfaces
        return List.super.spliterator();
    }
}
