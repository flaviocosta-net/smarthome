/*
 * generated by Xtext 2.12.0
 */
package org.eclipse.smarthome.model.sitemap.standard

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.IResourceServiceProvider

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class SmartHomeSitemapStandaloneSetup extends SmartHomeSitemapStandaloneSetupGenerated {

	def static void doSetup() {
		new SmartHomeSitemapStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
    
    def static void unregister() {
        EPackage.Registry.INSTANCE.remove("http://www.eclipse.org/smarthome/model/sitemap/standard/SmartHomeSitemap");
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().remove("smarthome");
        IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().remove("smarthome");
    }
}
