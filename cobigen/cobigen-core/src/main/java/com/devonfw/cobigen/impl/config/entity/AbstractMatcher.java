package com.devonfw.cobigen.impl.config.entity;

import java.util.Objects;

/** Common declaration of all available matcher types */
public abstract class AbstractMatcher {

  /** Matcher type */
  private String type;

  /** Matcher value to be matched against */
  private String value;

  /**
   * Creates a new {@link Matcher} for a given type, with a given value to match against
   *
   * @param type matcher type
   * @param value to match against
   */
  public AbstractMatcher(String type, String value) {

    this.type = type;
    this.value = value;
  }

  /**
   * Returns the matcher type
   *
   * @return matcher type
   */
  public String getType() {

    return this.type;
  }

  /**
   * Returns the value the matcher should match against
   *
   * @return the value the matcher should match against
   */
  public String getValue() {

    return this.value;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.type, this.value);
  }
}
