/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension.to;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * The transfer object for increments
 * @author mbrunnli (09.04.2014)
 */
public class IncrementTo {

    /**
     * Id of the Increment
     */
    protected String id;

    /**
     * Textual description of the increment.
     */
    protected String description;

    /**
     * Trigger Id, this {@link IncrementTo} was retrieved from
     */
    protected String triggerId;

    /**
     * Increments, which are part of this increment
     */
    protected List<IncrementTo> dependentIncrements = Lists.newLinkedList();

    /**
     * Set of templates contained in this increment, inclusively templates of dependent increments.
     */
    protected List<TemplateTo> templates = Lists.newLinkedList();

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
     * @author mbrunnli (09.04.2014)
     */
    public IncrementTo(String id, String description, String triggerId, List<TemplateTo> templates,
        List<IncrementTo> dependentIncrements) {
        this.id = id;
        this.description = description;
        this.templates = templates;
        this.triggerId = triggerId;
        this.dependentIncrements = dependentIncrements;
    }

    /**
     * Returns the id
     * @return the id
     * @author mbrunnli (09.04.2014)
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description
     * @return the description
     * @author mbrunnli (09.04.2014)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the trigger's id the increment is assigned to
     * @return the trigger's id the increment is assigned to
     * @author mbrunnli (09.04.2014)
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * Returns the {@link List} of templates, which are part of the increment (recursively resolved)
     * @return the {@link List} of tempaltes
     * @author mbrunnli (09.04.2014)
     */
    public List<TemplateTo> getTemplates() {
        return Lists.newLinkedList(templates);
    }

    /**
     * Returns the {@link List} of increments, which are part of this increment
     * @return the {@link List} of increments, which are part of this increment
     * @author mbrunnli (10.04.2014)
     */
    public List<IncrementTo> getDependentIncrements() {
        return Lists.newLinkedList(dependentIncrements);
    }

    /**
     * {@inheritDoc}
     * @author sbasnet (23.10.2014)
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        result = prime * result + (getDescription() == null ? 0 : getDescription().hashCode());
        result = prime * result + (getTemplates() == null ? 0 : getTemplates().hashCode());
        result = prime * result + (getTriggerId() == null ? 0 : getTriggerId().hashCode());
        result =
            prime * result + (getDependentIncrements() == null ? 0 : getDependentIncrements().hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @author sbasnet (23.10.2014)
     */
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

    /**
     * {@inheritDoc}
     * @author sbasnet (23.10.2014)
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName() + " {");
        result.append("id: " + getId());
        result.append(" desc: " + getDescription());
        result.append(" #templates: " + getTemplates().size());
        result.append(" triggerId: " + getTriggerId());
        result.append(" #dependentIncrements: " + getDependentIncrements().size());
        result.append("}");
        return result.toString();
    }

}
