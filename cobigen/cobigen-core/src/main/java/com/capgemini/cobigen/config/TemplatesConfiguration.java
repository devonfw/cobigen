package com.capgemini.cobigen.config;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.reader.TemplatesConfigurationReader;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 * The {@link TemplatesConfiguration} is a configuration data wrapper for all information of a context about
 * templates and the target destination for the generated data.
 *
 * @author trippl (04.04.2013)
 */
public class TemplatesConfiguration {

    /**
     * Folder name of the context definition (root folder for all templates)
     */
    private String templatesFolderName;

    /**
     * All available templates
     */
    private Map<String, Template> templates;

    /**
     * All available increments
     */
    private Map<String, Increment> increments;

    /**
     * {@link Trigger}, all templates of this configuration depend on
     */
    private Trigger trigger;

    /**
     * {@link ITriggerInterpreter} the trigger has been interpreted with
     */
    private ITriggerInterpreter triggerInterpreter;

    /**
     * Creates a new {@link TemplatesConfiguration} for the given template folder with the given settings
     * reference
     *
     * @param configRoot
     *            configuration root path
     * @param trigger
     *            {@link Trigger} of this {@link TemplatesConfiguration}
     * @param triggerInterpreter
     *            the trigger has been interpreted with
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if the given templates.xml is not valid
     * @author trippl (04.04.2013)
     */
    public TemplatesConfiguration(Path configRoot, Trigger trigger, ITriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(configRoot.resolve(trigger.getTemplateFolder()));
        templatesFolderName = trigger.getTemplateFolder();
        templates = reader.loadTemplates(trigger, triggerInterpreter);
        increments = reader.loadIncrements(templates, trigger);
        this.trigger = trigger;
        this.triggerInterpreter = triggerInterpreter;
    }

    /**
     * Returns the {@link Template} with the given id
     *
     * @param id
     *            of the {@link Template} to be searched for
     * @return the {@link Template} with the given id or <code>null</code> if there is no
     * @author mbrunnli (09.04.2014)
     */
    public Template getTemplate(String id) {

        return templates.get(id);
    }

    /**
     * Returns the set of all available templates
     *
     * @return the set of all available templates
     * @author mbrunnli (12.02.2013)
     */
    public Set<Template> getAllTemplates() {

        return new HashSet<>(templates.values());
    }

    /**
     * Returns the {@link Trigger}, this {@link TemplatesConfiguration} is related to
     *
     * @return the {@link Trigger}, this {@link TemplatesConfiguration} is related to
     * @author mbrunnli (09.04.2014)
     */
    public Trigger getTrigger() {

        return trigger;
    }

    /**
     * Returns the field 'triggerInterpreter'
     * @return value of triggerInterpreter
     * @author mbrunnli (16.10.2014)
     */
    public ITriggerInterpreter getTriggerInterpreter() {
        return triggerInterpreter;
    }

    /**
     * Returns the set of all available increments
     *
     * @return the set of all available increments
     * @author trippl (25.02.2013)
     */
    public List<Increment> getAllGenerationPackages() {

        return new LinkedList<>(increments.values());
    }

    /**
     * Returns the folder name of this context definition (root folder for all templates)
     *
     * @return the folder name of this context definition (root folder for all templates)
     * @author mbrunnli (05.04.2013)
     */
    public String getTemplatesFolderName() {

        return templatesFolderName;
    }

}
