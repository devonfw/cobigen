package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * Tests the usage of the adapt-templates command.
 */
public class AdaptTemplatesCommandIT extends AbstractCliTest {

  /**
   * Checks if adapt-templates command successfully created adapted folder and its sub folders
   *
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplateSetTest() throws Exception {

    Path cliSystemTestPath = new File(
        AdaptTemplatesCommandIT.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
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

    String args[] = new String[2];
    args[0] = "adapt-templates";
    args[1] = "--all";

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
        .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();

  }

  /**
   * Checks if adapt-templates command successfully created cobigen templates folder and its sub folders
   *
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplatesTest() throws Exception {

    Path cliSystemTestPath = new File(
        AdaptTemplatesCommandIT.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().toPath();
    Path templateSourcesJar = cliSystemTestPath
        .resolve("src/test/resources/testdata/templates-devon4j-1.0-sources.jar");
    Path templateJar = cliSystemTestPath.resolve("src/test/resources/testdata/templates-devon4j-1.0.jar");
    Path templatesPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
    Path CobigenTemplatesPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    if (!Files.exists(templatesPath)) {
      Files.createDirectories(templatesPath);
    }
    Files.copy(templateSourcesJar, templatesPath.resolve(templateJar.getFileName()));
    Files.copy(templateJar, templatesPath.resolve(templateSourcesJar.getFileName()));

    String args[] = new String[2];
    args[0] = "adapt-templates";
    args[1] = "--all";

    execute(args, false, false, true);

    assertThat(CobigenTemplatesPath).exists();

    Path templateRoot = CobigenTemplatesPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);

    // check if context configuration exists
    assertThat(templateRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)).exists();

    Path template = templateRoot.resolve("crud_java_server_app");
    Path templateComplex = templateRoot.resolve("crud_java_server_app_complex");
    // check if templates exists
    assertThat(template).exists();
    assertThat(templateComplex).exists();
    // check if template.xml exists
    assertThat(template.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME)).exists();
    assertThat(templateComplex.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME)).exists();
  }
}
