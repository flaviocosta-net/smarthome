package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.workflow;

import java.net.URI;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.PatternMatchModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;

/**
 * Module settings implementation for EPL programs.
 *
 * @author Flavio Costa - Initial contribution.
 */
public class EplModuleSettings extends AbstractModuleSettings<PatternMatchModel> {

    /**
     * EPL module instance.
     */
    private EplModule module;

    /**
     * Sets the workflow and script URI.
     *
     * @param workflow Workflow containing this module
     * @param scriptURI URI for the transformation script.
     */
    public EplModuleSettings(RenderingWorkflow workflow, URI scriptURI) {
        super(workflow, scriptURI);
    }

    @Override
    public String getModuleType() {
        return "epl";
    }

    @Override
    public IEolModule getModule() {
        return module;
    }

    @Override
    public IEolModule createInstance(ModuleSettings<?> previousModule) {
        return super.createInstance(previousModule, this.module = new EplModule());
    }

    @Override
    public PatternMatchModel execute() throws EolRuntimeException {
        return result = (PatternMatchModel) module.execute();
    }
}
