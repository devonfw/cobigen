package com.devonfw.cobigen.api.exception;

import java.nio.file.Path;

/**
 * Exception that indicates that an old monolithic template structure has been found. For asking if the template
 * structure should be upgraded later.
 */
public class DeprecatedMonolithicConfigurationException extends CobiGenRuntimeException {

  /**
   * Default serial version UID.
   */
  private static final long serialVersionUID = 1;

  /**
   * Path to the monolithicConfiguration.
   */
  private static Path monolithicConfiguration = null;

  /**
   * Getter the path to the monolithicConfiguration
   *
   * @return oldTemplatesPath path to the monolithicConfiguration
   */
  public static Path getMonolithicConfiguration() {

    return monolithicConfiguration;
  }

  /**
   * The constructor.
   *
   * @param message informative message describing the exception or where it has been occurred
   * @param cause originating this exception
   */
  public DeprecatedMonolithicConfigurationException(String message, Throwable cause) {

    super(message, cause);
  }

  /**
   * The constructor.
   *
   * Creates a new {@link DeprecatedMonolithicConfigurationException} with a proper notification message
   */
  public DeprecatedMonolithicConfigurationException() {

    super("You are using an old templates configuration. Please consider upgrading your templates! Thank you!");
  }

  /**
   * The constructor.
   *
   * Creates a new {@link DeprecatedMonolithicConfigurationException} with a proper notification message
   *
   * @param monolithicConfiguration path to the monolithicConfiguration
   */
  public DeprecatedMonolithicConfigurationException(Path monolithicConfiguration) {

    super("You are using an old templates configuration. Please consider upgrading your templates! Thank you!");
    DeprecatedMonolithicConfigurationException.monolithicConfiguration = monolithicConfiguration;
  }

}
