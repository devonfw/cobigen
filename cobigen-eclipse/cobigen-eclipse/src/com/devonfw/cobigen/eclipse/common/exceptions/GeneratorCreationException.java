package com.devonfw.cobigen.eclipse.common.exceptions;

/** States that an exception occured during generator instance creation */
public class GeneratorCreationException extends Exception {

  /** Default serial version UID */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link GeneratorCreationException}
   *
   * @param message of the exception
   */
  public GeneratorCreationException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link GeneratorCreationException}
   *
   * @param message of the exception
   * @param cause of the exception
   */
  public GeneratorCreationException(String message, Throwable cause) {

    super(message, cause);
  }
}
