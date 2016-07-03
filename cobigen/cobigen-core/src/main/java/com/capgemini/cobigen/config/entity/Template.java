package com.capgemini.cobigen.config.entity;

import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 * Storage class for template data provided within the config.xml
 * @author trippl (07.03.2013)
 *
 */
public class Template extends AbstractTemplateResolver {

    /**
     * Identifies the {@link Template}.
     */
    private String name;

    /**
     * Relative path to the template file.
     */
    private String templateFile;

    /**
     * Determines the required strategy to merge the {@link Template}
     */
    private String mergeStrategy;

    /**
     * Charset of the target file
     */
    private String targetCharset;

    /**
     * Creates a new {@link Template} for the given data
     * @param name
     *            template name
     * @param destinationPath
     *            path of the destination file
     * @param templateFile
     *            relative path of the template file
     * @param mergeStrategy
     *            for the template
     * @param outputCharset
     *            output charset for the generated contents
     * @param trigger
     *            {@link Trigger} the template is dependent on
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} the trigger has been interpreted with
     */
    public Template(String name, String destinationPath, String templateFile, String mergeStrategy,
        String outputCharset, Trigger trigger, ITriggerInterpreter triggerInterpreter) {
        super(destinationPath, trigger, triggerInterpreter);
        this.name = name;
        this.templateFile = templateFile;
        this.mergeStrategy = mergeStrategy;
        targetCharset = outputCharset;
    }

    /**
     * Returns the {@link Template}'s {@link #name}
     * @return the template name
     * @author trippl (07.03.2013)
     */
    public String getName() {
        return name;
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
     * Sets the {@link #mergeStrategy} for the {@link Template}
     * @param mergeStrategy
     *            the {@link #mergeStrategy} for the {@link Template}
     * @author mbrunnli (07.12.2014)
     */
    public void setMergeStrategy(String mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
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
     * Sets the output charset for this template
     * @param targetCharset
     *            the output charset for this template
     * @author mbrunnli (07.12.2014)
     */
    public void setTargetCharset(String targetCharset) {
        this.targetCharset = targetCharset;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 25, 2015)
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name='" + getName() + "]";
    }

}
