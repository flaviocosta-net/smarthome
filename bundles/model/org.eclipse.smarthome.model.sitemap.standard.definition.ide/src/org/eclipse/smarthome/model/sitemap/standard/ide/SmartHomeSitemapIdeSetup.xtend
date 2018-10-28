/*
 * generated by Xtext 2.12.0
 */
package org.eclipse.smarthome.model.sitemap.standard.ide

import com.google.inject.Guice
import org.eclipse.smarthome.model.sitemap.standard.SmartHomeSitemapRuntimeModule
import org.eclipse.smarthome.model.sitemap.standard.SmartHomeSitemapStandaloneSetup
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages as language servers.
 */
class SmartHomeSitemapIdeSetup extends SmartHomeSitemapStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new SmartHomeSitemapRuntimeModule, new SmartHomeSitemapIdeModule))
	}
	
}
