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
package org.eclipse.smarthome.io.rest.sse.sitemap.rendering;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventFactory;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation that posts Sitemap {@link Event}s through the Eclipse SmartHome event bus in an asynchronous way.
 * Posted events are created by {@link SitemapEventFactory}, which implements the {@link EventSubscriber} callback
 * interface.
 *
 * @author Flavio Costa - Initial contribution
 */
@Component
public class RenderingModelEventPublisher implements SitemapEventSubscriber {

    /**
     * ESH event publisher.
     */
    protected EventPublisher eventPublisher;

    /**
     * Sitemap event factory.
     */
    protected SitemapEventFactory eventFactory;

    @Reference
    protected void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }

    @Reference
    protected void setSitemapEventFactory(SitemapEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    protected void unsetSitemapEventFactory(SitemapEventFactory eventFactory) {
        this.eventFactory = null;
    }

    @Override
    public <D> void atomChanged(EventAction eventAction, Sitemap sitemap, Container<?, ?> container, Atom<D> atom,
            D previousData) {
        eventPublisher.post(eventFactory.createAtomEvent(eventAction, sitemap, container, atom, previousData));
    }

    @Override
    public void sitemapChanged(EventAction eventAction, Sitemap sitemap) {
        eventPublisher.post(eventFactory.createSitemapEvent(eventAction, sitemap));
    }

    @Override
    public <D> void styleChanged(EventAction eventAction, Sitemap sitemap,
            org.eclipse.smarthome.model.sitemap.rendering.Component<D> component, String styleName) {
        eventPublisher.post(eventFactory.createStyleEvent(eventAction, sitemap, component, styleName));
    }
}
