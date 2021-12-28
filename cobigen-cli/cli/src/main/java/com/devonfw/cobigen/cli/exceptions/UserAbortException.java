package com.devonfw.cobigen.cli.exceptions;

import com.devonfw.cobigen.api.exception.CobiGenCancellationException;

/**
 * Exception is thrown if the generation process was cancelled by the user
 */
public class UserAbortException extends CobiGenCancellationException {

  /**
   * Default serial version UID.
   */
  private static final long serialVersionUID = 1L;

}
