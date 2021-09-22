package com.devonfw.cobigen.eclipse.common.exceptions;

/**
 * Thrown if the input could not be read as expected.
 */
public class InvalidInputException extends Exception {

  /** Generated Serial Version UID. */
  private static final long serialVersionUID = 6923294459129047262L;

  /** States, whether the exception has been initialized with a source exception */
  private boolean hasRootCause;

  /**
   * Creates a new {@link InvalidInputException} with a given message an cause.
   *
   * @param message error message
   */
  public InvalidInputException(String message) {

    super(message);
    this.hasRootCause = false;
  }

  /**
   * Creates a new {@link InvalidInputException} with a given message an cause.
   *
   * @param message error message
   * @param t cause
   */
  public InvalidInputException(String message, Throwable t) {

    super(message, t);
    this.hasRootCause = true;
  }

  /**
   * @return <code>true</code> if a root cause exists
   */
  public boolean hasRootCause() {

    return this.hasRootCause;
  }

}
