/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension.to;

import com.capgemini.cobigen.config.entity.AbstractTemplateResolver;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 *
 * The transfer object for templates
 * @author mbrunnli (09.04.2014)
 */
public class TemplateTo extends AbstractTemplateResolver {

    /**
     * Id of the template
     */
    private String id;

    /**
     * Determines the required strategy to merge the template
     */
    private String mergeStrategy;

    /**
     * Creates a new templates transfer object with the given properties
     *
     * @param id
     *            of the template
     * @param unresolvedDestinationPath
     *            the generated resources should be generated to
     * @param mergeStrategy
     *            merge strategy the generated sources can be merged with
     * @param trigger
     *            the template is assigned to
     * @param triggerInterpreter
     *            used interpreter of the trigger
     * @author mbrunnli (09.04.2014)
     */
    public TemplateTo(String id, String unresolvedDestinationPath, String mergeStrategy, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {
        super(unresolvedDestinationPath, trigger, triggerInterpreter);
        this.id = id;
        this.mergeStrategy = mergeStrategy;
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
        return getTrigger().getId();
    }

    /**
     * Intended only for debugging.
     *
     * <P>
     * Here, the contents of every field are placed into the result, with one field per line.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" Id: " + getId() + NEW_LINE);
        result.append(" TriggerId: " + getTriggerId() + NEW_LINE);
        result.append(" MergeStrategy: " + getMergeStrategy() + NEW_LINE);
        result.append("}");
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        if (getId() != null) {
            result = prime * result + getId().hashCode();
        } else if (getTriggerId() != null) {
            result = prime * result + getTriggerId().hashCode();
        } else if (getMergeStrategy() != null) {
            result = prime * result + getMergeStrategy().hashCode();
        }
        result = prime * result;
        return result;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (07.06.2014)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof TemplateTo) {
            if (((TemplateTo) obj).getId() != null) {
                return ((TemplateTo) obj).getId().equals(getId());
            } else if (((TemplateTo) obj).getTriggerId() != null) {
                ((TemplateTo) obj).getTriggerId().equals(getTriggerId());
            } else if (((TemplateTo) obj).getMergeStrategy() != null) {
                return ((TemplateTo) obj).getMergeStrategy().equals(getMergeStrategy());
            }
        }
        return false;
    }
}
