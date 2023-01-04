package com.devonfw.cobigen.impl.config.entity;

/**
 * The {@link ContainerMatcher} type corresponds to the &lt;containerMatcher&gt; xml node
 */
public class ContainerMatcher extends AbstractMatcher {

  /**
   * States whether objects should be retrieved recursively or not.
   */
  private boolean retrieveObjectsRecursively;

  /**
   * Creates a new {@link ContainerMatcher} with the given type and the given value to match against
   *
   * @param type of the matcher
   * @param value value to be processed by the matcher
   * @param retrieveObjectsRecursively states whether objects should be retrieved recursively or not
   */
  public ContainerMatcher(String type, String value, boolean retrieveObjectsRecursively) {

    super(type, value);
    this.retrieveObjectsRecursively = retrieveObjectsRecursively;
  }

  /**
   * Returns the field 'retrieveObjectsRecursively'
   *
   * @return value of retrieveObjectsRecursively
   */
  public boolean isRetrieveObjectsRecursively() {

    return this.retrieveObjectsRecursively;
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[type='" + getType() + "'/value='" + getValue() + "']";
  }
}
