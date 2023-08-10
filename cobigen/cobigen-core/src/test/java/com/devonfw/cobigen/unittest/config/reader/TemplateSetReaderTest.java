package com.devonfw.cobigen.unittest.config.reader;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.reader.TemplateSetReader;
import com.devonfw.cobigen.impl.config.reader.TemplateSetsConfigReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplateSetReader}
 */

public class TemplateSetReaderTest extends AbstractUnitTest {

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

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test // (expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    // when
    Path faultyPath = TEST_FILE_ROOT_PATH.resolve("faulty").toAbsolutePath().resolve("template-sets");
    assertThatThrownBy(() -> {

      TemplateSetsConfigReader reader = new TemplateSetsConfigReader(faultyPath);
      reader.readContextConfiguration();

    }).isInstanceOf(InvalidConfigurationException.class)
        .hasMessage(faultyPath + ":\n" + "Could not find any template-set configuration file in the given folder.");

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

      TemplateSetsConfigReader reader = new TemplateSetsConfigReader(INVALID_CONFIGURATION_PATH);
      reader.readContextConfiguration();

    }).isInstanceOf(InvalidConfigurationException.class).hasMessage(INVALID_CONFIGURATION_PATH.toAbsolutePath() + ":\n"
        + "Could not find any template-set configuration file in the given folder.");

  }

  /**
   * Tests if duplicated template sets using same trigger id and increment name were not loadable and an exception was
   * thrown
   *
   * @throws Exception test fails
   */
  @Ignore // TODO: Detection and handling of duplicates needs to be implemented first, then the test can be enabled,
          // see: https://github.com/devonfw/cobigen/issues/1663
  @Test
  public void testTemplateSetsDuplicatedThrowsError() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsDuplicatedTest");
    Path templateSetPathAdapted = TEST_FILE_ROOT_PATH.resolve("valid_template_sets_duplicated");
    FileUtils.copyDirectory(templateSetPathAdapted.toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      ContextConfiguration templateSetConfiguration = new ContextConfiguration(null, null,
          folder.toPath().resolve("template-sets"));
      // TODO add check for proper exception message
    });
  }

  /**
   * Tests if a template set configuration can be found from a template set jar file
   *
   * @throws Exception test fails
   *
   */
  @Test
  public void testTemplateSetsDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPath = TEST_FILE_ROOT_PATH.resolve("valid_template_sets_downloaded/");
    FileUtils.copyDirectory(templateSetPath.toFile(), folder);
    CobiGenPaths.setCobiGenHomeTestPath(folder.toPath());

    TemplateSetsConfigReader reader = new TemplateSetsConfigReader(folder.toPath().resolve("template-sets"));
    assertThat(reader.readContextConfiguration().getTriggers().size()).isEqualTo(1);

  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory
   *
   * @throws Exception test fails
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPath = TEST_FILE_ROOT_PATH.resolve("valid_template_sets/");
    FileUtils.copyDirectory(templateSetPath.toFile(), folder);
    CobiGenPaths.setCobiGenHomeTestPath(folder.toPath());

    TemplateSetsConfigReader reader = new TemplateSetsConfigReader(folder.toPath().resolve("template-sets"));
    assertThat(reader.readContextConfiguration().getTriggers().size()).isEqualTo(3);

  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory, even if an invalid folder .settings was added to the adapted folder
   *
   * @throws Exception test fails
   *
   */
  @Test
  public void testGetTemplatesWithInvalidAdaptedFolder() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    Path templateSetPath = TEST_FILE_ROOT_PATH.resolve("valid_template_sets/");
    FileUtils.copyDirectory(templateSetPath.toFile(), folder);
    // create an invalid folder which has to be ignored
    Files.createDirectory(folder.toPath().resolve("template-sets").resolve("adapted").resolve(".settings"));

    CobiGenPaths.setCobiGenHomeTestPath(folder.toPath());

    TemplateSetsConfigReader reader = new TemplateSetsConfigReader(folder.toPath().resolve("template-sets"));
    assertThat(reader.readContextConfiguration().getTriggers().size()).isEqualTo(3);

  }

}