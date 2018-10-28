package org.eclipse.smarthome.io.rest.sitemap.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a data transfer object that is used to serialize page content for pages of classic sitemaps.
 *
 * @author Flavio Costa - Initial contribution and API
 *
 */
public class ClassicPageDTO extends PageDTO {

    public ClassicPageDTO parent;
    public boolean leaf;
    public boolean timeout;

    public List<WidgetDTO> widgets = new ArrayList<WidgetDTO>();

    public ClassicPageDTO() {
    }
}
