/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.entity;

import com.capgemini.cobigen.config.resolver.PathExpressionResolver;

/**
 * Storage class for template data provided within the config.xml
 * @author trippl (07.03.2013)
 * 
 */
public class Template {

    /**
     * Identifies the {@link Template}.
     */
    private String id;

    /**
     * Relative path for the result.
     */
    private String destinationPath;

    /**
     * Relative path to the template file.
     */
    private String templateFile;

    /**
     * {@link PathExpressionResolver} for resolving the destination path variables
     */
    private PathExpressionResolver expressionResolver;

    /**
     * Determines the required strategy to merge the {@link Template}
     */
    private String mergeStrategy;

    /**
     * Charset of the target file
     */
    private String targetCharset;

    /**
     * {@link Trigger} the template is dependent on
     */
    private Trigger trigger;

    /**
     * Creates a new {@link Template} for the given data
     * @param id
     *            template ID
     * @param destinationPath
     *            path of the destination file
     * @param templateFile
     *            relative path of the template file
     * @param mergeStrategy
     *            for the template
     * @param outputCharset
     *            output charset for the generated contents
     * @param expressionResolver
     *            {@link PathExpressionResolver} to resolve the destination paths
     * @param trigger
     *            {@link Trigger} the template is dependent on
     */
    public Template(String id, String destinationPath, String templateFile, String mergeStrategy,
        String outputCharset, PathExpressionResolver expressionResolver, Trigger trigger) {
        this.id = id;
        this.destinationPath = destinationPath;
        this.templateFile = templateFile;
        this.expressionResolver = expressionResolver;
        this.mergeStrategy = mergeStrategy;
        targetCharset = outputCharset;
        this.trigger = trigger;
    }

    /**
     * Returns the {@link Template}'s {@link #id}
     * @return the template id
     * @author trippl (07.03.2013)
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the {@link Template}'s {@link #destinationPath}
     * @return the destination path
     * @author trippl (07.03.2013)
     */
    public String getDestinationPath() {
        return expressionResolver.evaluateExpressions(destinationPath);
    }

    /**
     * Returns the path to the {@link #templateFile}
     * @return the relative path to the templateFile
     * @author trippl (07.03.2013)
     */
    public String getTemplateFile() {
        return templateFile;
    }

    /**
     * Returns the {@link #mergeStrategy} for the {@link Template}
     * @return the merge strategy
     * @author trippl (12.03.2013)
     */
    public String getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * Returns the output charset for this template
     * @return the output charset for this template
     * @author mbrunnli (26.03.2013)
     */
    public String getTargetCharset() {
        return targetCharset;
    }

    /**
     * Returns the {@link Trigger} the template is dependent on
     * @return the {@link Trigger} the template is dependent on
     * @author mbrunnli (05.04.2013)
     */
    public Trigger getTrigger() {
        return trigger;
    }
}
