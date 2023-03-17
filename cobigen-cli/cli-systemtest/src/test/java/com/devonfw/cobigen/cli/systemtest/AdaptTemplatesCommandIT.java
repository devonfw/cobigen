package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateState;

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
  @Ignore
  // TODO: re-enable when template set adaptation is implemented
  public void adaptTemplateSetTest() throws Exception {

    Path devTemplateSetPath = new File(
        AdaptTemplatesCommandIT.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("crud-java-server-app").resolve("target");
    File jars = devTemplateSetPath.toFile();
    List<String> filenames = new ArrayList<>(2);
    for (File file : jars.listFiles()) {
      if (file.getName().endsWith(".jar")) {
        filenames.add(file.getName());
      }
    }
    if (Files.exists(devTemplateSetPath)) {
      Path downloadedTemplateSetsPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
          .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
      if (!Files.exists(downloadedTemplateSetsPath)) {
        Files.createDirectories(downloadedTemplateSetsPath);
      }
      for (String jarFilename : filenames) {
        Files.copy(devTemplateSetPath.resolve(jarFilename),
            downloadedTemplateSetsPath.resolve(jarFilename.replace("-SNAPSHOT", "")));
      }
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
    Path templateSet = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-2021.12.007");
    Path templateSetSources = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-2021.12.007-sources");
    // check if template and sources exist
    assertThat(templateSet).exists();
    assertThat(templateSetSources).exists();
    // check if context configuration exists
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATES_FOLDER)).exists();
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
    assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
    // validate correct folder structure
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    // validate maven specific contents
    assertThat(templateSet.resolve("pom.xml")).exists();

  }

  /**
   * Checks if adapt-templates command successfully created cobigen templates folder and its sub folders
   *
   * @throws Exception test fails
   */

  @Test
  @Ignore
  public void adaptTemplatesTest() throws Exception {

    Path cliSystemTestPath = new File(
        AdaptTemplatesCommandIT.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().toPath();
    Path templatesPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
    Path CobigenTemplatesPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    if (!Files.exists(templatesPath)) {
      Files.createDirectories(templatesPath);
    }

    MavenCoordinateState nonSourcesJar = new MavenCoordinateState("com.devonfw.cobigen", "templates-devon4j", "3.0.0",
        false);
    MavenCoordinateState sourcesJar = new MavenCoordinateState("com.devonfw.cobigen", "templates-devon4j", "3.0.0",
        true);
    TemplatesJarUtil.downloadJar(nonSourcesJar, templatesPath.toFile());
    TemplatesJarUtil.downloadJar(sourcesJar, templatesPath.toFile());

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
