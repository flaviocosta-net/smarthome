package org.eclipse.smarthome.model.sitemap.definition.runtime.internal.i18n;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.ConfigConstants;
import org.eclipse.smarthome.model.sitemap.definition.runtime.SitemapTranslationProvider;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation that obtains translation for sitemaps from ResourceBundles.
 *
 * @author Flavio Costa - Initial implementation
 */
@Component(immediate = true)
public class SitemapResourceBundleTranslationProvider implements SitemapTranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClassLoader classLoader;

    public SitemapResourceBundleTranslationProvider() throws MalformedURLException {
        // TODO For now "sitemaps" is hardcoded
        URL sitemapUrlPath = Paths.get(ConfigConstants.getConfigFolder(), "sitemaps").toUri().toURL();
        URL[] urls = new URL[] { sitemapUrlPath };
        classLoader = new URLClassLoader(urls);
    }

    @Override
    public String getText(String sitemapId, String componentId, String defaultText, Locale locale) {
        return getText(sitemapId, componentId, defaultText, locale, (Object[]) null);
    }

    @Override
    public String getText(String sitemapId, String componentId, String defaultText, Locale locale,
            @Nullable Object @Nullable... arguments) {
        ResourceBundle resource = ResourceBundle.getBundle(sitemapId, locale, classLoader);
        String key = String.format("%s.label", componentId);
        String text = resource.getString(key);
        if (text == null) {
            return defaultText;
        }
        if (arguments != null) {
            text = MessageFormat.format(text, arguments);
        }
        return text;
    }
}
