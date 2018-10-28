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

import org.eclipse.smarthome.core.events.AbstractEventFactory;
import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventFactory;
import org.eclipse.smarthome.io.json.JsonBindingService;
import org.eclipse.smarthome.model.sitemap.rendering.atom.Atom;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber.EventAction;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.beans.AtomEventPayloadBean;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.beans.SitemapEventPayloadBean;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.beans.StyleEventPayloadBean;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Responsible for creating sitemap rendering model event instances.
 *
 * @author Flavio Costa - Initial contribution.
 */
@Component(service = { EventFactory.class, SitemapEventFactory.class })
public class SitemapEventFactory extends AbstractEventFactory {

    /**
     * Root for all sitemap topics. The parameters are Sitemap type/Sitemap Id.
     */
    public static final String SITEMAP_TOPIC_ROOT = "smarthome/sitemaps/%s/%s/";

    /**
     * JSON data binding service.
     */
    private JsonBindingService bindingService;

    @Reference
    void setJsonBindingService(JsonBindingService binding) {
        this.bindingService = binding;
    }

    void unsetJsonBindingService(JsonBindingService binding) {
        this.bindingService = null;
    }

    /**
     * Default constructor to set the supported event types.
     */
    public SitemapEventFactory() {
        super(AtomEvent.TYPE, StyleEvent.TYPE, RenderingModelEvent.TYPE);
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, String source) throws Exception {

        AbstractSitemapEvent event = null;
        String[] topicParts = topic.split("/");
        // the last part of the topic indicates the EventAction
        EventAction action = EventAction.valueOf(topicParts[topicParts.length - 1]);
        if (AtomEvent.TYPE.equals(eventType)) {
            AtomEventPayloadBean<?> bean = deserializePayloadBean(payload, AtomEventPayloadBean.class);
            // sample atom topic: smarthome/sitemaps/smarthome/newdemo/~sl0002/CHANGED
            event = new AtomEvent<>(action, topic, payload, source, topicParts[topicParts.length - 2],
                    bean.getNewData(), bean.getPreviousData());
        } else if (StyleEvent.TYPE.equals(eventType)) {
            StyleEventPayloadBean bean = deserializePayloadBean(payload, StyleEventPayloadBean.class);
            event = new StyleEvent(action, topic, payload, source, bean.getStyleName(), bean.getNewValue());
        } else if (RenderingModelEvent.TYPE.equals(eventType)) {
            SitemapEventPayloadBean bean = deserializePayloadBean(payload, SitemapEventPayloadBean.class);
            event = new RenderingModelEvent(action, topic, payload, source, bean.getId());
        }
        if (event == null) {
            throw new UnsupportedOperationException("Unsupported event type: " + eventType);
        }
        return event;
    }

    /**
     * Deserializes the payload received on an event.
     *
     * @param json JSON String representing the serialized payload.
     * @param beanClass Payload bean type to be deserialized.
     * @return Payload bean that was deserialized.
     */
    private <T> T deserializePayloadBean(String json, Class<T> beanClass) {
        return bindingService.fromJson(json, beanClass);
    }

    /**
     * Serializes the payload to be sent on an event.
     *
     * @param bean Payload bean to be serialized.
     * @return JSON String representing the serialized payload.
     */
    private String serializePayloadBean(Object bean) {
        return bindingService.toJson(bean);
    }

    /**
     * Creates an Atom Event.
     *
     * @param eventAction Action of the event.
     * @param sitemap Sitemap instance.
     * @param container Atom Container instance.
     * @param atom Atom instance.
     * @param previousData Previous data of the atom.
     * @return New Atom Event instance based on the provided parameters.
     */
    public <D> AtomEvent<D> createAtomEvent(EventAction eventAction, Sitemap sitemap, Container<?, ?> container,
            Atom<D> atom, D previousData) {
        // TODO "smarthome" should be the sitemap type (smarthome, registry, etc.)
        String topic = AtomEvent.buildTopic("smarthome", sitemap.getId(), atom.getId(), eventAction.toString());
        String payload = serializePayloadBean(new AtomEventPayloadBean<D>(atom, previousData));
        return new AtomEvent<D>(eventAction, topic, payload, SitemapEventFactory.class.getSimpleName(), atom.getId(),
                atom.getData(), previousData);
    }

    public <D> Event createStyleEvent(EventAction eventAction, Sitemap sitemap,
            org.eclipse.smarthome.model.sitemap.rendering.Component<D> component, String styleName) {
        // TODO "smarthome" should be the sitemap type (smarthome, registry, etc.)
        String topic = StyleEvent.buildTopic("smarthome", sitemap.getId(), component.getId(), eventAction.toString());
        String payload = serializePayloadBean(new StyleEventPayloadBean(component, styleName));
        return new StyleEvent(eventAction, topic, payload, SitemapEventFactory.class.getSimpleName(), styleName,
                component.getStyleMap().get(styleName));
    }

    /**
     * Creates a Sitemap Event.
     *
     * @param eventAction Action of the event.
     * @param sitemap Sitemap instance.
     * @return New Sitemap Event instance based on the provided parameters.
     */
    public Event createSitemapEvent(EventAction eventAction, Sitemap sitemap) {
        // TODO "smarthome" should be the sitemap type (smarthome, registry, etc.)
        String topic = RenderingModelEvent.buildTopic("smarthome", sitemap.getId(), eventAction.toString());
        String payload = serializePayloadBean(new SitemapEventPayloadBean(sitemap));
        return new RenderingModelEvent(eventAction, topic, payload, SitemapEventFactory.class.getSimpleName(),
                sitemap.getId());
    }
}
