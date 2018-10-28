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
package org.eclipse.smarthome.model.sitemap.standard.runtime.internal.activator;

import org.eclipse.smarthome.model.core.ModelParser;
import org.eclipse.smarthome.model.sitemap.standard.SmartHomeSitemapStandaloneSetup;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator for the 'smarthome' sitemap EMF meta-model. It triggers EMF registration and add the EPackage to the global
 * Registry.
 *
 * @author Flavio Costa - Initial contribution.
 */
@Component(service = ModelParser.class, immediate = true)
public class SmartHomeSitemapRuntimeActivator implements ModelParser {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(SmartHomeSitemapRuntimeActivator.class);

    /**
     * Register the configuration parser.
     *
     * @throws Exception
     */
    public void activate() throws Exception {
        SmartHomeSitemapStandaloneSetup.doSetup();
        logger.debug("Registered 'smarthome' configuration parser");
    }

    /**
     * Unregister the configuration parser.
     *
     * @throws Exception
     */
    public void deactivate() throws Exception {
        SmartHomeSitemapStandaloneSetup.unregister();
    }

    @Override
    public String getExtension() {
        return "smarthome";
    }

}
