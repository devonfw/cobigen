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

    new ContextConfigurationReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests v2.2 context configuration reading two templates with context.xmls
   *
   */
  @Test
  public void testNewModularConfiguration() {

    ContextConfigurationReader context = new ContextConfigurationReader(
        Paths.get(new File(testFileRootPath + "valid_new").toURI()));
    assertThat(context.getContextFiles().size()).isEqualTo(2);
  }

  /**
   * Tests v2.1 context configuration reading one template with one context.xml
   *
   */
  @Test
  public void testOldConfiguration() {

    ContextConfigurationReader context = new ContextConfigurationReader(
        Paths.get(new File(testFileRootPath + "valid_source_folder").toURI()));
    assertThat(context.getContextFiles().size()).isEqualTo(1);
  }

  /**
   * Tests if a conflict between old and new template structures occurred and an exception was thrown
   *
   * @throws InvalidConfigurationException if a conflict occurred
   *
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testConflictConfiguration() throws InvalidConfigurationException {

    new ContextConfigurationReader(Paths.get(new File(testFileRootPath + "invalid_new").toURI()));
  }

  /**
   * Tests whether a valid configuration can be read from src/main/templates folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromRootAndSourceFolder() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder").toURI());
  }

  /**
   * Tests whether a valid configuration can be read from a zip file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testReadConfigurationFromZip() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI());
  }

}
