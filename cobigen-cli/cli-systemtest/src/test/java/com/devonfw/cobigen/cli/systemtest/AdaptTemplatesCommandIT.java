package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateState;

/**
 * Tests the usage of the adapt-templates command.
 */
public class AdaptTemplatesCommandIT extends AbstractCliTest {

  /** Test resources root path */
  private static String testFileRootPath = "src/test/resources/testdata/AdaptTemplatesCommandIT/template-sets/downloaded";

  /**
   * Checks if adapt-templates command successfully created adapted folder and its sub folders
   *
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplateSetTest() throws Exception {

    Path downloadedTemplateSetsPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    if (!Files.exists(downloadedTemplateSetsPath)) {
      Files.createDirectories(downloadedTemplateSetsPath);
    }
    FileUtils.copyDirectory(new File(testFileRootPath), downloadedTemplateSetsPath.toFile());
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

    Path templateSetSimple = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-1.0.0");
    Path templateSetComplex = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-complex-1.0.0");

    // check if adapted template set exists
    assertThat(templateSetSimple).exists();
    assertThat(templateSetComplex).exists();

    Path templateSetResourcesPath = templateSetSimple
        .resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER);
    Path templateSetResourcesPathComplex = templateSetComplex
        .resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER);

    // check if templates folder exists
    assertThat(templateSetSimple.resolve(templateSetResourcesPath).resolve(ConfigurationConstants.TEMPLATES_FOLDER))
        .exists();
    assertThat(
        templateSetComplex.resolve(templateSetResourcesPathComplex).resolve(ConfigurationConstants.TEMPLATES_FOLDER))
            .exists();

    // check if template-set.xml exists
    assertThat(templateSetSimple.resolve(templateSetResourcesPath)
        .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
    assertThat(templateSetComplex.resolve(templateSetResourcesPathComplex)
        .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();

    // validate correct folder structure
    assertThat(templateSetSimple.resolve(templateSetResourcesPath).resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    assertThat(
        templateSetComplex.resolve(templateSetResourcesPathComplex).resolve(ConfigurationConstants.TEMPLATES_FOLDER)
            .resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();

    // check if template set utility resource folder exists
    assertThat(templateSetSimple.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER)).exists();
    assertThat(templateSetComplex.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER)).exists();

    // validate maven specific contents
    assertThat(templateSetSimple.resolve("pom.xml")).exists();
    assertThat(templateSetComplex.resolve("pom.xml")).exists();

  }

  /**
   * Checks if adapt-templates command successfully created cobigen templates folder and its sub folders
   *
   * @throws Exception test fails
   */

  @Test
  public void adaptTemplatesTest() throws Exception {

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
