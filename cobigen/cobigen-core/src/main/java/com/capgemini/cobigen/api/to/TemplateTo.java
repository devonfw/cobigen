package com.capgemini.cobigen.api.to;

import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.config.entity.AbstractTemplateResolver;
import com.capgemini.cobigen.impl.config.entity.Trigger;

/**
 *
 * The transfer object for templates
 * @author mbrunnli (09.04.2014)
 */
public class TemplateTo extends AbstractTemplateResolver implements GenerableArtifact {

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
        TriggerInterpreter triggerInterpreter) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        result = prime * result + (getTriggerId() == null ? 0 : getTriggerId().hashCode());
        result = prime * result + (getMergeStrategy() == null ? 0 : getMergeStrategy().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (obj instanceof TemplateTo) {
            boolean equal = true;
            TemplateTo otherTemplate = (TemplateTo) obj;
            if (getId() != null) {
                equal = equal && getId().equals(otherTemplate.getId());
            }
            if (!equal) {
                return false;
            }

            if (getTriggerId() != null) {
                equal = equal && getTriggerId().equals(otherTemplate.getTriggerId());
            }
            if (!equal) {
                return false;
            }

            if (getMergeStrategy() != null) {
                equal = equal && getMergeStrategy().equals(otherTemplate.getMergeStrategy());
            }
            return equal;
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id='" + getId() + "'/triggerId='" + getTriggerId()
            + "'/mergeStrategy=" + getMergeStrategy() + "']";
    }
}
