package com.capgemini.cobigen.impl.config.entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Storage class for increments provided within the config.xml
 * @author trippl (07.03.2013)
 *
 */
public class Increment {

    /**
     * Identifies the {@link Increment}
     */
    private String name;

    /**
     * Textual description of the {@link Increment}. Used for (G)UI.
     */
    private String description;

    /**
     * Set of {@link Template}s contained in the {@link Increment}
     */
    private Set<Template> templates = new HashSet<>();

    /**
     * Dependent {@link Increment}s
     */
    private Set<Increment> dependentIncrements = new HashSet<>();

    /**
     * {@link Trigger} the increment is dependent on
     */
    private Trigger trigger;

    /**
     * Creates a new {@link Increment} with the specified name and description
     * @param name
     *            of the increment
     * @param description
     *            of the increment
     * @param trigger
     *            the increment depends on
     * @author trippl (07.03.2013), adapted by mbrunnli (07.03.2013)
     */
    public Increment(String name, String description, Trigger trigger) {
        this.name = name;
        this.description = description;
        this.trigger = trigger;
    }

    /**
     * Returns the {@link Increment}'s {@link #name}
     * @return the increment name
     * @author trippl (07.03.2013)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Increment}'s {@link #description}
     * @return the increment description
     * @author trippl (07.03.2013)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the {@link Template}s contained in the {@link Increment}
     * @return the contained templates
     * @author trippl (07.03.2013)
     */
    public Set<Template> getTemplates() {
        return new HashSet<>(templates);
    }

    /**
     * Adds the specified {@link Template} to the {@link #templates} set if not already present.
     * @param template
     *            {@link Template} to be added
     * @author trippl (07.03.2013)
     */
    public void addTemplate(Template template) {
        templates.add(template);
    }

    /**
     * Adds the given {@link Increment} as a dependency
     * @param increment
     *            {@link Increment} dependency
     * @author mbrunnli (26.03.2013)
     */
    public void addIncrementDependency(Increment increment) {
        dependentIncrements.add(increment);
    }

    /**
     * Returns a {@link Set} of all dependent increments
     * @return a {@link Set} of all dependent increments
     * @author mbrunnli (26.03.2013)
     */
    public List<Increment> getDependentIncrements() {
        return new LinkedList<>(dependentIncrements);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (11.03.2013)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Increment) {
            Increment objIncrement = (Increment) obj;
            if (hasTrigger() && objIncrement.hasTrigger()) {
                if (trigger.getType().equals(objIncrement.getTrigger().getType())) {
                    return name.equals(((Increment) obj).getName());
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (11.03.2013)
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns the {@link Trigger} the template is dependent on
     * @return the {@link Trigger} the template is dependent on
     * @author trippl (18.04.2013)
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Determines whether there is an trigger defined or not
     * @return <code>true</code> if there is an trigger defined,<br>
     *         <code>false</code> otherwise
     * @author trippl (18.04.2013)
     */
    public boolean hasTrigger() {
        return trigger != null;
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
