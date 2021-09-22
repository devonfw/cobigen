package com.devonfw.cobigen.impl.config.entity;

import java.util.LinkedList;
import java.util.List;

import com.devonfw.cobigen.impl.config.entity.io.AccumulationType;

/** The {@link Matcher} type corresponds to the &lt;matcher&gt; xml node */
public class Matcher extends AbstractMatcher {

  /** {@link VariableAssignment}s */
  private List<VariableAssignment> variableAssignments;

  /** Accumulation type */
  private AccumulationType accumulationType;

  /**
   * Creates a new Matcher for a given type, with a given value to match against and the corresponding variable
   * assignments which should be resolvable
   *
   * @param type matcher type
   * @param value to match against
   * @param variableAssignments of the matcher
   * @param accumulationType of the matcher
   */
  public Matcher(String type, String value, List<VariableAssignment> variableAssignments,
      AccumulationType accumulationType) {

    super(type, value);
    this.accumulationType = accumulationType;
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
  public AccumulationType getAccumulationType() {

    return this.accumulationType;
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[type='" + getType() + "'/value='" + getValue() + "'/accuType='"
        + this.accumulationType.name() + "']";
  }
}
