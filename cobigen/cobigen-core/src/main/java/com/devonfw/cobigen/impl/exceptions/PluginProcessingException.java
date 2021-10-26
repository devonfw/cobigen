package com.devonfw.cobigen.impl.exceptions;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Exception for any unexpected behavior of a plug-in.
 */
public class PluginProcessingException extends CobiGenRuntimeException {

  /**
   * Generated Serial Version UID
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new {@link PluginProcessingException} with the given message
   *
   * @param message error message
   */
  public PluginProcessingException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link PluginProcessingException} with the given message and the given cause
   *
   * @param cause cause of the exception
   */
  public PluginProcessingException(Throwable cause) {

    super("A plug-in terminated abruptly! - Please consider to state this as a Bug on CobiGen's GitHub repository.",
        cause);
  }

}
