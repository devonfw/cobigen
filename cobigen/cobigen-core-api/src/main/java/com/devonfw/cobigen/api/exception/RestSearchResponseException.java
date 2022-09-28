package com.devonfw.cobigen.api.exception;

/** Exception to indicate that the REST search API encountered a problem while accessing the server. */
public class RestSearchResponseException extends CobiGenRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link RestSearchResponseException} with the given message
   *
   * @param message error message of the exception
   */
  public RestSearchResponseException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link RestSearchResponseException} with the specified message and the causing {@link Throwable}
   *
   * @param message describing the exception
   * @param cause the causing Throwable
   */
  public RestSearchResponseException(String message, Throwable cause) {

    super(message, cause);
  }

  /**
   * Creates a new {@link RestSearchResponseException} with the specified message and the causing {@link Throwable}
   *
   * @param message describing the exception
   * @param statusCode status code causing the {@link RestSearchResponseException} or null if not available
   */
  public RestSearchResponseException(String message, int statusCode) {

    super(message + String.valueOf(statusCode));
  }

}
