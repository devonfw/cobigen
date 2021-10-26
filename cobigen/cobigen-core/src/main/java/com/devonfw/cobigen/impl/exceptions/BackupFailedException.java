package com.devonfw.cobigen.impl.exceptions;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Exception indicating a failed backup process.
 */
public class BackupFailedException extends CobiGenRuntimeException {

  /**
   * Default serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link BackupFailedException} with the given message
   *
   * @param message message
   */
  public BackupFailedException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link BackupFailedException} with the given message and cause.
   *
   * @param message message
   * @param cause original cause
   */
  public BackupFailedException(String message, Throwable cause) {

    super(message, cause);
  }

}
