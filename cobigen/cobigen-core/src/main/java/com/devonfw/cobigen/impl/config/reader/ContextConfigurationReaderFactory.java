package com.devonfw.cobigen.impl.config.reader;

import java.nio.file.Path;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/** The {@link ContextConfigurationReaderFactory} creates a context configuration reader for reading the context.xml */
public class ContextConfigurationReaderFactory {

  /**
   * Create a configuration reader instance based on template configuration
   *
   * @param configRoot Path to configuration root directory
   * @return AbstractContextConfigurationReader the context configuration reader
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public static AbstractContextConfigurationReader getReader(Path configRoot) throws InvalidConfigurationException {

    if (configRoot.toUri().getScheme().equals("jar")
        || !configRoot.getFileName().toString().equals(ConfigurationConstants.TEMPLATE_SETS_FOLDER)) {
      return new ContextConfigurationReader(configRoot);
    }

    return new ContextConfigurationSetReader(configRoot);
  }
}
