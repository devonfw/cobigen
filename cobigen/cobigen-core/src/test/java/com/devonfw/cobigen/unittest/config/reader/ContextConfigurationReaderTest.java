package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link ContextConfigurationReader}
 */
public class ContextConfigurationReaderTest extends AbstractUnitTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/reader/ContextConfigurationReaderTest/";

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    ContextConfigurationReader reader = new ContextConfigurationReader(
        Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests whether a valid v2.1 configuration can be read from src/main/templates folder
   *
   * Backward Compatibility test, remove when monolithic context.xml is deprecated.
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromOldConfiguration() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder").toURI(), true);
  }

  /**
   * Tests that exactly one v3.0 context configuration is read
   *
   * Backward Compatibility test, remove when monolithic context.xml is deprecated.
   *
   */
  @Test
  public void testOldConfiguration() {

    ContextConfigurationReader context = new ContextConfigurationReader(
        Paths.get(new File(testFileRootPath + "valid_source_folder").toURI()));
    assertThat(context.getContextFiles().size()).isEqualTo(1);
  }

  /**
   * Tests whether a valid configuration can be read from a zip file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testReadConfigurationFromZip() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI(), true);
  }

}