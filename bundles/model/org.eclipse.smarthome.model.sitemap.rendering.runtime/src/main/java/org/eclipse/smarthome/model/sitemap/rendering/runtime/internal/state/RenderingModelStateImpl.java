package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.thing.events.ThingStatusInfoChangedEvent;
import org.eclipse.smarthome.model.sitemap.rendering.Component;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.events.SitemapEventSubscriber;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.ComponentState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;

/**
 * Default rendering model state implementation.
 *
 * @author Flavio Costa - Initial contribution
 */
public class RenderingModelStateImpl implements RenderingModelState {

    /**
     * Component states.
     */
    private final Map<Component<?>, ComponentState<?>> states = new HashMap<>();

    /**
     * Event subscribers.
     */
    private final transient Set<SitemapEventSubscriber> subscribers;

    /**
     * Creates a new rendering model state.
     *
     * @param subscribers Subscribers for sitemap events.
     */
    public RenderingModelStateImpl(Set<SitemapEventSubscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void add(ComponentState<?> state) {
        states.put(state.getComponent(), state);
        state.setSubscribers(subscribers);
    }

    @Override
    public void remove(ComponentState<?> state) {
        states.remove(state.getComponent());
        state.setSubscribers(Collections.emptySet());
    }

    @Override
    public Collection<ComponentState<?>> getComponentStates() {
        return Collections.unmodifiableCollection(states.values());
    }

    @Override
    public void itemStateChanged(ItemStateChangedEvent event) {
        states.values().forEach(cs -> cs.itemStateChanged(event));
    }

    @Override
    public void thingStatusChanged(ThingStatusInfoChangedEvent event) {
        states.values().forEach(cs -> cs.thingStatusChanged(event));
    }
}
