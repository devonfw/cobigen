package com.devonfw.cobigen.systemtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
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

  // TODO jars has to be updated
  /**
   * Root path to all resources used in tests that test the structure of the template sets.
   */
  private static String testFileRootPathTemplateSets = apiTestsRootPath + "AdaptTemplateSetsTest/";

  /**
   * Root path to all resources used in tests that test the old monolithic template structure.
   */
  private static String testFileRootPathMonolithicTemplates = apiTestsRootPath + "AdaptMonolithicTemplatesTest/";

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * temporary project to store CobiGen home for a project with the new template structure consisting of template sets.
   */
  Path cobiGenHomeTemplateSets;

  /**
   * temporary project to store CobiGen home for a project with the old template structure consisting of a monolitihic
   * template set
   */
  Path cobiGenHomeMonolithicTemplates;

  /**
   * Creates a temporary CobiGen home directory for each test. A separate directory to test the old and new structure.
   *
   * @throws IOException if an Exception occurs
   */
  @Before
  public void prepare() throws IOException {

    this.cobiGenHomeTemplateSets = this.tempFolder.newFolder("playground", "templateSetsHome").toPath();
    this.cobiGenHomeMonolithicTemplates = this.tempFolder.newFolder("playground", "templatesMonolithicHome").toPath();
  }

  /**
   * Tests if template sets can be extracted properly
   *
   * @throws IOException if an Exception occurs
   */
  @Test
  public void extractTemplateSetsTest() throws IOException {

    FileUtils.copyDirectory(new File(testFileRootPathTemplateSets), this.cobiGenHomeTemplateSets.toFile());

    Path templateSetsFolder = this.cobiGenHomeTemplateSets.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path adaptedFolder = templateSetsFolder.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(templateSetsFolder);

    Exception exception = assertThrows(TemplateSelectionForAdaptionException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    List<Path> templateSetJars = ((TemplateSelectionForAdaptionException) exception).getTemplateSets();
    templateAdapter.adaptTemplateSets(templateSetJars, adaptedFolder, false);
    // sources jar is needed or we remove the functionality
    Path extractedJar1 = adaptedFolder.resolve("template-test1-0.0.1")
        .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    Path extractedJar2 = adaptedFolder.resolve("template-test2-0.0.1")
        .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    assertThat(extractedJar1).exists().isDirectory();
    assertThat(extractedJar2).exists().isDirectory();
  }

  /**
   * Test of extract templates with old CobiGen_Templates project existing
   *
   * @throws IOException if an Exception occurs
   */
  @Test
  public void extractTemplatesWithOldConfiguration() throws IOException {

    FileUtils.copyDirectory(new File(testFileRootPathMonolithicTemplates),
        this.cobiGenHomeMonolithicTemplates.toFile());

    Path cobigenTemplatesParent = this.cobiGenHomeMonolithicTemplates
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
