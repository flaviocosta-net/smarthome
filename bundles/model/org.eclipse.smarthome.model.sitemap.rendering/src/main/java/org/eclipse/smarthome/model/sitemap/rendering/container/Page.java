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
package org.eclipse.smarthome.model.sitemap.rendering.container;

import org.eclipse.smarthome.model.sitemap.rendering.container.AbstractNavigableContainer.Data;

/**
 * Page model implementation. A Page container is generated on the server-side
 * to hold the containers to be displayed when the user navigates into a
 * specific page on the sitemap (so it is a {@link NavigableContainer}).
 *
 * @author Flavio Costa
 */
public class Page extends AbstractNavigableContainer<Data, Container<?, ?>> {

}
