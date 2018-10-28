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
package org.eclipse.smarthome.io.rest.sitemap.internal;

/**
 * This is a data transfer object that is used to serialize page content.
 *
 * @author Kai Kreuzer - Initial contribution and API
 * @author Flavio Costa - Moved attributes to {@link ClassicPageDTO}
 */
public class PageDTO {

    public String id;

    public String title;
    public String icon;
    public String link;

    public PageDTO() {
    }

}
