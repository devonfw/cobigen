package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * Tests the usage of the adapt-templates command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class AdaptTemplatesCommandTest extends AbstractCliTest {

  /**
   * Simulate the download of the template set jars, as this not yet implemented. This method can be removed later
   *
   * @throws URISyntaxException if the path could not be created properly
   * @throws IOException if accessing a directory or file fails
   */
  @Before
  public void initAdaptTemplatesTest() throws URISyntaxException, IOException {

    Path cliSystemTestPath = new File(
        AdaptTemplatesCommandTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().toPath();
    Path templateJar = cliSystemTestPath.resolve("src/test/resources/testdata/crud-java-server-app.jar");
    if (Files.exists(templateJar)) {
      Path downloadedTemplateSetsPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
          .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
      if (!Files.exists(downloadedTemplateSetsPath)) {
        Files.createDirectories(downloadedTemplateSetsPath);
      }
      Files.copy(templateJar, downloadedTemplateSetsPath.resolve(templateJar.getFileName()));
    }
  }

  /**
   * Checks if adapt-templates command successfully created cobigen templates folder and its sub folders
   *
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplatesTest() throws Exception {

    String args[] = new String[1];
    args[0] = "adapt-templates";

    execute(args, false);

    Path cobigenTemplateSetsFolderPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path downloadedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path adaptedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    assertThat(cobigenTemplateSetsFolderPath).exists();
    assertThat(downloadedTemplateSetsFolderPath).exists();
    assertThat(adaptedTemplateSetsFolderPath).exists();

    // check if adapted template set exists
    Path templateSet = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app");
    assertThat(templateSet).exists();
    // check if context configuration exists
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)).exists();
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
        .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)).exists();
  }
}
