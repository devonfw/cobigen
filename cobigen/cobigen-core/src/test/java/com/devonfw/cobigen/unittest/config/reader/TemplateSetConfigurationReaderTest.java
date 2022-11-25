package com.devonfw.cobigen.unittest.config.reader;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

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

    // when
    assertThatThrownBy(() -> {

      new TemplateSetConfiguration(TEST_FILE_ROOT_PATH.resolve("faulty"));

    }).isInstanceOf(InvalidConfigurationException.class)
        .hasMessage(TEST_FILE_ROOT_PATH.resolve("faulty").toAbsolutePath() + ":\n"
            + "Could not find a folder in which to search for the template-set configuration file.");

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

    assertThatThrownBy(() -> {

      new TemplateSetConfiguration(INVALID_CONFIGURATION_PATH);

    }).isInstanceOf(InvalidConfigurationException.class).hasMessage(INVALID_CONFIGURATION_PATH.toAbsolutePath() + ":\n"
        + "Could not find any template-set configuration file in the given folder.");

  }

  /**
   * Tests if a template set configuration can be found from a template set jar file
   *
   * @throws Exception
   *
   */
  @Test
  public void testTemplateSetsDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPathAdapted = TEST_FILE_ROOT_PATH.resolve("valid_template_sets_downloaded/");
    FileUtils.copyDirectory(templateSetPathAdapted.toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {

      TemplateSetConfiguration testDecorator;
      testDecorator = new TemplateSetConfiguration(folder.toPath().resolve("template-sets"));
      assertThat(testDecorator.getTemplateSetFiles().size()).isEqualTo(1);
    });
  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory
   *
   * @throws Exception
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPathAdapted = TEST_FILE_ROOT_PATH.resolve("valid_template_sets/");
    FileUtils.copyDirectory(templateSetPathAdapted.toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {

      TemplateSetConfiguration testDecoratorAdapted = new TemplateSetConfiguration(
          folder.toPath().resolve("template-sets"));
      TemplateSetConfiguration testDecoratorDownloaded = new TemplateSetConfiguration(
          folder.toPath().resolve("template-sets").resolve("downloaded"));

      assertThat(
          testDecoratorAdapted.getTemplateSetFiles().size() + testDecoratorDownloaded.getTemplateSetFiles().size())
              .isEqualTo(2);
    });
  }

  /**
   *
   * @throws Exception
   */
  @Test
  public void testTemplateScans() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPathAdapted = TEST_FILE_ROOT_PATH.resolve("valid_template_sets_adapted/");
    FileUtils.copyDirectory(templateSetPathAdapted.toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      TemplateSetConfiguration templateSetConfiguration = new TemplateSetConfiguration(
          folder.toPath().resolve("template-sets"));
      TemplateSetConfigurationReader reader = templateSetConfiguration.getTemplateSetConfigurationReader();
      // Map<String, Template> map = reader.loadTemplates();
      // then
      // assertThat(templates).isNotNull().hasSize(6);
      //
      // String templateIdFooClass = "prefix_FooClass.java";
      // Template templateFooClass = templates.get(templateIdFooClass);
      // assertThat(templateFooClass).isNotNull();
      // assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
      // assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("foo/FooClass.java.ftl");
      // assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/foo/FooClass.java");
      // assertThat(templateFooClass.getMergeStrategy()).isNull();
    });
  }

}