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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.thing.events.ThingStatusInfoChangedEvent;

/**
 * Defines a listener for changes into any possible component state dependencies.
 *
 * @author Flavio Costa - Initial contribution and API
 */
public interface StateDependencyChangeListener {

    /**
     * Event types that this component subscribes to.
     */
    public static final Set<String> SUBSCRIBED_EVENT_TYPES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(ItemStateChangedEvent.TYPE, ThingStatusInfoChangedEvent.TYPE)));

    /**
     * Called when an Item state is changed.
     *
     * @param event Received change event.
     */
    public void itemStateChanged(ItemStateChangedEvent event);

    /**
     * Called when a Thing status is changed.
     *
     * @param event Received change event.
     */
    void thingStatusChanged(ThingStatusInfoChangedEvent event);
}
