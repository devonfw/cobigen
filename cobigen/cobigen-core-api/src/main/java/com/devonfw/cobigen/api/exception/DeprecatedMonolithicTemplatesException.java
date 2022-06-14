package com.devonfw.cobigen.api.exception;

/**
 * Exception that indicates that an old monolithic template structure has been found. For asking if the template
 * structure should be upgraded later.
 */
public class DeprecatedMonolithicTemplatesException extends CobiGenRuntimeException {

  /**
   * Default serial version UID.
   */
  private static final long serialVersionUID = 1;

  /**
   * The constructor.
   *
   * @param message informative message describing the exception or where it has been occurred
   * @param cause originating this exception
   */
  public DeprecatedMonolithicTemplatesException(String message, Throwable cause) {

    super(message, cause);
  }

  /**
   * The constructor.
   *
   * Creates a new {@link DeprecatedMonolithicTemplatesException} with a proper notification message
   */
  public DeprecatedMonolithicTemplatesException() {

    super("You Are using an old Templates Configuration, please consider to upgrade your templates! Thank You!");
  }

}
