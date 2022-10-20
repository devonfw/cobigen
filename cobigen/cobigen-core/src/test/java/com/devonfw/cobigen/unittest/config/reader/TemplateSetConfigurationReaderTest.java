package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;
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
  private final static Path TEST_FILE_ROOT_PATH = Paths
      .get("src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/");

  private final static Path INVALID_CONFIGURATION_PATH = Paths.get(TEST_FILE_ROOT_PATH + "/invalid_template_sets")
      .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);

  private final static Path VALID_CONFIGURATION_PATH = Paths.get(TEST_FILE_ROOT_PATH + "/valid_template_sets")
      .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER + "/adapted/test-template");

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test // (expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    try {
      new TemplateSetConfiguration(TEST_FILE_ROOT_PATH.resolve("faulty"));

    } catch (InvalidConfigurationException ice) {
      assertThat(ice).hasMessage(TEST_FILE_ROOT_PATH.resolve("faulty").toAbsolutePath() + ":\n"
          + "Could not find a folder in which to search for the template-set configuration file.");
    }

  }

  /**
   * Tests whether an {@link InvalidConfigurationException} will be thrown when no template set configuration is found
   * in the template-sets directory
   *
   * @throws InvalidConfigurationException if no template set configuration is found
   *
   */
  @Test
  public void testInvalidTemplateSets() throws InvalidConfigurationException {

    try {
      new TemplateSetConfiguration(INVALID_CONFIGURATION_PATH);
    } catch (InvalidConfigurationException ice) {
      assertThat(ice).hasMessage(INVALID_CONFIGURATION_PATH.toAbsolutePath() + ":\n"
          + "Could not find any template-set configuration file in the given folder.");
    }
  }

  /**
   * Tests if a template set configuration can be found from a template set jar file
   *
   */
  @Test
  public void testTemplateSetsDownloaded() {

    Path templateSetPath = TEST_FILE_ROOT_PATH
        .resolve("valid_template_sets/" + ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    templateSetPath = Path.of(templateSetPath + "/downloaded");
    TemplateSetConfiguration testDecorator = new TemplateSetConfiguration(templateSetPath);
    assertThat(testDecorator.getTemplateSetFiles().size()).isEqualTo(1);
  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() {

    Path templateSetPathAdapted = TEST_FILE_ROOT_PATH
        .resolve("valid_template_sets/" + ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    TemplateSetConfiguration testDecoratorAdapted = new TemplateSetConfiguration(templateSetPathAdapted);
    Path templateSetPathDownloaded = TEST_FILE_ROOT_PATH
        .resolve("valid_template_sets/" + ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    templateSetPathDownloaded = Path.of(templateSetPathDownloaded + "/downloaded");
    TemplateSetConfiguration testDecoratorDownloaded = new TemplateSetConfiguration(templateSetPathDownloaded);

    assertThat(testDecoratorAdapted.getTemplateSetFiles().size() + testDecoratorDownloaded.getTemplateSetFiles().size())
        .isEqualTo(3);
  }

}