package com.capgemini.cobigen.impl.config.entity;

/** Storage class for template data provided within the config.xml */
public class Template {

    /** Identifies the {@link Template}. */
    private String name;

    /** Relative path to the template file. */
    private String templateFile;

    /** Determines the required strategy to merge the {@link Template} */
    private String mergeStrategy;

    /** Charset of the target file */
    private String targetCharset;

    /** Relative path for the result. */
    private String unresolvedDestinationPath;

    /**
     * Creates a new {@link Template} for the given data
     * @param name
     *            template name
     * @param unresolvedDestinationPath
     *            path of the destination file
     * @param templateFile
     *            relative path of the template file
     * @param mergeStrategy
     *            for the template
     * @param outputCharset
     *            output charset for the generated contents
     */
    public Template(String name, String unresolvedDestinationPath, String templateFile, String mergeStrategy,
        String outputCharset) {
        this.name = name;
        this.templateFile = templateFile;
        this.mergeStrategy = mergeStrategy;
        targetCharset = outputCharset;
        this.unresolvedDestinationPath = unresolvedDestinationPath;
    }

    /**
     * Returns the {@link Template}'s {@link #name}
     * @return the template name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the {@link #templateFile}
     * @return the relative path to the templateFile
     */
    public String getTemplateFile() {
        return templateFile;
    }

    /**
     * Returns the {@link #mergeStrategy} for the {@link Template}
     * @return the merge strategy
     */
    public String getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * Sets the {@link #mergeStrategy} for the {@link Template}
     * @param mergeStrategy
     *            the {@link #mergeStrategy} for the {@link Template}
     */
    public void setMergeStrategy(String mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
    }

    /**
     * Returns the output charset for this template
     * @return the output charset for this template
     */
    public String getTargetCharset() {
        return targetCharset;
    }

    /**
     * Sets the output charset for this template
     * @param targetCharset
     *            the output charset for this template
     */
    public void setTargetCharset(String targetCharset) {
        this.targetCharset = targetCharset;
    }

    /**
     * Returns the unresolved destination path defined in the templates configuration
     * @return the unresolved destination path
     */
    public String getUnresolvedDestinationPath() {
        return unresolvedDestinationPath;
    }

    /**
     * Sets the unresolved destination path defined in the templates configuration
     * @param unresolvedDestinationPath
     *            the unresolved destination path
     */
    public void setUnresolvedDestinationPath(String unresolvedDestinationPath) {
        this.unresolvedDestinationPath = unresolvedDestinationPath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name='" + getName() + "]";
    }

}
