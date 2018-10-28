/**
 *
 */
package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.state;

import java.util.Map;

import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapTranslationProvider;
import org.eclipse.smarthome.model.sitemap.rendering.container.Container;

/**
 * Implementation of Atom component states based on EOL expressions.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 * @param <D> Component data type.
 */
public class ContainerComponentState<D> extends AbstractComponentState<D> {

    /**
     * Constructor that sets the container state parameters from the sitemap.
     *
     * @param translator Translation provider for labels.
     * @param container Sitemap container.
     * @param style Styles defined in the container.
     * @param context Execution context.
     */
    public ContainerComponentState(SitemapTranslationProvider translator, Container<D, ?> container,
            Map<String, Expression> style, IEtlContext context) {
        super(translator, container, style, context);
    }

    @Override
    protected void handleDependencyChange() {
        handleStyleDependencyChange();
    }
}
