package com.capgemini.cobigen.impl.config;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.exception.UnknownExpressionException;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;
import com.capgemini.cobigen.impl.config.entity.Increment;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.reader.TemplatesConfigurationReader;
import com.capgemini.cobigen.impl.exceptions.UnknownContextVariableException;

/**
 * The {@link TemplatesConfiguration} is a configuration data wrapper for all information of a context about
 * templates and the target destination for the generated data.
 */
public class TemplatesConfiguration {

    /** Folder name of the context definition (root folder for all templates) */
    private String templatesFolderName;

    /** All available templates */
    private Map<String, Template> templates;

    /** All available increments */
    private Map<String, Increment> increments;

    /** {@link Trigger}, all templates of this configuration depend on */
    private Trigger trigger;

    /** {@link TextTemplateEngine} to be used for the template set covered by this configuration. */
    private String templateEngine;

    /**
     * Creates a new {@link TemplatesConfiguration} for the given template folder with the given settings
     * reference
     *
     * @param configRoot
     *            configuration root path
     * @param trigger
     *            {@link Trigger} of this {@link TemplatesConfiguration}
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if the given templates.xml is not valid
     */
    public TemplatesConfiguration(Path configRoot, Trigger trigger) throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(configRoot.resolve(trigger.getTemplateFolder()));
        templatesFolderName = trigger.getTemplateFolder();
        templates = reader.loadTemplates(trigger);
        increments = reader.loadIncrements(templates, trigger);
        templateEngine = reader.getTemplateEngine();
        this.trigger = trigger;
    }

    /**
     * Returns the {@link Template} with the given id
     *
     * @param id
     *            of the {@link Template} to be searched for
     * @return the {@link Template} with the given id or <code>null</code> if there is no
     */
    public Template getTemplate(String id) {
        return templates.get(id);
    }

    /**
     * Returns the set of all available templates
     *
     * @return the set of all available templates
     */
    public Set<Template> getAllTemplates() {
        return new HashSet<>(templates.values());
    }

    /**
     * Returns the {@link Trigger}, this {@link TemplatesConfiguration} is related to
     * @return the {@link Trigger}, this {@link TemplatesConfiguration} is related to
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Returns the set of all available increments
     *
     * @return the set of all available increments
     */
    public List<Increment> getAllGenerationPackages() {
        return new LinkedList<>(increments.values());
    }

    /**
     * Returns the folder name of this context definition (root folder for all templates)
     * @return the folder name of this context definition (root folder for all templates)
     */
    public String getTemplatesFolderName() {
        return templatesFolderName;
    }

    /**
     * Returns the configured template engine
     * @return the template engine name to be used
     */
    public String getTemplateEngine() {
        return templateEngine;
    }
}
