package com.devonfw.cobigen.api.extension;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;

/**
 * This interface should be inherited by implementations to cover the interpretation of new trigger types. Therefore you
 * have to provide exactly one {@link InputReader} and one {@link MatcherInterpreter}.
 */
@ExceptionFacade
public interface TriggerInterpreter {

  /**
   * This function should return the type name, which could be declared in the type parameter of the XML &lt;trigger&gt;
   * element and therefore invoke this interpreter
   *
   * @return the type name (not null)
   */
  public String getType();

  /**
   * This function should return the {@link InputReader} for reading the intended input format for this trigger
   * interpreter
   *
   * @return the {@link InputReader} (not null)
   */
  public InputReader getInputReader();

  /**
   * This function should return the {@link MatcherInterpreter} for matching a given input as a valid input to be
   * processed and resolving the values of all variable assignments
   *
   * @return the {@link MatcherInterpreter} (not null)
   */
  public MatcherInterpreter getMatcher();
}
