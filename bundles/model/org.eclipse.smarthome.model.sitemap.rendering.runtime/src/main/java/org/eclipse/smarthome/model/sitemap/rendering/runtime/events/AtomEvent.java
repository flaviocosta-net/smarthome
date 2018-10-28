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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.events;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;

/**
 * Atom event implementation.
 *
 * @author Flavio Costa - Initial contribution
 *
 * @param <D> Type of the data field of the atom.
 */
public class AtomEvent<D> extends AbstractSitemapEvent {

    /**
     * Type of this event.
     */
    public static final String TYPE = AtomEvent.class.getSimpleName();

    /**
     * Specific string for atom data topics. The added parameter are Atom Id/EventAction.
     */
    public static final String TOPIC = SitemapEventFactory.SITEMAP_TOPIC_ROOT + "%s/%s";

    /**
     * Id of the Atom.
     */
    private final String atomId;

    /**
     * Previous Atom data.
     */
    private final D previousData;

    /**
     * New Atom data.
     */
    private final D newData;

    /**
     * Constructor for events.
     *
     * @param eventAction Action of the event.
     * @param topic Event topic.
     * @param payload Event payload.
     * @param source Event source.
     * @parma atomId Atom Id.
     * @param newData New data of the atom.
     * @param previousData Previous data of the atom.
     */
    AtomEvent(EventAction eventAction, String topic, String payload, String source, String atomId, D newData,
            D previousData) {
        super(eventAction, topic, payload, source);
        this.atomId = atomId;
        this.newData = newData;
        this.previousData = previousData;
    }

    /**
     * Builds a topic with the given parameters.
     *
     * @param topic Topic pattern.
     * @param args Arguments to be replaced on the topic.
     * @return Topic string with the provided arguments.
     */
    public static String buildTopic(Object... args) {
        return String.format(TOPIC, args);
    }

    @Override
    public String toString() {
        return String.format("Atom '%s' data changed from %s to %s", atomId, previousData, newData);
    }

    @Override
    public Set<EventAction> getSupportedActions() {
        return Collections.singleton(EventAction.CHANGED);
    }
}
