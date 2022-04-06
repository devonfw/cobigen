package com.devonfw.cobigen.templates.devon4j.test.templates;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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
public class TemplatesGenerationTest extends AbstractMavenTest {

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
        TemplatesGenerationTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().toPath();

    Path utilsPom = new File(TemplatesGenerationTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
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

      FileUtils.copyDirectory(templatesProject.toFile(), templateSetsAdaptedFolder.toFile());

      try (Stream<Path> files = Files.list(templateSetsAdaptedFolder)) {
        files.forEach(path -> {
          if (Files.isDirectory(path)) {
            Path resourcesFolder = path.resolve("src/main/resources");
            Path templatesFolder = path.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
            if (Files.exists(resourcesFolder) && !Files.exists(templatesFolder)) {
              try {
                Files.move(resourcesFolder, templatesFolder);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }

          // Replace the pom.xml in the utils project. Needed so that the utils project in the temp directory is build
          // properly
          if (path.getFileName().toString().equals("templates-devon4j-utils")) {
            if (Files.exists(path.resolve("pom.xml"))) {
              try {
                Files.delete(path.resolve("pom.xml"));
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            try {
              Files.copy(utilsPom, path.resolve("pom.xml"));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
      }
    }
  }

  /**
   * Test successful generation of all templates based on an entity
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_EntityInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesEntityInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on an ETO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_EtoInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesEtoInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
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

  /**
   * Test successful generation of all templates based on a RestService
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_RestServiceInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesRestServiceInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on a TO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_ToInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesToInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on a
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_XML() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesXMLInput/");
    runMavenInvoker(testProject, templatesProjectTemporary.toFile(), MavenMetadata.LOCAL_REPO);
  }

}
