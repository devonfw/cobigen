package com.devonfw.cobigen.impl.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * This Class is used to set the timestamp property
 *
 */
public class TimeStampUtil {

  /**
   * The constructor.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public TimeStampUtil(Path configFile) {

    TimeStampReader(configFile);
  }

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TimeStampUtil.class);

  private static Timestamp timeStamp;

  /**
   * Writes the timestamp in a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @throws IOException
   */
  public static void TimeStampWriter(Path configFile) throws IOException {

    TimeStampWriter(configFile, null);
  }

  /**
   * Writes the timestamp in a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @param timestamp to be added to the config file
   * @throws Exception
   */
  public static void TimeStampWriter(Path configFile, Timestamp timestamp) throws IOException {

    Properties props = new Properties();
    try {
      if (!Files.exists(configFile))
        Files.createFile(configFile);

      props.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TIME_STAMP, timestamp.toString());
      props.store(new FileOutputStream(configFile.toFile()), null);
    } catch (IOException e) {
      LOG.error("An error occurd while writing the timestamp to the config file.");
    }
  }

  /**
   * Reads the timestamp from a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static Timestamp TimeStampReader(Path configFile) {

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(configFile.toFile()));
    } catch (IOException e) {
      LOG.error("An error occurd while loading the timestamp from the config file.");
    }

    String strTimeStamp = ConfigurationConstants.CONFIG_PROPERTY_TIME_STAMP;
    String stampprop = props.getProperty(strTimeStamp);
    if (stampprop != null) {
      Timestamp timeStamp1 = Timestamp.valueOf(props.getProperty(strTimeStamp));
      return timeStamp1;
    }
    return null;
  }

  /**
   * Delete all timestamps from a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void TimeStampDeleter(Path configFile) {

    try {
      TimeStampWriter(configFile, null);
    } catch (IOException e) {
      LOG.error("An error occurd while deleting the timestamps from the config file.");
    }
  }

}
