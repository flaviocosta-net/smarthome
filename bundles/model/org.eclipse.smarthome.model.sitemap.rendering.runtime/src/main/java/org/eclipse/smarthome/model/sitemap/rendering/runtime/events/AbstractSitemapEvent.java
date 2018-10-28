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

import java.util.Set;

import org.eclipse.smarthome.core.events.AbstractEvent;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;

/**
 * Base sitemap event implementation.
 *
 * @author Flavio Costa - Initial contribution
 */
public abstract class AbstractSitemapEvent extends AbstractEvent {

    /**
     * Action of the event.
     */
    private final EventAction eventAction;

    public AbstractSitemapEvent(EventAction eventAction, String topic, String payload, String source) {
        super(topic, payload, source);
        this.eventAction = eventAction;
        if (!getSupportedActions().contains(eventAction)) {
            throw new IllegalArgumentException(getType() + " does not support action " + eventAction);
        }
    }

    /**
     * Supported actions on this event type.
     *
     * @return Set of Event Actions.
     */
    public abstract Set<EventAction> getSupportedActions();

    /**
     * Event Action associated with this event.
     *
     * @return Event Action instance.
     */
    public EventAction getEventAction() {
        return eventAction;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
