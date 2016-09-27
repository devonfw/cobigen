package com.capgemini.cobigen.api.to;

/**
 * The transfer object for templates
 */
public class TemplateTo implements GenerableArtifact {

    /** Id of the template */
    private String id;

    /** Determines the required strategy to merge the template */
    private String mergeStrategy;

    /** Relative path for the result. */
    private String unresolvedDestinationPath;

    /** Trigger ID, the template has been resolved from. */
    private String triggerId;

    /**
     * Creates a new templates transfer object with the given properties
     *
     * @param id
     *            of the template
     * @param unresolvedDestinationPath
     *            the generated resources should be generated to
     * @param mergeStrategy
     *            merge strategy the generated sources can be merged with
     * @param triggerId
     *            Trigger ID, the template has been resolved from.
     */
    public TemplateTo(String id, String unresolvedDestinationPath, String mergeStrategy, String triggerId) {
        this.id = id;
        this.mergeStrategy = mergeStrategy;
        this.unresolvedDestinationPath = unresolvedDestinationPath;
        this.triggerId = triggerId;
    }

    /**
     * Returns the template's id
     * @return the template's id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the merge strategy the generated sources can be merged with
     * @return the merge strategy
     */
    public String getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * Returns the trigger's id this template is assigned to
     * @return the trigger's id
     */
    public String getTriggerId() {
        return triggerId;
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
