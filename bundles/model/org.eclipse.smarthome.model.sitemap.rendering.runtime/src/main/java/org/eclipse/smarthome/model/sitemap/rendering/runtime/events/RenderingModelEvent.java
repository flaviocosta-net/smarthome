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

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;

/**
 * Rendering model event implementation.
 *
 * @author Flavio Costa - Initial contribution
 */
public class RenderingModelEvent extends AbstractSitemapEvent {

    /**
     * Type of this event.
     */
    public static final String TYPE = RenderingModelEvent.class.getSimpleName();

    /**
     * Specific string for sitemap topics. The added parameter is the EventAction.
     */
    public static final String TOPIC = SitemapEventFactory.SITEMAP_TOPIC_ROOT + "%s";

    /**
     * Sitemap Id.
     */
    private final String sitemapId;

    /**
     * Constructor for events.
     *
     * @param eventAction Action of the event.
     * @param topic Event topic.
     * @param payload Event payload.
     * @param source Event source.
     * @param sitemapId Event sitemap Id.
     */
    RenderingModelEvent(EventAction eventAction, String topic, String payload, String source, String sitemapId) {
        super(eventAction, topic, payload, source);
        this.sitemapId = sitemapId;
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

    /**
     * Id of the Sitemap associated with this event.
     *
     * @return Id of the Sitemap.
     */
    public String getSitemapId() {
        return sitemapId;
    }

    @Override
    public String toString() {
        return String.format("Sitemap '%s' is %s", sitemapId, this.getEventAction());
    }

    @Override
    public Set<EventAction> getSupportedActions() {
        return EnumSet.of(EventAction.LOADED, EventAction.RELOADED, EventAction.REMOVED);
    }
}
