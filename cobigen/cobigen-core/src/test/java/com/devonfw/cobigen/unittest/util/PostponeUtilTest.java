package com.devonfw.cobigen.unittest.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.impl.util.PostponeUtil;

/**
 * Tests TimestampUtil class on write to the configuration file and read from the configuration file correctly
 *
 */
public class PostponeUtilTest {

  /**
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   *
   * Tests the write method from TimestampUtil also tests that only one timestamp will be stored at a time
   *
   * @throws IOException test fails
   */
  @Test
  public void testWriteTimestamp() throws IOException {

    Path configFile = Files.createFile(this.tmpFolder.getRoot().toPath().resolve("configFile"));
    Timestamp InstantTimestamp = PostponeUtil.createInstantTimestamp();
    PostponeUtil.addATimestampForOneMonth(configFile);
    PostponeUtil.writeTimestamp(configFile, InstantTimestamp);

    Timestamp InstantTimestampCopy = PostponeUtil.readTimestamp(configFile);

    assertThat(InstantTimestamp).isEqualTo(InstantTimestampCopy);

  }

  /**
   *
   * Tests the readTimestamp method from TimestampUtil
   *
   * @throws IOException test fails
   */
  @Test
  public void testReadTimestamp() throws IOException {

    Path configFile = Files.createFile(this.tmpFolder.getRoot().toPath().resolve("configFile"));
    Timestamp InstantTimestamp = PostponeUtil.createInstantTimestamp();
    PostponeUtil.writeTimestamp(configFile, InstantTimestamp);

    Timestamp InstantTimestampCopy = PostponeUtil.readTimestamp(configFile);

    assertThat(InstantTimestamp).isEqualTo(InstantTimestampCopy);

  }
}
