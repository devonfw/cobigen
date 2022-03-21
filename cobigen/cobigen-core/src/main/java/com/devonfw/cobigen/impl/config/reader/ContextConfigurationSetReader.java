package com.devonfw.cobigen.impl.config.reader;

import java.nio.file.Path;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/** The {@link ContextConfigurationSetReader} reads the context xml */
public class ContextConfigurationSetReader extends AbstractContextConfigurationReader {

  /**
   * The constructor.
   *
   * @param configRoot
   * @throws InvalidConfigurationException
   */
  public ContextConfigurationSetReader(Path configRoot) throws InvalidConfigurationException {

    super(configRoot);
  }

}
