package com.devonfw.cobigen.impl.config.reader;

import java.nio.file.Path;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader extends AbstractContextConfigurationReader {

  /**
   * The constructor.
   *
   * @param configRoot
   * @throws InvalidConfigurationException
   */
  public ContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    super(configRoot);
  }

}
