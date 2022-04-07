package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
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
   * temporary project to store CobiGen home
   */
  File cobiGenHome;

  /**
   * Creates a temporary CobiGen home directory for each test
   *
   * @throws IOException if an Exception occurs
   */
  @Before
  public void prepare() throws IOException {

    this.cobiGenHome = this.tempFolder.newFolder("playground", "templatesHome");
  }

  /**
   * @throws IOException if an Exception occurs
   */
  public void extractTemplateSetsTest() throws IOException {

    FileUtils.copyDirectory(new File(testFileRootPath + "templates"),
        this.cobiGenHome.toPath().resolve("template-sets/downloaded").toFile());
    CobiGenFactory.extractTemplates();
    Path adaptedFolder = this.cobiGenHome.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_PATH)
        .resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Path extractedJar1 = adaptedFolder.resolve("template-test1-0.0.1");
    Path extractedJar2 = adaptedFolder.resolve("template-test2-0.0.1");
    assertThat(Files.exists(extractedJar1));
    assertThat(Files.exists(extractedJar2));
  }

  /**
   * @throws IOException if an Exception occurs
   */
  public void extractTemplatesWithOldConfiguration() throws IOException {

    Path cobigenTemplatesProject = this.cobiGenHome.toPath()
        .resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    Files.createDirectories(cobigenTemplatesProject);
    CobiGenFactory.extractTemplates();
    assertThat(Files.exists(cobigenTemplatesProject));
  }

  /**
   * Test of extract templates with old CobiGen_Templates project existing with custom COBIGEN_HOME environment variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testExtractTemplatesWithOldConfiguration() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toPath().toString())
        .execute(() -> extractTemplatesWithOldConfiguration());
  }

  /**
   * Test of extract template sets with custom COBIGEN_HOME environment variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testExtractTemplateSets() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toPath().toString())
        .execute(() -> extractTemplateSetsTest());
  }

  /**
   * @throws IOException if an Exception occurs
   */
  public void findTemplateSetJarsWithBackwardsCompatibilityTest() throws IOException {

    Path downloadedFolder = this.cobiGenHome.toPath().resolve("template-sets").resolve("downloaded");
    Files.createDirectories(downloadedFolder);
    URI templatesLocationURI = ConfigurationFinder.findTemplatesLocation();
    assertThat(templatesLocationURI.compareTo(this.cobiGenHome.toPath().resolve("template-sets").toUri()));

  }

  /**
   * Test of find template set downloaded folder to ensure backwards compatibility with custom COBIGEN_HOME environment
   * variable
   *
   * @throws Exception test fails
   */
  @Test
  public void testfindTemplateSetDownloadedWithBackwardsCompatibility() throws Exception {

    withEnvironmentVariable("COBIGEN_HOME", this.cobiGenHome.toPath().toString())
        .execute(() -> findTemplateSetJarsWithBackwardsCompatibilityTest());
  }

}
