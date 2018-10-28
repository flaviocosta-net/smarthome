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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.smarthome.core.auth.Role;
import org.eclipse.smarthome.io.rest.LocaleService;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.ComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;
import org.eclipse.smarthome.model.sitemap.rendering.style.StylePropertyValue;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class acts as a REST resource for sitemap rendering models and provides different methods to interact with them.
 *
 * @author Flavio Costa - Initial contribution and API
 */
@Component(service = RESTResource.class)
@Path(SitemapRenderingResource.PATH_SITEMAP_RENDERING)
@RolesAllowed({ Role.USER, Role.ADMIN })
@Api(value = SitemapRenderingResource.PATH_SITEMAP_RENDERING)
public class SitemapRenderingResource implements RESTResource {

    private final Logger logger = LoggerFactory.getLogger(SitemapRenderingResource.class);

    public static final String PATH_RENDERING = "rendering";

    public static final String PATH_SITEMAP_RENDERING = "sitemaps/" + PATH_RENDERING;

    public static final String PATH_STATE = "state";

    @Context
    UriInfo uriInfo;

    @Context
    private HttpServletResponse response;

    private LocaleService localeService;

    private final Map<String, SitemapRenderingProvider> renderingProviders = new HashMap<>();

    @Activate
    protected void activate() {
    }

    @Deactivate
    protected void deactivate() {
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addRenderingProvider(SitemapRenderingProvider provider) {
        renderingProviders.put(provider.getSitemapType(), provider);
    }

    public void removeRenderingProvider(SitemapRenderingProvider provider) {
        renderingProviders.remove(provider.getSitemapType(), provider);
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    protected void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    protected void unsetLocaleService(LocaleService localeService) {
        this.localeService = null;
    }

    private Set<String> getSitemapTypes(String sitemapName) {

        Set<String> types = new HashSet<>();

        for (SitemapRenderingProvider provider : renderingProviders.values()) {
            if (provider.getSitemapNames().contains(sitemapName)) {
                types.add(provider.getSitemapType());
            }
        }

        return types;
    }

    private RenderingModel getRenderingModel(String sitemapName) {
        Set<String> types = getSitemapTypes(sitemapName);
        switch (types.size()) {
            case 0:
                logger.info("Received HTTP GET request at '{}' for the unknown sitemap '{}'.", uriInfo.getPath(),
                        sitemapName);
                throw new WebApplicationException(404);
            case 1:
                String sitemapType = types.iterator().next();
                RenderingModel model = renderingProviders.get(sitemapType).getRenderingModel(sitemapName);
                if (model == null) {
                    throw new WebApplicationException("Rendering model not available for sitemap '" + sitemapName + "'",
                            500);
                }
                return model;
            default:
                logger.warn("HTTP GET request at '{}' matches multiple sitemap types: '{}'.", uriInfo.getPath(), types);
                String message = String.format("Multiple Choices for '%s': %s", sitemapName, types.toString());
                throw new WebApplicationException(message, 300);
        }

    }

    private <D> ComponentStateDTO<D> mapDTO(Locale locale, ComponentState<D> componentState) {
        ComponentStateDTO<D> dto = null;
        org.eclipse.smarthome.model.sitemap.rendering.Component<D> component = componentState.getComponent();
        String displayValue = componentState.getDisplayValue(locale);
        Map<String, StylePropertyValue> style = component.getStyleMap();
        if (displayValue != null || style != null) {
            dto = new ComponentStateDTO<>();
            dto.id = component.getId();
            dto.data = component.getData();
            dto.style = style;
            dto.displayValue = displayValue;
        }
        return dto;
    }

    @GET
    @Path("/{sitemapname: [a-zA-Z_0-9]*}/" + PATH_STATE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get rendering model state by sitemap name.", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 300, message = "Multiple sitemaps defined for the id provided."),
            @ApiResponse(code = 304, message = "Sitemap in cache has not changed."),
            @ApiResponse(code = 404, message = "Sitemap with requested name does not exist."),
            @ApiResponse(code = 500, message = "Sitemap rendering failed.") })
    public Response getRenderingModelState(@Context HttpHeaders headers,
            @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) @ApiParam(value = "language") String language,
            @PathParam("sitemapname") @ApiParam(value = "sitemap name") String sitemapName,
            @QueryParam("type") String type) {
        logger.debug("Received HTTP GET request at '{}' for media type '{}'", uriInfo.getPath(), type);

        final Locale locale = localeService.getLocale(language);

        RenderingModelState state = getRenderingModel(sitemapName).getState();

        List<ComponentStateDTO<?>> componentStates = state.getComponentStates().parallelStream()
                .map(s -> mapDTO(locale, s)).filter(Objects::nonNull).collect(Collectors.toList());

        return Response.ok(componentStates).build();
    }

    @GET
    @Path("/{sitemapname: [a-zA-Z_0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get rendering model by sitemap name.", response = Sitemap.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 300, message = "Multiple sitemaps defined for the id provided."),
            @ApiResponse(code = 304, message = "Sitemap in cache has not changed."),
            @ApiResponse(code = 404, message = "Sitemap with requested name does not exist."),
            @ApiResponse(code = 500, message = "Sitemap rendering failed.") })
    public Response getRenderingModel(@Context HttpHeaders headers,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) @ApiParam(value = "cached rendering model ETag") EntityTag eTag,
            @PathParam("sitemapname") @ApiParam(value = "sitemap name") String sitemapName,
            @QueryParam("type") String type) {
        logger.debug("Received HTTP GET request at '{}' for media type '{}'", uriInfo.getPath(), type);

        Sitemap sitemap = getRenderingModel(sitemapName).getSitemap();
        EntityTag entityTag = new EntityTag(String.valueOf(sitemap.hashCode()));
        // ResponseBuilder builder = request.evaluatePreconditions(tag);
        // if (builder != null) {
        // TODO this instead of the line below?
        if (entityTag.equals(eTag)) {
            return Response.notModified(entityTag).build();
        }
        if (!sitemapName.equals(sitemap.getId())) {
            logger.warn("Id '{}' defined in the sitemap does not match the name '{}'", sitemap.getId(), sitemapName);
        }
        return Response.ok(sitemap).tag(entityTag).build();
    }

    @Override
    public boolean isSatisfied() {
        return localeService != null;
    }
}
