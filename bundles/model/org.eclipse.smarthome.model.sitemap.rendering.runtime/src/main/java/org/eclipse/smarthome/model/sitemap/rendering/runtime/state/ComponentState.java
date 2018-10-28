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

import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber;

/**
 * Stores the current state for a given component.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <D> Component data type.
 */
public interface ComponentState<D> extends StateDependencyChangeListener {

    /**
     * Returns the component instance.
     *
     * @return Component the state refers to.
     */
    Component<D> getComponent();

    /**
     * Returns the value of the component state to be displayed to the end-user.
     *
     * @param locale Locale used to determine display values for components.
     *
     * @return Calculated state display value.
     */
    String getDisplayValue(Locale locale);

    /**
     * Defines the set of subscribers to be notified of any state changes for this component.
     *
     * @param subscribers Sitemap event subscribers.
     */
    void setSubscribers(Set<SitemapEventSubscriber> subscribers);
}
