package com.devonfw.cobigen.openapiplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.openapiplugin.model.EntityDef;
import com.devonfw.cobigen.openapiplugin.util.TestConstants;

import junit.framework.AssertionFailedError;

/**
 * Testing the integration of input reader and matcher as the matchers algorithm depends on the model created by the
 * input reader.
 */
public class InputReaderMatcherTest {

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/integrationtest/InputReaderMatcherTest";

  /** Temporary folder rule to create new temporary folder and files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Tests the correct basic retrieval of ComponentDef inputs
   *
   * @throws Exception test fails
   */
  @Test
  public void testBasicElementMatcher_oneComponent() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
    assertThat(openApiFile).isNotNull();

    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);
    assertThat(inputObjects).isNotNull().hasSize(1);
  }

  /**
   * Tests the correct basic retrieval of ComponentDef inputs
   *
   * @throws Exception test fails
   */
  @Test
  public void testBasicElementMatcher_twoComponents() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
    assertThat(openApiFile).isNotNull();

    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);
    assertThat(inputObjects).isNotNull().hasSize(2);
  }

  /**
   * Tests the correct basic retrieval of ComponentDef inputs
   *
   * @throws Exception test fails
   */
  @Test
  public void testBasicElementMatcher_twoComponents_matchRegex() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates-regex").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
    assertThat(openApiFile).isNotNull();

    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(openApiFile);
    assertThat(matchingTemplates).extracting(TemplateTo::getId).containsExactly("sales_template.txt",
        "table_template.txt");
  }

  /**
   * Tests the correct basic retrieval of ComponentDef inputs
   *
   * @throws Exception test fails
   */
  @Test
  public void testBasicElementMatcher_oneComponent_matchRegex() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates-regex").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
    assertThat(openApiFile).isNotNull();

    List<Object> resolveContainers = cobigen.resolveContainers(openApiFile);
    assertThat(resolveContainers).hasSize(1).first().isInstanceOf(EntityDef.class)
        .extracting(e -> ((EntityDef) e).getName()).containsExactly("Table");

    List<TemplateTo> matchingTemplates = resolveContainers.stream()
        .flatMap(e -> cobigen.getMatchingTemplates(e).stream()).collect(Collectors.toList());

    assertThat(matchingTemplates).extracting(TemplateTo::getId).containsExactly("table_template.txt");
  }

  /**
   * Tests variable assignment resolution of PROPERTY type at the example of the component version
   *
   * @throws Exception test fails
   */
  @Test
  public void testVariableAssignment_propertyName() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

    String templateName = "testVariableAssignment_propertyName.txt";
    TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

    File targetFolder = this.tmpFolder.newFolder();
    GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve("testVariableAssignment_propertyName.txt").toFile()).exists()
        .hasContent("Table");
  }

  /**
   * Tests variable assignment resolution of ROOTPACKAGE type, thus that the user can define the root package in the
   * "info" part of the OpenAPI file
   *
   * @throws Exception test fails
   */
  @Test
  public void testVariableAssignment_rootPackage() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "root-package.yaml"), TestConstants.UTF_8);

    // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
    // TestConstants.UTF_8);
    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

    String templateName = "testVariableAssignment_rootPackage.txt";
    TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

    File targetFolder = this.tmpFolder.newFolder();
    GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve("testVariableAssignment_rootPackage.txt").toFile()).exists()
        .hasContent("testingRootName");
  }

  /**
   * Tests variable assignment resolution of ROOTPACKAGE type, thus that the user can define the root package in the
   * "info" part of the OpenAPI file
   *
   * @throws Exception test fails
   */
  @Test
  public void testVariableAssignment_rootComponent() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "root-package.yaml"), TestConstants.UTF_8);

    // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
    // TestConstants.UTF_8);
    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

    String templateName = "testModel_rootComponentProperty.txt";
    TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

    File targetFolder = this.tmpFolder.newFolder();
    GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve(templateName).toFile()).exists().hasContent("tablemanagement");

  }

  /**
   * Tests variable assignment resolution of ATTRIBUTE type, thus that the user can define any custom variables inside
   * the schema of OpenAPI files. <br>
   * <br>
   * The input test file contains one attribute per entity. We are testing here that both attributes are correctly
   * generated
   *
   * @throws Exception test fails
   */
  @Test
  public void testVariableAssignment_attribute() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);

    // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
    // TestConstants.UTF_8);
    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

    String templateName = "testVariableAssignment_attribute.txt";
    TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

    File targetFolder = this.tmpFolder.newFolder();
    GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve("testVariableAssignment_attribute.txt").toFile()).exists()
        .hasContent("testingAttributeTableiChangeGlobalVariable");

    template = findTemplate(cobigen, inputObjects.get(1), templateName);
    targetFolder = this.tmpFolder.newFolder();
    report = cobigen.generate(inputObjects.get(1), template, targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve("testVariableAssignment_attribute.txt").toFile()).exists()
        .hasContent("testingAttributeSalesitIsGlobal");
  }

  /**
   * Tests the case when <b>no</b> ATTRIBUTE was found on the OpenAPI input file for one entity. Therefore an empty
   * string should be assigned.<br>
   * <br>
   * The input test file contains two entities, one has an attribute and the other one does not. We are testing here
   * that the first entity gets his attribute and the second entity gets an empty string
   *
   * @throws Exception test fails
   */
  @Test
  public void testVariableAssignment_noAttributeFound() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

    Object openApiFile = cobigen.read(Paths.get(testdataRoot, "two-components-no-attribute.yaml"), TestConstants.UTF_8);

    // Previous version: List<Object> inputObjects = cobigen.getInputObjects(openApiFile,
    // TestConstants.UTF_8);
    List<Object> inputObjects = cobigen.resolveContainers(openApiFile);

    String templateName = "testVariableAssignment_attribute.txt";
    TemplateTo template = findTemplate(cobigen, inputObjects.get(0), templateName);

    File targetFolder = this.tmpFolder.newFolder();
    GenerationReportTo report = cobigen.generate(inputObjects.get(0), template, targetFolder.toPath());
    assertThat(report).containsException(CobiGenRuntimeException.class);

    assertThat(targetFolder.toPath().resolve("testVariableAssignment_attribute.txt").toFile()).doesNotExist();
  }

  /**
   * Finds a template or throws an assertion error
   *
   * @param cobigen {@link CobiGen} instance
   * @param inputObject input object to match against
   * @param templateName the id of the template
   * @return the found template
   */
  private TemplateTo findTemplate(CobiGen cobigen, Object inputObject, String templateName) {

    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(inputObject);
    for (TemplateTo t : matchingTemplates) {
      if (t.getId().equals(templateName)) {
        return t;
      }
    }
    throw new AssertionFailedError("Could not find template with id " + templateName);
  }
}
