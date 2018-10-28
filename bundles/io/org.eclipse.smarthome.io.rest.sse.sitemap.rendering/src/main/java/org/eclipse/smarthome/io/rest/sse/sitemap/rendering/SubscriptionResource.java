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

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST resource for SSE subscriptions.
 *
 * TODO This is probably temporary code, this logic, if still applicable, will eventually be implemented elsewhere.
 *
 * @author Flavio Costa - Initial contribution.
 */
@Path(SubscriptionResource.PATH_SUBSCRIPTIONS)
@RolesAllowed({ Role.USER, Role.ADMIN })
@Api(value = SubscriptionResource.PATH_SUBSCRIPTIONS)
public class SubscriptionResource implements RESTResource {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(SubscriptionResource.class);

    /**
     * Name of the HTTP header to control proxy buffering with nginx.
     */
    private static final String X_ACCEL_BUFFERING_HEADER = "X-Accel-Buffering";

    /**
     * Name of the HTTP header to obtain the source IP for clients running behind a proxy.
     */
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * Custom HTTP header for the client fingerprint, without X- prefix as per RFC 6648.
     */
    private static final String CLIENT_FINGERPRINT_HEADER = "Client-Fingerprint";

    /**
     * Path for this resource.
     */
    public static final String PATH_SUBSCRIPTIONS = "subscriptions/sitemap";

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    /**
     * Subscribes the connecting client to the stream of sitemap events.
     *
     * @return {@link EventOutput} object associated with the incoming
     *         connection.
     */
    @GET
    @Path(PATH_SUBSCRIPTIONS + "/{etag: [a-zA-Z_0-9-]*}/")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @ApiOperation(value = "Get sitemap events.", response = EventOutput.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 410, message = "Subscription does not exist.") })
    public Object getSitemapEvents(@PathParam("etag") @ApiParam(value = "entity tag") String eTag,
            @HeaderParam(CLIENT_FINGERPRINT_HEADER) @ApiParam(value = "client fingerprint") UUID fingerprint,
            @HeaderParam(X_FORWARDED_FOR) @ApiParam(value = "list of proxied IPs") String forwardedFor,
            @HeaderParam(HttpHeaders.USER_AGENT) @ApiParam(value = "user agent") String userAgent) {

        ClientIdentity cliendId = getClientIdentity(fingerprint, forwardedFor, userAgent);

        /*
         * subscriptions.get(cliendId);
         *
         * EventOutput eventOutput = eventOutputs.get(subscriptionId);
         * if (!subscriptions.exists(subscriptionId) || eventOutput == null) {
         * return JSONResponse.createResponse(Status.GONE, null,
         * "Subscription id " + subscriptionId + " does not exist.");
         * }
         */
        logger.debug("Client {} requested sitemap event stream for entity {}.", fingerprint, eTag);

        // Disables proxy buffering when using an nginx http server proxy for this response.
        // This allows you to not disable proxy buffering in nginx and still have working sse
        response.addHeader(X_ACCEL_BUFFERING_HEADER, "no");

        return null; // eventOutput;
    }

    /**
     * Build a ClientIdentity based on the request metadata.
     *
     * @param fingerprint Client-provided fingerprint.
     * @param forwardedFor X-Forwarded-For header value.
     * @param userAgent User-Agent header value.
     * @return ClientIdentity instance based on the provided parameters.
     */
    private ClientIdentity getClientIdentity(UUID fingerprint, String forwardedFor, String userAgent) {
        // try to get the UUID from the fingerprint
        UUID uuid = fingerprint;
        if (uuid == null) {
            // try to get the UUID from the original client IP
            try {
                if (forwardedFor != null && forwardedFor.length() > 0) {
                    String originalIP = forwardedFor.split(",", 1)[0];
                    if (originalIP != null) {
                        uuid = UUID.fromString(originalIP);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                logger.warn("Invalid {} value: {}", X_FORWARDED_FOR, forwardedFor);
            }
        }
        if (uuid == null) {
            // get the UUID from the final request remote IP
            uuid = UUID.fromString(request.getRemoteAddr());
        }
        return new ClientIdentity(uuid, userAgent);
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }
}
