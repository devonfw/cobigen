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
import com.devonfw.cobigen.api.util.CobiGenPaths;

/**
 * This Class is used to set the timestamp property
 *
 */
public class TimestampUtil {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TimestampUtil.class);

  /**
   * Writes the timestamp in a config file.
   *
   * @param configFile the config file or .cobigen file containing the properties.
   * @param timestamp to be added to the config file
   * @throws IOException
   */
  public static void writeTimestamp(Path configFile, Timestamp timestamp) throws IOException {

    Properties props = new Properties();

    if (!Files.exists(configFile))
      Files.createFile(configFile);

    props.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TIME_STAMP, timestamp.toString());

    try (FileOutputStream configfileOutputStream = new FileOutputStream(configFile.toFile())) {
      props.store(configfileOutputStream, null);
    }

    catch (IOException e) {
      LOG.error("An error has occurred while writing the timestamp to the config file.", e);
    }
  }

  /**
   * Reads the timestamp from a config file.
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
    }

    String strTimestamp = ConfigurationConstants.CONFIG_PROPERTY_TIME_STAMP;
    String stampprop = props.getProperty(strTimestamp);
    if (stampprop != null) {
      return Timestamp.valueOf(props.getProperty(strTimestamp));
    }
    return null;
  }

  /**
   * creates a time stamp
   *
   * @return a new Timestamp with the current time
   */
  public static Timestamp createInstantTimestamp() {

    Date currentDate = new Date();
    return new Timestamp(currentDate.getTime());
  }

  @SuppressWarnings("javadoc")
  public static boolean isaMonthPassed() {

    return isaMonthPassed(getHomePath());
  }

  /**
   * @param configFile the config file or .cobigen file containing the properties.
   * @return boolean true if config file not exists, if no Timestamp found, or if 30 days already passed.
   */
  public static boolean isaMonthPassed(Path configFile) {

    if (!Files.exists(configFile) || Files.exists(configFile) && TimestampUtil.readTimestamp(configFile) == null
        || TimestampUtil.createInstantTimestamp().after(TimestampUtil.readTimestamp(configFile)))
      return true;
    return false;
  }

  @SuppressWarnings("javadoc")
  public static void addATimestampForOneMonth() {

    addATimestampForOneMonth(getHomePath());

  }

  /**
   * add a new Timestamp in the config file with the current time + one month
   *
   * @param configFile the config file or .cobigen file containing the properties.
   */
  public static void addATimestampForOneMonth(Path configFile) {

    Date afterMonthDate = DateUtils.addMonths(new Date(), 1);

    try {
      TimestampUtil.writeTimestamp(configFile, new Timestamp(afterMonthDate.getTime()));
    } catch (IOException e) {
      LOG.error("An error has occurred while writing the timestamp to the config file.");
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
