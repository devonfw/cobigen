package com.devonfw.cobigen.unittest.config.reader;

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
   * Tests whether not having a context.xml top level in a template set results in an
   * {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfigurationNotTopLevel() throws InvalidConfigurationException {

    new ContextConfigurationReader(
        Paths.get(new File(testFileRootPath + "invalid_source_context_not_top_level").toURI()));
  }

  /**
   * Tests whether a valid configuration can be read from src/main/templates folder (old config location) Backward
   * Compatibility test, remove when monolithic context.xml is deprecated.
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromRootAndSourceFolder() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder_root").toURI());
  }

  /**
   * Tests whether a valid configuartion can be read from src/main/templates/templateSet (new config location)
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromTemplateSetFolder() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder_modular").toURI());
  }

  /**
   * Tests whether a valid configuartion can be read from both root and templateSet folders (new templates with old
   * custom templates) Backward Compatibility test, remove when monolithic context.xml is deprecated.
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromTemplateSetFolderAndRoot() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder_modular_and_root").toURI());
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
