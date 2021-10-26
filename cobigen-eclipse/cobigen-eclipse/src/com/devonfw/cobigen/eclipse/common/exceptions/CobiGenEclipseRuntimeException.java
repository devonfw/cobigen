package com.devonfw.cobigen.eclipse.common.exceptions;

/**
 * {@link RuntimeException} of CobiGen eclipse plug-in.
 */
public class CobiGenEclipseRuntimeException extends RuntimeException {

  /**
   * Default serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link CobiGenEclipseRuntimeException} for the given message and cause.
   *
   * @param message error message
   */
  public CobiGenEclipseRuntimeException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link CobiGenEclipseRuntimeException} for the given message and cause.
   *
   * @param message error message
   * @param cause of the exception
   */
  public CobiGenEclipseRuntimeException(String message, Throwable cause) {

    super(message, cause);
  }

}
