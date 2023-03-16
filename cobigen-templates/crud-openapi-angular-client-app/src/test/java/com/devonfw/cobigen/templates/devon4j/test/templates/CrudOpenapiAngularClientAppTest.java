package com.devonfw.cobigen.templates.devon4j.test.templates;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.maven.test.AbstractMavenTest;
import com.devonfw.cobigen.templates.devon4j.config.constant.MavenMetadata;

/**
 * Smoke tests of all templates.
 */
public class CrudOpenapiAngularClientAppTest extends AbstractMavenTest {

  /** Root of all test resources of this test suite */
  public static final String TEST_RESOURCES_ROOT = "src/test/resources/testdata/templatetest/";

  /** Temporary files rule to create temporary folders */
  @ClassRule
  public static TemporaryFolder tempFolder = new TemporaryFolder();

  /** The templates development folder */
  protected static Path templatesProject;

  /** The templates development folder */
  protected static Path templatesProjectTemporary;

  /**
   * Creates a copy of the templates project in the temp directory
   *
   * @throws URISyntaxException if the path could not be created properly
   * @throws IOException if accessing a directory or file fails
   */
  @BeforeClass
  public static void setupDevTemplates() throws URISyntaxException, IOException {

    templatesProject = new File(
        CrudOpenapiAngularClientAppTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
            .getParentFile().getParentFile().toPath();

    Path utilsPom = new File(
        CrudOpenapiAngularClientAppTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
            .getParentFile().getParentFile().toPath().resolve("src/test/resources/utils/pom.xml");

    // create a temporary directory cobigen-templates/template-sets/adapted containing the template sets
    Path tempFolderPath = tempFolder.getRoot().toPath();
    Path cobigenTemplatePath = tempFolderPath.resolve("cobigen-templates");
    if (!Files.exists(cobigenTemplatePath)) {
      Files.createDirectory(cobigenTemplatePath);

      templatesProjectTemporary = cobigenTemplatePath.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Path templateSetsAdaptedFolder = templatesProjectTemporary.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Files.createDirectory(templatesProjectTemporary);
      Files.createDirectory(templateSetsAdaptedFolder);

      Path templateSetPath = templateSetsAdaptedFolder.resolve("template-set");
      FileUtils.copyDirectory(templatesProject.toFile(), templateSetPath.toFile());

      if (Files.isDirectory(templateSetPath)) {

        // Replace the pom.xml in the template sets. Needed so that the project in the temp directory is build
        // properly
        if (Files.exists(templateSetPath.resolve("pom.xml"))) {
          try {
            Files.delete(templateSetPath.resolve("pom.xml"));
          } catch (IOException e) {
            throw new IOException("Error deleting file " + templateSetPath.resolve("pom.xml"), e);
          }
          try {
            Files.copy(utilsPom, templateSetPath.resolve("pom.xml"));
          } catch (IOException e) {
            throw new IOException("Error copying file " + utilsPom, e);
          }
        }
      }
    }
  }

  /**
   * Test successful generation of all templates based on an ETO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_OpenApiInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesOpenApiInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
  }

}
