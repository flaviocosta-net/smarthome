package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.workflow;

import java.net.URI;
import java.util.Collection;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.EolIllegalReturnException;
import org.eclipse.epsilon.eol.exceptions.EolNoReturnException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.FrameStack;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.trace.TransformationTrace;
import org.eclipse.smarthome.model.sitemap.rendering.container.Sitemap;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.RenderingModel;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.RenderingModelImpl;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.state.RenderingModelState;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;

/**
 * Module settings implementation for ETL programs.
 *
 * @author Flavio Costa - Initial contribution.
 */
public class EtlModuleSettings extends AbstractModuleSettings<RenderingModel> {

    /**
     * ETL module instance.
     */
    private EtlModule module;

    /**
     * Sets the workflow and script URI.
     *
     * @param workflow Workflow containing this module
     * @param scriptURI URI for the transformation script.
     */
    public EtlModuleSettings(RenderingWorkflow workflow, URI scriptURI) {
        super(workflow, scriptURI);
    }

    @Override
    public String getModuleType() {
        return "etl";
    }

    @Override
    public IEolModule getModule() {
        return module;
    }

    @Override
    public IEolModule createInstance(ModuleSettings<?> previousModule) {
        return super.createInstance(previousModule, this.module = new EtlModule());
    }

    @Override
    public RenderingModel execute() throws EolRuntimeException {

        RenderingWorkflow workflow = getWorkflow();

        // set global variables from settings
        FrameStack frameStack = module.getContext().getFrameStack();
        workflow.getGlobalVariables().forEach(frameStack::putGlobal);

        Sitemap sitemap = null;
        TransformationTrace trace = (TransformationTrace) module.execute();
        // sitemap root transformation
        Collection<Object> targets = trace.getTransformations(workflow.getDefinitionObject()).getTargets();
        switch (targets.size()) {
            case 0:
                // No target found after rendering sitemap
                throw new EolNoReturnException("RenderingModel", module, module.getContext());
            case 1:
                sitemap = (Sitemap) targets.iterator().next();
                break;
            default:
                // Rendering of sitemap returned too many targets
                throw new EolIllegalReturnException("RenderingModel", targets, module, module.getContext());
        }
        RenderingModelState renderingModelState = (RenderingModelState) workflow.getGlobalVariable(VarName.STATES)
                .getValue();
        return result = new RenderingModelImpl(sitemap, renderingModelState);
    }
}
