package com.devonfw.cobigen.impl.config.entity;

import java.util.Objects;

/**
 * A {@link VariableAssignment} key value pair, which will be interpreted by any plug-in declaring the {@link #type}
 */
public class VariableAssignment {

  /**
   * Type of this variable assignment
   */
  private String type;

  /**
   * Variable name
   */
  private String varName;

  /**
   * Concrete value (if set)
   */
  private String value;

  /**
   * True if the value is required, false if not
   */
  protected boolean mandatory;

  /**
   * Constructor to create a {@link VariableAssignment} for a concrete string value
   *
   * @param type Type of the variable assignment, interpreted by the plug-ins
   * @param varName variable name
   * @param value concrete string value
   */
  public VariableAssignment(String type, String varName, String value, boolean mandatory) {

    this.type = type;
    this.varName = varName;
    this.value = value;
    this.mandatory = mandatory;
  }

  /**
   * Constructor to create a {@link VariableAssignment} for a concrete string value
   *
   * @param type Type of the variable assignment, interpreted by the plug-ins
   * @param varName variable name
   * @param value concrete string value
   */
  public VariableAssignment(String type, String varName, String value) {

    this.type = type;
    this.varName = varName;
    this.value = value;
    this.mandatory = false;
  }

  /**
   * Returns the variable assignment type
   *
   * @return the variable assignment type
   */
  public String getType() {

    return this.type;
  }

  /**
   * Returns the variable name
   *
   * @return the variable name
   */
  public String getVarName() {

    return this.varName;
  }

  /**
   * Returns the value
   *
   * @return the value
   */
  public String getValue() {

    return this.value;
  }

  /**
   * @return mandatory
   */
  public boolean isMandatory() {

    return this.mandatory;
  }

  /**
   * @param mandatory new value of {@link #getmandatory}.
   */
  public void setMandatory(boolean mandatory) {

    this.mandatory = mandatory;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.type, this.varName, this.value, this.mandatory);
  }
}
