package com.devonfw.cobigen.api.to;

import java.util.List;

import com.google.common.collect.Lists;

/** The transfer object for increments */
public class IncrementTo extends GenerableArtifact {

    /** Textual description of the increment. */
    private String description;

    /** Increments, which are part of this increment */
    private List<IncrementTo> dependentIncrements = Lists.newLinkedList();

    /** Set of templates contained in this increment, inclusively templates of dependent increments. */
    private List<TemplateTo> templates = Lists.newLinkedList();

    /**
     * Creates a new increment transfer object with the given properties
     * @param id
     *            of the increment
     * @param description
     *            of the increment
     * @param triggerId
     *            the increment is assigned to
     * @param templates
     *            a {@link List} of {@link TemplateTo}s which are part of the increment (recursively resolved)
     * @param dependentIncrements
     *            a {@link List} of dependent increments which are part of this increment
     */
    public IncrementTo(String id, String description, String triggerId, List<TemplateTo> templates,
        List<IncrementTo> dependentIncrements) {
        super(id, triggerId);
        this.description = description;
        this.templates = templates;
        this.dependentIncrements = dependentIncrements;
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the {@link List} of templates, which are part of the increment (recursively resolved)
     * @return the {@link List} of templates
     */
    public List<TemplateTo> getTemplates() {
        return Lists.newLinkedList(templates);
    }

    /**
     * Returns the {@link List} of increments, which are part of this increment
     * @return the {@link List} of increments, which are part of this increment
     */
    public List<IncrementTo> getDependentIncrements() {
        return Lists.newLinkedList(dependentIncrements);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        result = prime * result + (getDescription() == null ? 0 : getDescription().hashCode());
        result = prime * result + (getTemplates() == null ? 0 : getTemplates().hashCode());
        result = prime * result + (getTriggerId() == null ? 0 : getTriggerId().hashCode());
        result = prime * result + (getDependentIncrements() == null ? 0 : getDependentIncrements().hashCode());
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

        if (obj instanceof IncrementTo) {
            boolean equal = true;
            IncrementTo otherIncrement = (IncrementTo) obj;

            if (getId() != null) {
                equal = equal && getId().equals(otherIncrement.getId());
            }
            if (!equal) {
                return false;
            }

            if (getDescription() != null) {
                equal = equal && getDescription().equals(otherIncrement.getDescription());
            }
            if (!equal) {
                return false;
            }

            if (getTemplates() != null) {
                equal = equal && getTemplates().equals(otherIncrement.getTemplates());
            }
            if (!equal) {
                return false;
            }

            if (getTriggerId() != null) {
                equal = equal && getTriggerId().equals(otherIncrement.getTriggerId());
            }
            if (!equal) {
                return false;
            }

            if (getDependentIncrements() != null) {
                equal = equal && getDependentIncrements().equals(otherIncrement.getDependentIncrements());
            }
            return equal;
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id='" + getId() + "'/desc='" + getDescription() + "'/#templates='"
            + getTemplates().size() + "' triggerId='" + getTriggerId() + "'/#dependentIncrements: "
            + getDependentIncrements().size() + "']";
    }
}
