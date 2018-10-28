package org.eclipse.smarthome.model.sitemap.definition.runtime;

import java.util.Locale;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This interface exists in parallel to the TranslationProvider interface because the method signatures there are not
 * pertinent to sitemap translations, as sitemaps are not provided in bundles, and introducing a new implementation for
 * TranslationProvider causes conflicts elsewhere in the framework.
 *
 * @author Flavio Costa - Initial implementation and API
 */
public interface SitemapTranslationProvider {

    public String getText(String sitemapId, String componentId, String defaultText, Locale locale);

    public String getText(String sitemapId, String componentId, String defaultText, Locale locale,
            @Nullable Object @Nullable... arguments);

}
