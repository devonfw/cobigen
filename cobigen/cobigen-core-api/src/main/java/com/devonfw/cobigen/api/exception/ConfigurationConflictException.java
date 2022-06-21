package com.devonfw.cobigen.api.exception;

import java.nio.file.Path;

/** Occurs if a conflict between the configuration was found */
public class ConfigurationConflictException extends InvalidConfigurationException {

  /** Default serial version UID */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link ConfigurationConflictException} with the given message
   *
   * @param filePath file path causing the ConfigurationConflictException or null if not available
   * @param msg error message of the exception
   * @param t cause exception
   */
  public ConfigurationConflictException(Path filePath, String msg) {

    super(filePath, msg);
  }

}
