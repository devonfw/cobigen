/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config;

import java.io.File;
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
import com.capgemini.cobigen.util.SystemUtil;

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
     * Creates a new {@link TemplatesConfiguration} for the given template folder with the given settings
     * reference
     * 
     * @param file
     *            template folder for this context
     * @param variables
     *            settings reference for resolving the expressions
     * @param trigger
     *            {@link Trigger} of this {@link TemplatesConfiguration}
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if the given templates.xml is not valid
     * @author trippl (04.04.2013)
     */
    public TemplatesConfiguration(File file, Trigger trigger, Map<String, String> variables)
        throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(file.getAbsolutePath() + SystemUtil.FILE_SEPARATOR
                + "templates.xml"));
        templatesFolderName = file.getName();
        templates = reader.loadTemplates(trigger, variables);
        increments = reader.loadIncrements(templates, trigger);
        this.trigger = trigger;
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

        return new HashSet<Template>(templates.values());
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
     * Returns the set of all available increments
     * 
     * @return the set of all available increments
     * @author trippl (25.02.2013)
     */
    public List<Increment> getAllGenerationPackages() {

        return new LinkedList<Increment>(increments.values());
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
