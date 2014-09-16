/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension.to;

/**
 * 
 * The transfer object for templates
 * @author mbrunnli (09.04.2014)
 */
public class TemplateTo {

    /**
     * Id of the template
     */
    protected String id;

    /**
     * Relative path for the result.
     */
    protected String destinationPath;

    /**
     * Determines the required strategy to merge the template
     */
    protected String mergeStrategy;

    /**
     * The trigger's id the template is assigned to
     */
    protected String triggerId;

    /**
     * Creates a new templates transfer object with the given properties
     * 
     * @param id
     *            of the template
     * @param destinationPath
     *            the generated resources should be generated to
     * @param mergeStrategy
     *            merge strategy the generated sources can be merged with
     * @param triggerId
     *            the template is assigned to
     * @author mbrunnli (09.04.2014)
     */
    public TemplateTo(String id, String destinationPath, String mergeStrategy, String triggerId) {
        this.id = id;
        this.destinationPath = destinationPath;
        this.mergeStrategy = mergeStrategy;
        this.triggerId = triggerId;
    }

    /**
     * Returns the template's id
     * @return the template's id
     * @author mbrunnli (09.04.2014)
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the destination path the generated resources should be generated to
     * @return the destination path
     * @author mbrunnli (09.04.2014)
     */
    public String getDestinationPath() {
        return destinationPath;
    }

    /**
     * Returns the merge strategy the generated sources can be merged with
     * @return the merge strategy
     * @author mbrunnli (09.04.2014)
     */
    public String getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * Returns the trigger's id this template is assigned to
     * @return the trigger's id
     * @author mbrunnli (09.04.2014)
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (07.06.2014)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof TemplateTo) return ((TemplateTo) obj).getId().equals(getId());
        return false;
    }
}
