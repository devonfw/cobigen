package com.devonfw.cobigen.systemtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for extract templates scenarios.
 */
public class TemplateProcessingTest extends AbstractApiTest {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests if template sets can be adapted properly
   *
   * @throws IOException if an Exception occurs
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplateSetsTest() throws IOException, Exception {

    Path cobiGenHomeTemplateSets = this.tempFolder.newFolder("playground", "templateSetsHome").toPath();
    Path downloadedTemplateSetsPath = cobiGenHomeTemplateSets.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    if (!Files.exists(downloadedTemplateSetsPath)) {
      Files.createDirectories(downloadedTemplateSetsPath);
    }
    String testFileRootPath = "src/test/resources/testdata/systemtest/TemplateProcessingTest/template-sets/downloaded";
    FileUtils.copyDirectory(new File(testFileRootPath), downloadedTemplateSetsPath.toFile());

    // Prepare the test directories
    Path cobigenTemplateSetsFolderPath = cobiGenHomeTemplateSets.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path downloadedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path adaptedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    // Adapt the templates
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(cobigenTemplateSetsFolderPath);

    Exception exception = assertThrows(TemplateSelectionForAdaptionException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    List<Path> templateSetJars = ((TemplateSelectionForAdaptionException) exception).getTemplateSets();
    templateAdapter.adaptTemplateSets(templateSetJars, adaptedTemplateSetsFolderPath, false);

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
    assertThat(templateSetSimple.resolve(templateSetResourcesPath)
        .resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    assertThat(templateSetComplex.resolve(templateSetResourcesPathComplex)
        .resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();

    // check if template set utility resource folder exists
    assertThat(templateSetSimple.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER)).exists();
    assertThat(templateSetComplex.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER)).exists();

    // validate maven specific contents
    assertThat(templateSetSimple.resolve("pom.xml")).exists();
    assertThat(templateSetComplex.resolve("pom.xml")).exists();

    // check if META-INF was deleted
    assertThat(templateSetResourcesPath.resolve("META-INF")).doesNotExist();
    assertThat(templateSetResourcesPathComplex.resolve("META-INF")).doesNotExist();
  }

  /**
   * Test of adapt templates with old CobiGen_Templates project existing
   *
   * TODO: Check if this test is really validating a complete adapt of a monolithic template, see:
   * https://github.com/devonfw/cobigen/issues/1681
   *
   * @throws IOException if an Exception occurs
   */
  @Test
  public void adaptTemplatesWithOldConfiguration() throws IOException {

    String testFileRootPathMonolithicTemplates = apiTestsRootPath + "AdaptMonolithicTemplatesTest/";
    Path cobiGenHomeMonolithicTemplates = this.tempFolder.newFolder("playground", "templatesMonolithicHome").toPath();

    FileUtils.copyDirectory(new File(testFileRootPathMonolithicTemplates), cobiGenHomeMonolithicTemplates.toFile());

    Path cobigenTemplatesParent = cobiGenHomeMonolithicTemplates
        .resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);

    Path cobigenTemplatesProject = cobigenTemplatesParent.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(cobigenTemplatesParent);
    assertThrows(UpgradeTemplatesNotificationException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    assertThat(cobigenTemplatesProject).exists().isDirectory();
    assertThat(cobigenTemplatesProject.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)).exists().isDirectory();
    assertThat(cobigenTemplatesProject.resolve("src/main/java")).exists().isDirectory();
  }
}
