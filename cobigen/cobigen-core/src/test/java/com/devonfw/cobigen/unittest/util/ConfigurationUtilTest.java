package com.devonfw.cobigen.unittest.util;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

/**
 * Test suite for Configuration Utility scenarios.
 */
public class ConfigurationUtilTest {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Tests findTemplateSetLocation logic Checks if a template jar is located inside the downloaded folder of
   * template-sets Checks if a template jar can be loaded directly when being set from the .cobigen properties file
   *
   * @throws Exception
   */
  @Test
  public void testFindTemplateSetsLocation() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      System.setProperty("user.home", userHome.getAbsolutePath());
      Path templatesFolder = userHome.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Files.createDirectories(templatesFolder);
      String templatesArtifact = "templates-devon4j-1.0.jar";

      Path downloadedTemplatesDirectory = templatesFolder.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
      Files.createDirectories(downloadedTemplatesDirectory);
      Path templatesJar = templatesFolder.resolve(templatesArtifact);
      Files.createFile(templatesJar);
      // found template-sets directory
      assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(templatesFolder.toFile().toURI());
      Files.delete(downloadedTemplatesDirectory);

      // configuration file exists
      File randomDirectoryForConfigFile = this.tmpFolder.newFolder();
      File randomDirectoryForTemplates = this.tmpFolder.newFolder();
      File configFile = new File(randomDirectoryForConfigFile, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      File templates = new File(randomDirectoryForTemplates, templatesArtifact);
      configFile.createNewFile();
      templates.createNewFile();

      String templatesLocation = templates.getAbsolutePath().replace("\\", "\\\\");
      FileUtils.writeStringToFile(configFile,
          ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH + "=" + templatesLocation, StandardCharsets.UTF_8);

      withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, randomDirectoryForConfigFile.getAbsolutePath())
          .execute(() -> {
            // configuration file found from environment variable
            assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(templates.toURI());
          });

      Path configFileInCobigenHome = userHome.toPath().resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      FileUtils.copyFile(configFile, configFileInCobigenHome.toFile());
      // configuration file found in cobigen home directory
      assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(templates.toURI());
    });
  }

  /**
   * Tests findTemplatesLocation logic Checks if a template jar is located inside the templates folder Checks if a
   * template jar can be loaded directly when being set from the .cobigen properties file
   *
   * @throws Exception
   */
  @Test
  public void testFindTemplatesLocation() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      System.setProperty("user.home", userHome.getAbsolutePath());
      Path templatesFolder = userHome.toPath().resolve(ConfigurationConstants.TEMPLATES_FOLDER);
      Files.createDirectories(templatesFolder);
      Path cbTemplates = templatesFolder.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      Files.createDirectories(cbTemplates);
      String templatesArtifact = "templates-devon4j-1.0.jar";

      Path templatesJar = cbTemplates.resolve(templatesArtifact);
      Files.createFile(templatesJar);
      // found template-sets directory
      assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(cbTemplates.toFile().toURI());
      // configuration file exists
      File randomDirectoryForConfigFile = this.tmpFolder.newFolder();
      File randomDirectoryForTemplates = this.tmpFolder.newFolder();
      File configFile = new File(randomDirectoryForConfigFile, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      File templates = new File(randomDirectoryForTemplates, templatesArtifact);
      configFile.createNewFile();
      templates.createNewFile();

      String templatesLocation = templates.getAbsolutePath().replace("\\", "\\\\");
      FileUtils.writeStringToFile(configFile,
          ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH + "=" + templatesLocation, StandardCharsets.UTF_8);

      withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, randomDirectoryForConfigFile.getAbsolutePath())
          .execute(() -> {
            // configuration file found from environment variable
            assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(templates.toURI());
          });

      Path configFileInCobigenHome = userHome.toPath().resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      FileUtils.copyFile(configFile, configFileInCobigenHome.toFile());
      // configuration file found in cobigen home directory
      assertThat(ConfigurationFinder.findTemplatesLocation()).isEqualTo(templates.toURI());
    });
  }
}
