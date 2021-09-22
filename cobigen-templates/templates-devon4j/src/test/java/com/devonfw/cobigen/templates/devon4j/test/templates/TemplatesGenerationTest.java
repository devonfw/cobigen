package com.devonfw.cobigen.templates.devon4j.test.templates;

import java.io.File;

import org.junit.Test;

import com.devonfw.cobigen.maven.test.AbstractMavenTest;
import com.devonfw.cobigen.templates.devon4j.config.constant.MavenMetadata;

/**
 * Smoke tests of all templates.
 */
public class TemplatesGenerationTest extends AbstractMavenTest {

  /** Root of all test resources of this test suite */
  public static final String TEST_RESOURCES_ROOT = "src/test/resources/testdata/templatetest/";

  /**
   * Test successful generation of all templates based on an entity
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_EntityInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesEntityInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on an ETO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_EtoInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesEtoInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on an ETO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_OpenApiInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesOpenApiInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on a RestService
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_RestServiceInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesRestServiceInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on a TO
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_ToInput() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesToInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

  /**
   * Test successful generation of all templates based on a
   *
   * @throws Exception test fails
   */
  @Test
  public void testAllTemplatesGeneration_XML() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestAllTemplatesXMLInput/");
    runMavenInvoker(testProject, new File("").getAbsoluteFile(), MavenMetadata.LOCAL_REPO);
  }

}
