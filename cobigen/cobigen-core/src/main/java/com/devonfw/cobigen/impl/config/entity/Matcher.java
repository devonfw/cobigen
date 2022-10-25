package com.devonfw.cobigen.impl.config.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** The {@link Matcher} type corresponds to the &lt;matcher&gt; xml node */
public class Matcher extends AbstractMatcher {

  /** {@link VariableAssignment}s */
  private List<VariableAssignment> variableAssignments;

  /** Old Accumulation type */
  private com.devonfw.cobigen.impl.config.entity.io.v3_0.AccumulationType accumulationType;

  /** Accumulation type */
  private com.devonfw.cobigen.impl.config.entity.io.AccumulationType accumulationTypeNew;

  /**
   * Creates a new Matcher for a given type, with a given value to match against and the corresponding variable
   * assignments which should be resolvable
   *
   * @param type matcher type
   * @param value to match against
   * @param variableAssignments of the matcher
   * @param accumulationType2 of the matcher
   */
  public Matcher(String type, String value, List<VariableAssignment> variableAssignments,
      com.devonfw.cobigen.impl.config.entity.io.v3_0.AccumulationType accumulationType2) {

    super(type, value);
    this.accumulationType = accumulationType2;
    this.variableAssignments = variableAssignments == null ? new LinkedList<>() : variableAssignments;
  }

  /**
   * Creates a new Matcher for a given type, with a given value to match against and the corresponding variable
   * assignments which should be resolvable
   *
   * @param type matcher type
   * @param value to match against
   * @param variableAssignments of the matcher
   * @param accumulationType2 of the matcher
   */
  public Matcher(String type, String value, List<VariableAssignment> variableAssignments,
      com.devonfw.cobigen.impl.config.entity.io.AccumulationType accumulationType2) {

    super(type, value);
    this.accumulationTypeNew = accumulationType2;
    this.variableAssignments = variableAssignments == null ? new LinkedList<>() : variableAssignments;
  }

  /**
   * @return the variable assignments of the matcher
   */
  public List<VariableAssignment> getVariableAssignments() {

    return this.variableAssignments;
  }

  /**
   * @return value of accumulationType
   */
  public com.devonfw.cobigen.impl.config.entity.io.v3_0.AccumulationType getAccumulationType() {

    return this.accumulationType;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), this.accumulationType);
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[type='" + getType() + "'/value='" + getValue() + "'/accuType='"
        + this.accumulationType.name() + "']";
  }
}
