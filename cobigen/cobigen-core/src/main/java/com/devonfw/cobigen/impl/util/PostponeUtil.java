package com.devonfw.cobigen.impl.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.CobiGenPaths;

/**
 * This util class is used to set(create) a timestamp property, read it, and checks if exists. Also can check if a
 * timestamp passed the current time.
 *
 */
public class PostponeUtil {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(PostponeUtil.class);

  /**
   * Writes the Timestamp to a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @param timestamp to be added to the config file
   * @throws IOException
   */
  public static void writeTimestamp(Path configFile, Timestamp timestamp) throws IOException {

    Properties props = new Properties();

    if (!Files.exists(configFile))
      Files.createFile(configFile);

    props.setProperty(ConfigurationConstants.CONFIG_PROPERTY_POSTPONE_UPGRADE_MESSAGE_UNTIL, timestamp.toString());
    try (FileOutputStream configfileOutputStream = new FileOutputStream(configFile.toFile())) {
      props.store(configfileOutputStream, null);
    } catch (IOException e) {
      LOG.error("An error has occurred while writing the timestamp to the config file.", e);
    }
  }

  /**
   * Reads the Timestamp from a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @return the timestamp found in a config file
   */
  public static Timestamp readTimestamp(Path configFile) {

    Properties props = new Properties();

    try (FileInputStream configfileInputStream = new FileInputStream(configFile.toFile())) {
      props.load(configfileInputStream);
    } catch (IOException e) {
      LOG.error("An error has occurred while reading the timestamp from the config file.", e);
      throw new CobiGenRuntimeException("An error has occurred while reading the timestamp from the config file.");
    }
    String strTimestamp = ConfigurationConstants.CONFIG_PROPERTY_POSTPONE_UPGRADE_MESSAGE_UNTIL;
    String stampprop = props.getProperty(strTimestamp);
    if (stampprop != null) {
      return Timestamp.valueOf(props.getProperty(strTimestamp));
    }
    return null;
  }

  /**
   * Creates a time stamp
   *
   * @return a new Timestamp with the current time
   */
  public static Timestamp createInstantTimestamp() {

    Date currentDate = new Date();
    return new Timestamp(currentDate.getTime());
  }

  /**
   * Searches any Timestamp inside the config file in the HomePath, to determine whether that time is already passed.
   *
   * @return true if the current time already passed the time written in the config file.
   */
  public static boolean isTimePassed() {

    return isTimePassed(getHomePath());
  }

  /**
   * Searches any Timestamp inside the config file, to determine whether that time is already passed.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @return boolean true if config file not exists, if no Timestamp found, or if the current time already passed the
   *         time written in the config file.
   */
  public static boolean isTimePassed(Path configFile) {

    if (!Files.exists(configFile)) {
      return true;
    } else if (PostponeUtil.readTimestamp(configFile) == null) {
      return true;
    } else if (PostponeUtil.createInstantTimestamp().after(PostponeUtil.readTimestamp(configFile))) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adds a new Timestamp to the config file in HomePath.
   */
  public static void addATimestampForOneMonth() {

    addATimestampForOneMonth(getHomePath());
  }

  /**
   * Adds a new Timestamp in the config file with the current time + one month
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void addATimestampForOneMonth(Path configFile) {

    addATimestampForASpecificTime(configFile, DateUtils.addMonths(new Date(), 1));
  }

  /**
   * Adds a specific date to the config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @param date to be added to the config file
   */
  public static void addATimestampForASpecificTime(Path configFile, Date date) {

    try {
      PostponeUtil.writeTimestamp(configFile, new Timestamp(date.getTime()));
    } catch (IOException e) {
      LOG.error("An error has occurred while writing the timestamp to the config file.");
      throw new CobiGenRuntimeException("An error has occurred while writing the timestamp to the config file.");
    }
  }

  /**
   * @return Cobigen HomePath
   */
  public static Path getHomePath() {

    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();

    return cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
  }
}
