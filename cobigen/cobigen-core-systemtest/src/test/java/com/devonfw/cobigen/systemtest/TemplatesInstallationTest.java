package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for template installation with the cobigen startup
 *
 */
public class TemplatesInstallationTest extends AbstractApiTest {

  /**
   * Tests if the templates specified in the .cobigen file will be loaded at startup with the template-set structure and
   * an existing downloaded folder.
   *
   * TODO: Check if this test is valid
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartup() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "downloaded");
      File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
        writer.write(
            "template-sets.installed=com.devonfw.cobigen.templates:crud-openapi-angular-client-app:2021.12.007-SNAPSHOT");
      }
      CobiGenFactory.create(templateSets.toURI());
      assertThat(downloaded.listFiles()).hasSize(2);
    });

  }

  /**
   * Tests if the templates specified in the .cobigen file won´t be loaded when an adapted folder already exists.
   *
   * TODO: Check if this test is valid
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupAdapted() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File adapted = this.tmpFolder.newFolder("TemplateSetsInstalledTest", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
          ConfigurationConstants.ADAPTED_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, ConfigurationConstants.DOWNLOADED_FOLDER);
      File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
        writer.write(
            "template-sets.installed=com.devonfw.cobigen.templates:crud-openapi-angular-client-app:2021.12.007-SNAPSHOT");
      }
      CobiGenFactory.create(templateSets.toURI());
      assertThat(downloaded.listFiles()).hasSize(2);
    });

  }

  /**
   * Tests if the templates specified in the .cobigen file will be loaded at startup with the template-set structure
   * without an existing downloaded folder.
   *
   * TODO: Check if this test is valid
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupWithoutDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = templateSets.toPath().resolve(ConfigurationConstants.DOWNLOADED_FOLDER).toFile();
      File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
        writer.write(
            "template-sets.installed=com.devonfw.cobigen.templates:crud-openapi-angular-client-app:2021.12.007-SNAPSHOT");
      }
      CobiGenFactory.create(templateSets.toURI());
      assertThat(downloaded.listFiles()).hasSize(2);
    });
  }

  /**
   * Tests if the templates specified in the .cobigen file will be checked for the correct format defined in the
   * documentation.
   *
   * TODO: Check if this test is valid
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupWithWrongCoordinates() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "downloaded");
      File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
        writer.write("template-sets.installed=com.com.com:com-com:com.com");
      }
      CobiGenFactory.create(templateSets.toURI());
      assertThat(downloaded.listFiles()).hasSize(0);
    });
  }
}
