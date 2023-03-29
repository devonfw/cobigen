package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for download processes of template sets.
 */
public class DownloadTemplateSetsIT extends AbstractApiTest {

  /**
   * Root path to the resources used in this test
   */
  private static String testFileRootPath = apiTestsRootPath + "DownloadTemplateSets";

  /**
   * Tests if a template set SNAPSHOT defined in properties template-sets.installed can be retrieved and downloaded from
   * sonatype
   *
   * @throws Exception test fails
   */
  @Test
  public void testDownloadTemplateSetWithProperty() throws Exception {

    File folder = this.tmpFolder.newFolder("DownloadTemplatesetsTest");

    File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
      writer.write(
          "template-sets.installed=com.devonfw.cobigen.templates:crud-openapi-angular-client-app:2021.12.007-SNAPSHOT");
    }

    Path templateSetFolder = Files
        .createDirectories(folder.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      CobiGenFactory.create(templateSetFolder.toUri(), false);

    });

    Path downloadedFolder = templateSetFolder.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    assertThat(downloadedFolder).exists();
    assertThat(downloadedFolder.toFile().listFiles()).hasSize(2);
  }

  /**
   * Tests if a template set without a version number defined in properties template-sets.installed can be retrieved and
   * the latest version gets downloaded from sonatype
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore // TODO: re-enable when https://github.com/devonfw/cobigen/issues/1656 capability to download LATEST version
          // of template-sets was implemented
  public void testDownloadTemplateSetWithoutVersionPropertyDownloadsLatestVersion() throws Exception {

    File folder = this.tmpFolder.newFolder("DownloadTemplatesetsTest");

    File target = new File(folder, ConfigurationConstants.COBIGEN_CONFIG_FILE);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
      writer.write("template-sets.installed=com.devonfw.cobigen.templates:crud-openapi-angular-client-app");
    }

    Path templateSetFolder = Files
        .createDirectories(folder.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      CobiGenFactory.create(templateSetFolder.toUri(), false);

    });

    Path downloadedFolder = templateSetFolder.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    assertThat(downloadedFolder).exists();
    assertThat(downloadedFolder.toFile().listFiles()).hasSize(2);
  }
}
