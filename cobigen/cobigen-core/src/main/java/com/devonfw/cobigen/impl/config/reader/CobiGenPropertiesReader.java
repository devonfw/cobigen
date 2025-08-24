package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Reader for {@link ConfigurationConstants#COBIGEN_PROPERTIES} files.
 */
public class CobiGenPropertiesReader {

  /** The {@link Charset} used to read {@link ConfigurationConstants#COBIGEN_PROPERTIES}. */
  private static final Charset UTF_8 = Charset.forName("UTF-8");

  /**
   * @param folder the {@link Path} pointing to the folder that may contain a {@code cobigen.properties} file.
   * @return the new {@link Properties} containing the properties from a potential {@code cobigen.properties}. Will be
   *         {@code null} if no such properties file exists.
   */
  public static final Properties load(Path folder) {

    Path propertiesPath = folder.resolve(ConfigurationConstants.COBIGEN_PROPERTIES);
    if (!Files.exists(propertiesPath)) {
      return null;
    }

    Properties properties = new Properties();
    try (Reader reader = Files.newBufferedReader(propertiesPath, UTF_8)) {
      properties.load(reader);
    } catch (IOException e) {
      throw new CobiGenRuntimeException(
          "Failed to read " + ConfigurationConstants.COBIGEN_PROPERTIES + " from " + folder, e);
    }
    return properties;
  }

}
