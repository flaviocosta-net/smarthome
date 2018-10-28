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
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;

/**
 * Style event implementation.
 *
 * @author Flavio Costa - Initial contribution
 */
public class StyleEvent extends AbstractSitemapEvent {

    /**
     * Type of this event.
     */
    public static final String TYPE = StyleEvent.class.getSimpleName();

    /**
     * Specific string for component style topics. The added parameter are Component Id/EventAction.
     */
    public static final String TOPIC = SitemapEventFactory.SITEMAP_TOPIC_ROOT + "%s/%s";

    /**
     * Style property name.
     */
    private final String style;

    /**
     * Style property value.
     */
    private final StylePropertyValue newValue;

    /**
     * Constructor for events.
     *
     * @param eventAction Action of the event.
     * @param topic Event topic.
     * @param payload Event payload.
     * @param source Event source.
     * @param styleName Event style property name.
     * @param newValue Style property value.
     */
    StyleEvent(EventAction eventAction, String topic, String payload, String source, String style,
            StylePropertyValue newValue) {
        super(eventAction, topic, payload, source);
        this.style = style;
        this.newValue = newValue;
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
        return String.format("Style property '%s' data changed to %s", style, newValue);
    }

    @Override
    public Set<EventAction> getSupportedActions() {
        return Collections.singleton(EventAction.CHANGED);
    }
}
