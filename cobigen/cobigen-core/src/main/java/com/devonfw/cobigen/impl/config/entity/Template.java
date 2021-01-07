package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;

import com.devonfw.cobigen.api.extension.TextTemplate;

/** Storage class for template data provided within the config.xml */
public class Template implements TextTemplate {

    /** Identifies the {@link Template}. */
    private final String name;

    /** The {@link TemplateFile} pointing to the physical template file. */
    private final TemplateFile templateFile;

    /**
     * Relative non-canonical path of the final target file to generate to. Path variables are not resolved.
     * Relocates are resolved.
     */
    private String unresolvedTargetPath;

    /**
     * See {@link #getUnresolvedTemplatePath()}
     */
    private String unresolvedTemplatePath;

    /** Charset of the target file */
    private String targetCharset;

    /** Determines the required strategy to merge the {@link Template} */
    private String mergeStrategy;

    /**
     * Creates a new {@link Template} for the given data
     * @param templateFile
     *            the {@link TemplateFile} pointing to the physical template file.
     * @param name
     *            template name
     * @param unresolvedDestinationPath
     *            path of the destination file
     * @param unresolvedTemplatePath
     *            relative path of the actual file to generate to. Path variables and relocates are not
     *            resolved.
     * @param mergeStrategy
     *            for the template
     * @param outputCharset
     *            output charset for the generated contents
     */
    public Template(TemplateFile templateFile, String name, String unresolvedDestinationPath,
        String unresolvedTemplatePath, String mergeStrategy, String outputCharset) {
        this.templateFile = templateFile;
        this.name = name;
        this.mergeStrategy = mergeStrategy;
        targetCharset = outputCharset;
        unresolvedTargetPath = unresolvedDestinationPath;
        this.unresolvedTemplatePath = unresolvedTemplatePath;
    }

    /**
     * @return the {@link Template}'s name (a unique ID).
     */
    public String getName() {
        return name;
    }

    /**
     * @return the relative path to the template file.
     */
    @Override
    public String getRelativeTemplatePath() {
        return templateFile.toString();
    }

    /**
     * @return the strategy used when the target file already exists and has to be merged with the generated
     *         file.
     */
    public String getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * @param mergeStrategy
     *            the new value of {@link #getMergeStrategy()}.
     */
    public void setMergeStrategy(String mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
    }

    /**
     * @return the output charset for this template
     */
    public String getTargetCharset() {
        return targetCharset;
    }

    /**
     * @param targetCharset
     *            the new value of {@link #getTargetCharset()}.
     */
    public void setTargetCharset(String targetCharset) {
        this.targetCharset = targetCharset;
    }

    /**
     * Returns the relative non-canonical path of the final target file to generate to. Path variables are not
     * resolved. Relocates are resolved.
     * @return the unresolved destination path
     */
    public String getUnresolvedTargetPath() {
        return unresolvedTargetPath;
    }

    /**
     * @param unresolvedTargetPath
     *            the new value of {@link #getUnresolvedTargetPath()}.
     */
    public void setUnresolvedTargetPath(String unresolvedTargetPath) {
        this.unresolvedTargetPath = unresolvedTargetPath;
    }

    /**
     * @param unresolvedTemplatePath
     *            the value of {@link #getUnresolvedTemplatePath()}
     */
    public void setUnresolvedTemplatePath(String unresolvedTemplatePath) {
        this.unresolvedTemplatePath = unresolvedTemplatePath;
    }

    /**
     * @return the relative path of the final target file to generate to. Path variables and relocates are not
     *         resolved.
     */
    public String getUnresolvedTemplatePath() {
        return unresolvedTemplatePath;
    }

    /**
     * @return the absolute file path to the template
     */
    @Override
    public Path getAbsoluteTemplatePath() {
        return templateFile.getPath();
    }

    /**
     * @return the {@link Variables} with the variables for this template.
     * @see TemplateFolder#getVariables()
     */
    public Variables getVariables() {
        return templateFile.getParent().getVariables();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name='" + getName() + "]";
    }

    @Override
    public int hashCode() {
        return unresolvedTemplatePath.hashCode();
    }
}
