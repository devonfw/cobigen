package com.devonfw.cobigen.impl.util;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/**
 * TODO mdukhan This Class is used to set the timestamp property
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
   * getter of the timestamp.
   *
   * @return timeStamp
   */
  public static Timestamp getTimeStamp() {

    return timeStamp;
  }

  /**
   * Setter of the timestamps.
   *
   * @param timeStamp new value of {@link #gettimeStamp}.
   */
  public static void setTimeStamp(Timestamp timeStamp) {

    TimeStampUtil.timeStamp = timeStamp;
  }

  /**
   * Writes the timestamp in a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void TimeStampWriter(Path configFile) {

    TimeStampWriter(configFile, null);
  }

  /**
   * Writes the timestamp in a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @param timestamp to be added to the config file
   */
  public static void TimeStampWriter(Path configFile, Timestamp timestamp) {

  }

  /**
   * Reads the timestamp from a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void TimeStampReader(Path configFile) {

    Properties props = new Properties();
    try {
      props = ConfigurationFinder.readConfigurationFile(configFile);
    } catch (InvalidConfigurationException e) {
      LOG.info("This path {} is invalid. Please provide a proper path with the config file. ", configFile);
    }

    String strTimeStamp = ConfigurationConstants.CONFIG_PROPERTY_TIME_STAMP;

    if (props.getProperty(strTimeStamp) != null) {
      Timestamp timeStamp1 = Timestamp.valueOf(props.getProperty(strTimeStamp));
      setTimeStamp(timeStamp1);
    }
  }

  /**
   * Delete a timestamp from a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void TimeStampDeleter(Path configFile, Timestamp timestamp) {

    // TODO Implement this method
  }

}
