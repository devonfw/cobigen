package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for extract templates scenarios.
 */
public class TemplateProcessingTest extends AbstractApiTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = apiTestsRootPath + "TemplateProcessingTest/";

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * mock the pathObject to use the temporary folder instead of the user folder
   */
  private MockedStatic<CobiGenPaths> cobigenPaths;

  /**
   * temporary project to store CobiGen home
   */
  Path cobiGenHome;

  /**
   * Creates a temporary CobiGen home directory for each test and create static mock for CobiGenPaths object
   *
   * @throws IOException if an Exception occurs
   */
  @Before
  public void prepare() throws IOException {

    this.cobiGenHome = this.tempFolder.newFolder("playground", "templatesHome").toPath();

    this.cobigenPaths = Mockito.mockStatic(CobiGenPaths.class, Mockito.CALLS_REAL_METHODS);
    this.cobigenPaths.when(() -> CobiGenPaths.getCobiGenHomePath()).thenReturn(this.cobiGenHome);

  }

  /**
   * cleanup mockito static mock
   */
  @After
  public void cleanup() {

    this.cobigenPaths.close();
  }

  /**
   * Tests if template sets can be extracted properly
   *
   * @throws IOException if an Exception occurs
   */
  private void extractTemplateSetsTest() throws IOException {

    FileUtils.copyDirectory(new File(testFileRootPath + "templates"),
        this.cobiGenHome.resolve("template-sets/downloaded").toFile());
    CobiGenFactory.extractTemplates();
    Path adaptedFolder = this.cobiGenHome.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_PATH)
        .resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Path extractedJar1 = adaptedFolder.resolve("template-test1-0.0.1");
    Path extractedJar2 = adaptedFolder.resolve("template-test2-0.0.1");
    assertThat(extractedJar1).exists().isDirectory();
    assertThat(extractedJar2).exists().isDirectory();
  }

  /**
   * Test of extract templates with old CobiGen_Templates project existing
   *
   * @throws IOException if an Exception occurs
   */
  private void extractTemplatesWithOldConfiguration() throws IOException {

    Path cobigenTemplatesParent = this.cobiGenHome.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);
    Files.createDirectories(cobigenTemplatesParent);
    Path cobigenTemplatesProject = cobigenTemplatesParent.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    Files.createDirectories(cobigenTemplatesProject);
    CobiGenFactory.extractTemplates();
    assertThat(cobigenTemplatesProject).exists().isDirectory();
  }

  /**
   * Test of extract templates with old CobiGen_Templates project existing with custom COBIGEN_HOME environment variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testExtractTemplatesWithOldConfiguration() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toString())
        .execute(() -> extractTemplatesWithOldConfiguration());
  }

  /**
   * Test of extract template sets with custom COBIGEN_HOME environment variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testExtractTemplateSets() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toString()).execute(() -> extractTemplateSetsTest());
  }

  /**
   * Test of find template set downloaded folder to ensure backwards compatibility
   *
   * @throws IOException if an Exception occurs
   */
  private void findTemplateSetJarsWithBackwardsCompatibilityTest() throws IOException {

    FileUtils.createParentDirectories(new File(testFileRootPath + "template-sets"));
    URI templatesLocationURI = ConfigurationFinder.findTemplatesLocation();
    assertThat(templatesLocationURI.compareTo(this.cobiGenHome.resolve("template-sets").toUri()));
  }

  /**
   * Test of find template set downloaded folder to ensure backwards compatibility with custom COBIGEN_HOME environment
   * variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testfindTemplateSetDownloadedWithBackwardsCompatibility() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toString())
        .execute(() -> findTemplateSetJarsWithBackwardsCompatibilityTest());
  }

}
