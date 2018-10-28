package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal;

import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;

/**
 * Default rendering model implementation.
 *
 * @author Flavio Costa - Initial contribution
 */
public class RenderingModelImpl implements RenderingModel {

    /**
     * Sitemap model.
     */
    private final Sitemap sitemap;

    /**
     * State for this rendering model
     */
    private final RenderingModelState state;

    /**
     * Creates a new rendering model.
     *
     * @param sitemap Sitemap model.
     * @param state Model state.
     */
    public RenderingModelImpl(Sitemap sitemap, RenderingModelState state) {
        this.sitemap = sitemap;
        this.state = state;
    }

    @Override
    public Sitemap getSitemap() {
        return sitemap;
    }

    @Override
    public RenderingModelState getState() {
        return state;
    }
}
