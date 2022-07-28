package com.devonfw.cobigen.unittest.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

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
   * Tests the read and write method from TimestampUtil also tests that only one Timestamp will be stored at a time
   *
   * @throws IOException
   */
  @Test
  public void testWriteAndReadTimestamp() throws IOException {

    Path configFile = Paths.get("src/test/resources/testdata/unittest/config/util/config");
    Timestamp InstantTimestamp = TimestampUtil.createInstantTimestamp();
    TimestampUtil.addATimestampForOneMonth(configFile);
    TimestampUtil.writeTimestamp(configFile, InstantTimestamp);

    Timestamp InstantTimestampCopy = TimestampUtil.readTimestamp(configFile);

    Assert.assertEquals(InstantTimestamp, InstantTimestampCopy);

  }

}
