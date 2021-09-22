package com.devonfw.cobigen.impl.config.entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/** Storage class for increments provided within the config.xml */
public class Increment {

  /** Identifies the {@link Increment} */
  private String name;

  /** Textual description of the {@link Increment}. Used for (G)UI. */
  private String description;

  /** Set of {@link Template}s contained in the {@link Increment} */
  private Set<Template> templates = new HashSet<>();

  /** Dependent {@link Increment}s */
  private Set<Increment> dependentIncrements = new HashSet<>();

  /** {@link Trigger} the increment is dependent on */
  private Trigger trigger;

  /**
   * Creates a new {@link Increment} with the specified name and description
   *
   * @param name of the increment
   * @param description of the increment
   * @param trigger the increment depends on
   */
  public Increment(String name, String description, Trigger trigger) {

    this.name = name;
    this.description = description;
    this.trigger = trigger;
  }

  /** @return the {@link Increment}'s {@link #name} */
  public String getName() {

    return this.name;
  }

  /** @return the {@link Increment}'s {@link #description} */
  public String getDescription() {

    return this.description;
  }

  /** @return the {@link Template}s contained in the {@link Increment} */
  public Set<Template> getTemplates() {

    return new HashSet<>(this.templates);
  }

  /**
   * Adds the specified {@link Template} to the {@link #templates} set if not already present.
   *
   * @param template {@link Template} to be added
   */
  public void addTemplate(Template template) {

    this.templates.add(template);
  }

  /**
   * Adds the given {@link Increment} as a dependency
   *
   * @param increment {@link Increment} dependency
   */
  public void addIncrementDependency(Increment increment) {

    this.dependentIncrements.add(increment);
  }

  /**
   * Returns a {@link Set} of all dependent increments
   *
   * @return a {@link Set} of all dependent increments
   */
  public List<Increment> getDependentIncrements() {

    return new LinkedList<>(this.dependentIncrements);
  }

  @Override
  public boolean equals(Object obj) {

    if (obj != null && obj instanceof Increment) {
      Increment objIncrement = (Increment) obj;
      if (hasTrigger() && objIncrement.hasTrigger()) {
        if (this.trigger.getType().equals(objIncrement.getTrigger().getType())) {
          return this.name.equals(((Increment) obj).getName());
        }
      }
    }
    return false;
  }

  @Override
  public int hashCode() {

    return this.name.hashCode();
  }

  /**
   * Returns the {@link Trigger} the template is dependent on
   *
   * @return the {@link Trigger} the template is dependent on
   */
  public Trigger getTrigger() {

    return this.trigger;
  }

  /**
   * Determines whether there is an trigger defined or not
   *
   * @return <code>true</code> if there is an trigger defined,<br>
   *         <code>false</code> otherwise
   */
  public boolean hasTrigger() {

    return this.trigger != null;
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[name='" + getName() + "]";
  }
}
