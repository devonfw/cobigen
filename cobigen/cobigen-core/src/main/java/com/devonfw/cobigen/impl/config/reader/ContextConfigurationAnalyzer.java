package com.devonfw.cobigen.impl.config.reader;

import java.nio.file.Files;
import java.nio.file.Path;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/** The {@link ContextConfigurationAnalyzer} reads the context xml */
public class ContextConfigurationAnalyzer {

  /**
   * Gets ContextConfigurationReader based on templates type
   *
   * @param configRoot Path to configuration root directory
   * @return ContextConfigurationReader to use
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public static AbstractContextConfigurationReader getReader(Path configRoot) throws InvalidConfigurationException {

    // TODO: check for conflict between old and new configuration
    if (Files.exists(configRoot.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH))) {
      return new ContextConfigurationReader(configRoot);
    } else {
      return new ContextConfigurationSetReader(configRoot);
    }
  }

}
