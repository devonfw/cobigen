package com.devonfw.cobigen.impl.config.entity;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Storage class for trigger data provided within the context.xml
 */
public class Trigger {

  /** Technical id */
  private String id;

  /** Identifies the {@link Trigger}. */
  private String type;

  /** Folder in which the configuration.xml is located in */
  private String templateFolder;

  /** Input charset, which should be used to read the inputs */
  private Charset inputCharset;

  /** All available matchers */
  private List<Matcher> matchers;

  /** All available container matchers */
  private List<ContainerMatcher> containerMatchers;

  /** States if the trigger has been matched by a container matcher */
  private boolean matchesByContainerMatcher;

  /**
   * Creates a new {@link Trigger} for the given data
   *
   * @param id for the trigger
   * @param type of the trigger {@link #type}
   * @param templateFolder the trigger's {@link #templateFolder}
   * @param inputCharset which should be used to read the inputs
   * @param matcher all declared {@link Matcher}s for this trigger
   * @param containerMatchers all declared {@link ContainerMatcher}s for this trigger
   */
  public Trigger(String id, String type, String templateFolder, Charset inputCharset, List<Matcher> matcher,
      List<ContainerMatcher> containerMatchers) {

    this.id = id;
    this.type = type;
    this.templateFolder = templateFolder;
    this.inputCharset = inputCharset;
    this.matchers = matcher == null ? new LinkedList<>() : matcher;
    this.containerMatchers = containerMatchers == null ? new LinkedList<>() : containerMatchers;
  }

  /**
   * Copy constructor to reset {@link #matchesByContainerMatcher}, e.g. enable it
   *
   * @param trigger to be copied
   * @param matchesByContainerMatcher see {@link #matchesByContainerMatcher()}
   */
  public Trigger(Trigger trigger, boolean matchesByContainerMatcher) {

    this.id = trigger.id;
    this.type = trigger.type;
    this.templateFolder = trigger.templateFolder;
    this.inputCharset = trigger.inputCharset;
    this.matchers = trigger.matchers;
    this.containerMatchers = trigger.containerMatchers;
    this.matchesByContainerMatcher = matchesByContainerMatcher;
  }

  /**
   * @return the technical ID
   */
  public String getId() {

    return this.id;
  }

  /**
   * @return the {@link Trigger}'s {@link #type}
   */
  public String getType() {

    return this.type;
  }

  /**
   * @return the template folder name
   */
  public String getTemplateFolder() {

    return this.templateFolder;
  }

  /**
   * @return the specified inputCharset
   */
  public Charset getInputCharset() {

    return this.inputCharset;
  }

  /**
   * @return all available matchers
   */
  public List<Matcher> getMatcher() {

    return new ArrayList<>(this.matchers);
  }

  /**
   * @return all available containerMatchers
   */
  public List<ContainerMatcher> getContainerMatchers() {

    return new ArrayList<>(this.containerMatchers);
  }

  /**
   * @return <code>true</code> iff the trigger has been activated by a matching container matcher
   */
  public boolean matchesByContainerMatcher() {

    return this.matchesByContainerMatcher;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj instanceof Trigger) {
      return ((Trigger) obj).getId().equals(getId());
    }
    return false;
  }

  @Override
  public int hashCode() {

    return this.id.hashCode();
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[id='" + this.id + "']";
  }

}
