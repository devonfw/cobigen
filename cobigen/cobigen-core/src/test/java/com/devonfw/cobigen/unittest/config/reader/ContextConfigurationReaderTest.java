package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.reader.AbstractContextConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReaderFactory;
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

    ContextConfigurationReaderFactory.getReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests whether an {@link InvalidConfigurationException} will be thrown when no context configuration is found in the
   * template-sets directory
   *
   * @throws InvalidConfigurationException if no context configuration is found
   *
   */
  @Test
  public void testInvalidTemplateSets() throws InvalidConfigurationException {

    Path configurationPath = Paths.get(new File(testFileRootPath + "invalid_template_sets").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Throwable invalidException = assertThrows(InvalidConfigurationException.class, () -> {
      ContextConfigurationReaderFactory.getReader(configurationPath);
    });

    assertThat(invalidException instanceof InvalidConfigurationException);
    assertThat(invalidException.getMessage()).contains("Could not find any context configuration file.");
  }

  /**
   * Tests whether a valid configuration can be read from template-sets/adapted folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromNewConfiguration() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_template_sets_adapted").toURI().resolve("template-sets"));
  }

  /**
   * Tests if multiple (2) context configurations in the adapted template sets are found
   *
   */
  @Test
  public void testTemplateSetsAdapted() {

    Path configurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets_adapted").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    AbstractContextConfigurationReader context = ContextConfigurationReaderFactory.getReader(configurationPath);
    assertThat(context.getContextFiles().size()).isEqualTo(2);
  }

  /**
   * Tests if a context configuration can be found from a template set jar file
   *
   */
  @Test
  public void testTemplateSetsDownloaded() {

    Path configurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets_downloaded").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    AbstractContextConfigurationReader context = ContextConfigurationReaderFactory.getReader(configurationPath);
    assertThat(context.getContextFiles().size()).isEqualTo(1);
  }

  /**
   * Tests if context configurations can be found in both adapted and downloaded folder of the template sets directory
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() {

    Path configurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    AbstractContextConfigurationReader context = ContextConfigurationReaderFactory.getReader(configurationPath);
    assertThat(context.getContextFiles().size()).isEqualTo(3);
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

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder").toURI());
  }

  /**
   * Tests that exactly one v2.1 context configuration is read
   *
   * Backward Compatibility test, remove when monolithic context.xml is deprecated.
   *
   */
  @Test
  public void testOldConfiguration() {

    AbstractContextConfigurationReader context = ContextConfigurationReaderFactory
        .getReader(Paths.get(new File(testFileRootPath + "valid_source_folder").toURI()));
    assertThat(context.getContextFiles().size()).isEqualTo(1);
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