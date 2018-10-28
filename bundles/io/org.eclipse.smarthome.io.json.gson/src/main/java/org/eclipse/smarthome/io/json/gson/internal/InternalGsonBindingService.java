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
package org.eclipse.smarthome.io.json.gson.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.io.json.JsonBindingService;
import org.eclipse.smarthome.io.json.gson.GsonBindingService;
import org.eclipse.smarthome.io.json.gson.GsonTypeAdapterProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON data binding implementation that uses Google Gson to serialize and deserialize between
 * Java objects and JSON.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <T> Type to be serialized or deserialized.
 */
@Component(immediate = true, service = JsonBindingService.class, property = {
        InternalGsonBindingService.FORMAT_OUTPUT_PROPERTY + ":Boolean=true" })
public class InternalGsonBindingService extends GsonBindingService {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(InternalGsonBindingService.class);

    /**
     * Component property that determines whether JSON output should be formatted (easier to read) or not (less data to
     * be transmitted).
     */
    static final String FORMAT_OUTPUT_PROPERTY = "formatted-output";

    /**
     * Providers for Gson type adapters.
     */
    private final List<GsonTypeAdapterProvider> providers = new ArrayList<>();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTypeAdapterProvider(GsonTypeAdapterProvider provider) {
        // add type adapters to the configuration
        registerTypeAdapters(provider.getTypeAdapters().entrySet());
        providers.add(provider);
    }

    public void removeTypeAdapterProvider(GsonTypeAdapterProvider provider) {
        providers.remove(provider);
    }

    @Activate
    public void activate(Map<String, Object> properties) {
        Boolean prettyPrinting = (Boolean) properties.get(FORMAT_OUTPUT_PROPERTY);
        if (prettyPrinting) {
            // produces formatted output, should be disabled if not in debug mode
            setFormattedOutput(prettyPrinting);
            logger.debug("JSON output formatting is activated");
        }
    }
}
