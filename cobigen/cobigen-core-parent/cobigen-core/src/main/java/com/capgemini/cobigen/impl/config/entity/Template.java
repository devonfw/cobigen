package com.capgemini.cobigen.impl.config.entity;

import java.nio.file.Path;

import com.capgemini.cobigen.api.extension.TextTemplate;

/** Storage class for template data provided within the config.xml */
public class Template implements TextTemplate {

    /** Identifies the {@link Template}. */
    private String name;

    /** Determines the required strategy to merge the {@link Template} */
    private String mergeStrategy;

    /** Charset of the target file */
    private String targetCharset;

    /**
     * Relative non-canonical path of the final target file to generate to. Path variables are not resolved.
     * Relocates are resolved.
     */
    private String unresolvedTargetPath;

    /**
     * Relative path of the final target file to generate to. Path variables and relocates are not resolved.
     */
    private String unresolvedTemplatePath;

    /** Relative path to the template file. */
    private String relativeTemplatePath;

    /** Absolute path to the template file. */
    private Path absoluteTemplatePath;

    private TemplateFolder parentFolder;

    /**
     * Creates a new {@link Template} for the given data
     * @param name
     *            template name
     * @param unresolvedDestinationPath
     *            path of the destination file
     * @param relativeTemplatePath
     *            path of the template file relative to the template folder
     * @param mergeStrategy
     *            for the template
     * @param outputCharset
     *            output charset for the generated contents
     * @param absoluteTemplatePath
     *            absolute file path pointing to the template file.
     */
    public Template(String name, String unresolvedDestinationPath, String relativeTemplatePath, String mergeStrategy,
        String outputCharset, Path absoluteTemplatePath) {
        this.name = name;
        this.relativeTemplatePath = relativeTemplatePath;
        this.mergeStrategy = mergeStrategy;
        targetCharset = outputCharset;
        unresolvedTargetPath = unresolvedDestinationPath;
        this.absoluteTemplatePath = absoluteTemplatePath;
    }

    /**
     * Returns the {@link Template}'s {@link #name}
     * @return the template name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the {@link #relativeTemplatePath}
     * @return the relative path to the templateFile
     */
    @Override
    public String getRelativeTemplatePath() {
        return relativeTemplatePath;
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
     * Returns the relative non-canonical path of the final target file to generate to. Path variables are not
     * resolved. Relocates are resolved.
     * @return the unresolved destination path
     */
    public String getUnresolvedTargetPath() {
        return unresolvedTargetPath;
    }

    /**
     * Sets the relative non-canonical path of the final target file to generate to. Path variables are not
     * resolved. Relocates are resolved.
     * @param unresolvedTargetPath
     *            the unresolved destination path
     */
    public void setUnresolvedTargetPath(String unresolvedTargetPath) {
        this.unresolvedTargetPath = unresolvedTargetPath;
    }

    /**
     * Returns the relative path of the final target file to generate to. Path variables and relocates are not
     * resolved.
     * @return the unresolved template path.
     */
    public String getUnresolvedTemplatePath() {
        return unresolvedTemplatePath;
    }

    /**
     * Sets the relative path of the final target file to generate to. Path variables and relocates are not
     * resolved.
     * @param unresolvedTemplatePath
     *            the unresolved template path.
     */
    public void setUnresolvedTemplatePath(String unresolvedTemplatePath) {
        this.unresolvedTemplatePath = unresolvedTemplatePath;
    }

    /**
     * Returns the absolute file path to the template
     * @return the absolute file path to the template
     */
    @Override
    public Path getAbsoluteTemplatePath() {
        return absoluteTemplatePath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name='" + getName() + "]";
    }

}
