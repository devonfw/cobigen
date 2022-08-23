package com.devonfw.cobigen.unittest.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.Test;

import com.devonfw.cobigen.impl.util.TimestampUtil;

/**
 * Tests TimestampUtil class on write to the config file and read from the config file correctly
 *
 */
public class TimestampUtilTest {

  /**
   *
   * Tests the write method from TimestampUtil also tests that only one Timestamp will be stored at a time
   *
   * @throws IOException
   */
  @Test
  public void testWriteTimestamp() throws IOException {

    Path configFile = Paths.get("src/test/resources/testdata/unittest/config/util/config");
    Timestamp InstantTimestamp = TimestampUtil.createInstantTimestamp();
    TimestampUtil.addATimestampForOneMonth(configFile);
    TimestampUtil.writeTimestamp(configFile, InstantTimestamp);

    Timestamp InstantTimestampCopy = TimestampUtil.readTimestamp(configFile);

    Assert.assertEquals(InstantTimestamp, InstantTimestampCopy);

    Files.delete(configFile.toFile());

  }

  /**
   *
   * Tests the readTimestamp method from TimestampUtil
   *
   * @throws IOException
   */
  @Test
  public void testReadTimestamp() throws IOException {

    Path configFile = Paths.get("src/test/resources/testdata/unittest/config/util/config");
    Timestamp InstantTimestamp = TimestampUtil.createInstantTimestamp();
    TimestampUtil.writeTimestamp(configFile, InstantTimestamp);

    Timestamp InstantTimestampCopy = TimestampUtil.readTimestamp(configFile);

    Assert.assertEquals(InstantTimestamp, InstantTimestampCopy);

    Files.delete(configFile.toFile());

  }
}
