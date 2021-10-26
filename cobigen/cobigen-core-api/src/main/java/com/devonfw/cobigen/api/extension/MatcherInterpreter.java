package com.devonfw.cobigen.api.extension;

import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;

/**
 * This interface should be implemented for providing a new input matcher. Input matcher are defined as part of a
 * trigger and provide the ability to restrict specific inputs to a set of templates. Furthermore, matchers may provide
 * several variable assignments, which might be dependent on any information of the matched input and thus should be
 * resolvable by the defining matcher.
 */
@ExceptionFacade
public interface MatcherInterpreter {

  /**
   * This function should check, whether the given matcher matches the attached input
   *
   * @param matcher specifying the matcher type, the value to match against, and the object to be matched
   * @return <code>true</code> if the object matches the given parameters, <br>
   *         <code>false</code> otherwise (not null)
   */
  public boolean matches(MatcherTo matcher);

  /**
   * This function should resolve all given variable assignments with respect to the given matcher and return a
   * variable, value mapping.
   *
   * @param matcher the parent matcher of the given variable assignments
   * @param variableAssignments the variable assignments which should be resolved
   * @return resolved variable, value mapping (not null)
   * @throws InvalidConfigurationException if one of the requested variable assignment types could not be managed by
   *         this {@link MatcherInterpreter} instance
   */
  public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
      throws InvalidConfigurationException;
}
