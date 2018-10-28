package org.eclipse.smarthome.model.sitemap.rendering.runtime.internal.workflow;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.model.sitemap.rendering.ComponentType;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.SitemapRenderingProvider;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context.EolFrame.VarName;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.ModuleSettings;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.workflow.RenderingWorkflow;

/**
 * Rendering workflow implementation that runs EOL modules.
 *
 * @author Flavio Costa - Initial contribution
 */
public class RenderingWorkflowImpl implements RenderingWorkflow {

    /**
     * Name of the sitemap definition source instance.
     */
    protected final String sourceName;

    /**
     * Sitemap definition source object.
     */
    protected final Object source;

    /**
     * Rendering provider.
     */
    protected final @NonNull SitemapRenderingProvider renderingProvider;

    /**
     * Workflow global variables.
     */
    private final Map<String, Variable> variables = new HashMap<>();

    /**
     * Workflow modules.
     */
    private final List<ModuleSettings<?>> modules = new ArrayList<>();

    /**
     * Creates a new rendering workflow instance.
     *
     * @param renderingProvider Rendering provider.
     * @param sitemapName Name of the sitemap source instance.
     * @param source Sitemap source object.
     */
    public RenderingWorkflowImpl(@NonNull SitemapRenderingProvider renderingProvider, String sitemapName,
            Object source) {
        this.renderingProvider = renderingProvider;
        this.sourceName = sitemapName;
        this.source = source;
    }

    @Override
    public String getDefinitionModelName() {
        return sourceName;
    }

    @Override
    public Object getDefinitionObject() {
        return source;
    }

    @Override
    public Collection<Variable> getGlobalVariables() {
        return Collections.unmodifiableCollection(variables.values());
    }

    @Override
    public void addGlobalVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    @Override
    public Variable getGlobalVariable(VarName name) {
        return variables.get(name.toString());
    }

    @Override
    public String getSitemapType() {
        return renderingProvider.getSitemapType();
    }

    @Override
    public <T> ModuleSettings<T> addModule(String scriptPath) throws IllegalArgumentException {
        int dot = scriptPath.lastIndexOf('.');
        return addModule(scriptPath.substring(0, dot), scriptPath.substring(dot + 1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ModuleSettings<T> addModule(String scriptName, String extension) throws IllegalArgumentException {
        Class<?> renderingClass = renderingProvider.getClass();
        URL scriptResource = renderingClass.getResource(scriptName + '.' + extension);
        if (scriptResource == null) {
            throw new IllegalArgumentException(
                    "Script " + scriptName + " not found in package " + renderingClass.getPackage().getName());
        }
        URI uri;
        try {
            uri = scriptResource.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI from " + scriptName);
        }
        ModuleSettings<?> settings;
        switch (extension) {
            case "etl":
                settings = new EtlModuleSettings(this, uri);
                // target is always the same
                Collection<Class<?>> renderingClasses = Arrays.stream(ComponentType.values())
                        .map(ComponentType::getImplementingClass).collect(Collectors.toList());
                settings.addModel("Rendering", renderingClasses);
                break;
            case "epl":
                settings = new EplModuleSettings(this, uri);
                break;
            default:
                throw new IllegalArgumentException("Unsupported script extension: " + extension);
        }
        this.modules.add(settings);
        return (ModuleSettings<T>) settings;
    }

    @Override
    public List<ModuleSettings<?>> getModules() {
        return Collections.unmodifiableList(modules);
    }

    @Override
    public void dispose() {
        modules.forEach(ModuleSettings::dispose);
    }
}
