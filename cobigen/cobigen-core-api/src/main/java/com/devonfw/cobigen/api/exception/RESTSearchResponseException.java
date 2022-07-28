package com.devonfw.cobigen.api.exception;

/** Exception to indicate that the REST search API encountered a problem while accessing the server. */
public class RESTSearchResponseException extends CobiGenRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link RESTSearchResponseException} with the given message
   *
   * @param msg error message of the exception
   */
  public RESTSearchResponseException(String msg) {

    super(msg);
  }

  /**
   * Creates a new {@link RESTSearchResponseException} with the specified message and the causing {@link Throwable}
   *
   * @param message describing the exception
   * @param cause the causing Throwable
   */
  public RESTSearchResponseException(String message, Throwable cause) {

    super(message, cause);
  }

  /**
   * Creates a new {@link RESTSearchResponseException} with the specified message and the causing {@link Throwable}
   *
   * @param statusCode status code causing the {@link RESTSearchResponseException} or null if not available
   * @param message describing the exception
   */
  public RESTSearchResponseException(String message, String statusCode) {

    super((statusCode != null ? statusCode + ":\n" : "") + message);
  }

}
