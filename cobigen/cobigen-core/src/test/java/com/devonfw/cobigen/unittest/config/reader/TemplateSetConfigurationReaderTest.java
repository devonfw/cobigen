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
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplateSetConfigurationReader}
 */
public class TemplateSetConfigurationReaderTest extends AbstractUnitTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/";

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    new TemplateSetConfigurationReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests whether an {@link InvalidConfigurationException} will be thrown when no template set configuration is found in the
   * template-sets directory
   *
   * @throws InvalidConfigurationException if no template set configuration is found
   *
   */
  @Test
  public void testInvalidTemplateSets() throws InvalidConfigurationException {

    Path configurationPath = Paths.get(new File(testFileRootPath + "invalid_template_sets").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Throwable invalidException = assertThrows(InvalidConfigurationException.class, () -> {
      new TemplateSetConfigurationReader(configurationPath);
    });

    assertThat(invalidException instanceof InvalidConfigurationException);
    assertThat(invalidException.getMessage()).contains("Could not find any template set configuration file.");
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
   * Tests if a context configuration can be found from a template set jar file
   *
   */
  @Test
  public void testTemplateSetsDownloaded() {

    Path configurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets_downloaded").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    TemplateSetConfigurationReader context = new TemplateSetConfigurationReader(configurationPath);
    assertThat(context.getTemplateSetFiles().size()).isEqualTo(1);
  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() {

    Path configurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    TemplateSetConfigurationReader context = new TemplateSetConfigurationReader(configurationPath);
    assertThat(context.getTemplateSetFiles().size()).isEqualTo(3);
  }

  /**
   * Tests whether a valid template set configuration can be read from a zip file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testReadConfigurationFromZip() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI(), true);
  }

}