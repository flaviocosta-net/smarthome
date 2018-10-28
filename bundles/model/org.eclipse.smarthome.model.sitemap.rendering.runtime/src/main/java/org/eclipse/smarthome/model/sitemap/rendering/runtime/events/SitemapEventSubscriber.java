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

import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;

/**
 * Interface for subscribers of events related to sitemaps and its components. By implementing this interface, a class
 * will start receiving such events via the OSGi notification mechanism upon which the Eclipse SmartHome service bus is
 * built.
 *
 * @author Flavio Costa - Initial contribution and API
 */
public interface SitemapEventSubscriber {

    /**
     * Action associated with an event (i.e. "what happened").
     */
    public enum EventAction {
    LOADED,
    RELOADED,
    CHANGED,
    REMOVED;

        /*
         * TODO Cleanup
         * private final Class<?> eventClass;
         *
         * private EventType(Class<?> eventClass) {
         * this.eventClass = eventClass;
         * }
         *
         * public static Set<String> typeSet() {
         * return Stream.of(values()).map(EventType::getTypeName).collect(Collectors.toSet());
         * }
         *
         * public Class<?> getEventClass() {
         * return eventClass;
         * }
         *
         * public String getTypeName() {
         * return eventClass.getSimpleName();
         * }
         *
         * public static EventType fromTypeName(String typeName) {
         * return Stream.of(values()).filter(e -> e.getTypeName().equals(typeName)).findFirst().get();
         * };
         */
    }

    /**
     * Called when there is a change to an atom.
     *
     * @param eventAction Action of the event.
     * @param sitemap Sitemap instance.
     * @param container Atom Container instance.
     * @param atom Atom instance.
     * @param previousData Previous data of the atom.
     */
    <D> void atomChanged(EventAction eventAction, Sitemap sitemap, Container<?, ?> container, Atom<D> atom,
            D previousData);

    /**
     * Called when there is a change to a component style.
     *
     * @param eventAction Action of the event.
     * @param sitemap Sitemap instance.
     * @param component Component that the style refers to.
     * @param styleName Name of the style property that was changed.
     */
    <D> void styleChanged(EventAction eventAction, Sitemap sitemap, Component<D> component, String styleName);

    /**
     * Called when there is a change to a sitemap.
     *
     * @param eventAction Action of the event.
     * @param sitemap Sitemap instance.
     */
    void sitemapChanged(EventAction eventAction, Sitemap sitemap);
}
