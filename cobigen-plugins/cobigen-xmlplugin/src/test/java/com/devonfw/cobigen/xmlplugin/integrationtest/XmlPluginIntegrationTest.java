package com.devonfw.cobigen.xmlplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.assertj.GenerationReportToAssert;
import com.devonfw.cobigen.api.exception.PluginNotAvailableException;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;

import junit.framework.AssertionFailedError;

/** Test suite for testing the xml plugin correctly integrated with cobigen-core. */
public class XmlPluginIntegrationTest {

  /** Test resources root */
  private static final String testFileRootPath = "src/test/resources/testdata/integrationtest/";

  /** Test configuration to CobiGen */
  private File cobigenConfigFolder = new File(testFileRootPath + "templates");

  /** Test input file */
  private File testinput = new File(testFileRootPath + "testInput.xml");

  /** UTF-8 Charset */
  private static final Charset UTF_8 = Charset.forName("UTF-8");

  /** Temporary folder interface */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Tests simple extraction of entities out of XMI UML.
   *
   * @throws Exception test fails
   */
  @Test
  public void testSimpleUmlEntityExtraction() throws Exception {

    // arrange
    Path configFolder = new File(testFileRootPath + "uml-classdiag").toPath();
    File xmlFile = configFolder.resolve("completeUmlXmi.xml").toFile();
    CobiGen cobigen = CobiGenFactory.create(configFolder.toUri(), true);
    Object doc = cobigen.read(xmlFile.toPath(), UTF_8);
    File targetFolder = this.tmpFolder.newFolder("testSimpleUmlEntityExtraction");

    // act
    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(doc);
    List<TemplateTo> templateOfInterest = matchingTemplates.stream().filter(e -> e.getId().equals("${className}.txt"))
        .collect(Collectors.toList());
    assertThat(templateOfInterest).hasSize(1);

    GenerationReportTo generate = cobigen.generate(doc, templateOfInterest, targetFolder.toPath());

    // assert
    assertThat(generate).isSuccessful();
    File[] files = targetFolder.listFiles();
    assertThat(files).extracting(e -> e.getName()).containsExactlyInAnyOrder("Student.txt", "User.txt", "Marks.txt",
        "Teacher.txt");

    assertThat(targetFolder.toPath().resolve("Student.txt"))
        .hasContent("public Student EAID_4509184A_D724_495f_AAEB_1ACE1AD90879");
    assertThat(targetFolder.toPath().resolve("User.txt"))
        .hasContent("public User EAID_C2E366C0_510F_4145_B650_110537B98360");
    assertThat(targetFolder.toPath().resolve("Marks.txt"))
        .hasContent("public Marks EAID_1D7DCE81_651D_40f2_A6E5_A522CF6E0C64");
    assertThat(targetFolder.toPath().resolve("Teacher.txt"))
        .hasContent("public Teacher EAID_6EA6FC61_FB9B_4e8e_98A1_30BD386AEA9A");
  }

  /**
   * Tests simple extraction of methods and attributes out of XMI UML.
   *
   * @throws Exception test fails
   */
  @Test
  public void testUmlMethodAttributeExtraction() throws Exception {

    // arrange
    Path configFolder = new File(testFileRootPath + "uml-classdiag").toPath();
    File xmlFile = configFolder.resolve("completeUmlXmi.xml").toFile();
    CobiGen cobigen = CobiGenFactory.create(configFolder.toUri(), true);
    Object doc = cobigen.read(xmlFile.toPath(), UTF_8);
    File targetFolder = this.tmpFolder.newFolder("testSimpleUmlEntityExtraction");

    // act
    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(doc);
    List<TemplateTo> templateOfInterest = matchingTemplates.stream()
        .filter(e -> e.getId().equals("${className}MethodsAttributes.txt")).collect(Collectors.toList());
    assertThat(templateOfInterest).hasSize(1);

    GenerationReportTo generate = cobigen.generate(doc, templateOfInterest, targetFolder.toPath());

    // assert
    assertThat(generate).isSuccessful();
    File[] files = targetFolder.listFiles();
    assertThat(files).extracting(e -> e.getName()).containsExactlyInAnyOrder("StudentMethodsAttributes.txt",
        "UserMethodsAttributes.txt", "MarksMethodsAttributes.txt", "TeacherMethodsAttributes.txt");

    assertThat(targetFolder.toPath().resolve("StudentMethodsAttributes.txt")).hasContent("public newOperation");
    assertThat(targetFolder.toPath().resolve("UserMethodsAttributes.txt")).hasContent("");
    assertThat(targetFolder.toPath().resolve("MarksMethodsAttributes.txt")).hasContent("private int attributeExample");
    assertThat(targetFolder.toPath().resolve("TeacherMethodsAttributes.txt")).hasContent("");
  }

  /**
   * Tests the generation of entities out of XMI UML. </br>
   * </br>
   * In marks class there is an attribute from which it has to generate getters and setters and also the associations
   * between marks and the rest of connected classes. </br>
   * </br>
   * The file nullMultiplicity contains a class called TestingNullMultiplicity which is connected to marks but without
   * multiplicity defined.
   *
   * @throws Exception test fails
   */
  @Test
  public void testUmlEntityExtraction() throws Exception {

    // arrange
    Path configFolder = new File(testFileRootPath + "uml-classdiag").toPath();
    File xmlFile = configFolder.resolve("nullMultiplicity.xml").toFile();
    CobiGen cobigen = CobiGenFactory.create(configFolder.toUri(), true);
    Object doc = cobigen.read(xmlFile.toPath(), UTF_8);
    File targetFolder = this.tmpFolder.newFolder("testSimpleUmlEntityExtraction");

    // act
    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(doc);
    List<TemplateTo> templateOfInterest = matchingTemplates.stream()
        .filter(e -> e.getId().equals("${className}Entity.txt")).collect(Collectors.toList());
    assertThat(templateOfInterest).hasSize(1);

    GenerationReportTo generate = cobigen.generate(doc, templateOfInterest, targetFolder.toPath());

    // assert
    assertThat(generate).isSuccessful();
    File[] files = targetFolder.listFiles();
    assertThat(files).extracting(e -> e.getName()).containsExactlyInAnyOrder("StudentEntity.txt", "UserEntity.txt",
        "MarksEntity.txt", "TeacherEntity.txt", "TestingNullMultiplicityEntity.txt");

    assertThat(targetFolder.toPath().resolve("MarksEntity.txt")).hasContent(
        "import java.util.List;\n" + "import javax.persistence.Column;\n" + "import javax.persistence.Entity;\n"
            + "import javax.persistence.Table;\n" + "@Entity\n" + "@Table(name=Marks)\n"
            + "public class MarksEntity extends ApplicationPersistenceEntity implements Marks {\n"
            + "private static final long serialVersionUID = 1L;\n" + "private int attributeExample;\n"
            + "// I want one\n" + "private Student student;\n" + "@Override\n" + "public Student getStudent(){\n"
            + "return this.student;\n" + "}\n" + "@Override\n" + "public void setStudent(Student student){\n"
            + "student = this.student;\n" + "}\n" + "@Override\n" + "public Integer getAttributeExample(){\n"
            + "return this.attributeExample;\n" + "}\n" + "public void setAttributeExample(Integer attributeExample){\n"
            + "this.attributeExample = attributeExample;\n" + "}\n" + "}");

    assertThat(targetFolder.toPath().resolve("TestingNullMultiplicityEntity.txt")).hasContent("import java.util.List;\n"
        + "import javax.persistence.Column;\n" + "import javax.persistence.Entity;\n"
        + "import javax.persistence.Table;\n" + "@Entity\n" + "@Table(name=TestingNullMultiplicity)\n"
        + "public class TestingNullMultiplicityEntity extends ApplicationPersistenceEntity implements TestingNullMultiplicity {\n"
        + "private static final long serialVersionUID = 1L;\n" + "}\n");
  }

  /**
   * Tests the xml reader integration for single attributes
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_SingleAttribute() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute", "xmlTestOutput_SingleAttribute.txt",
        "rootAttr1ContentrootAttr2ContentrootAttr3Content");
  }

  /**
   * Tests the xml reader integration for attribute list
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_AttributeList() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_AttributeList", "xmlTestOutput_AttributeList.txt",
        "rootAttr1rootAttr1ContentrootAttr2rootAttr2ContentrootAttr3rootAttr3Content");
  }

  /**
   * Tests the xml reader integration for text content
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_TextContent() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_TextContent", "xmlTestOutput_TextContent.txt",
        "rootTextContent1rootTextContent2");
  }

  /**
   * Tests the xml reader integration for text nodes
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_TextNodes() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_TextNodes", "xmlTestOutput_TextNodes.txt",
        "rootTextContent1 rootTextContent2 ");
  }

  /**
   * Tests the xml reader integration for text nodes
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_SingleChild() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_SingleChild", "xmlTestOutput_SingleChild.txt", "child1");
  }

  /**
   * Tests the xml reader integration for text nodes
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_ChildList() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_ChildList", "xmlTestOutput_ChildList.txt",
        "child1childdublicatechilddublicate");
  }

  /**
   * Tests the xml reader integration for text nodes
   *
   * @throws Exception test fails
   */
  @Test
  public void testXmlReaderIntegration_VariablesConstant() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant", "xmlTestOutput_VariablesConstant.txt",
        "testConstantValue");
  }

  /**
   * Regression test that the error message of cobigen-core has not been changed, which indicates a merge strategy to
   * not being found. This is necessary for the tests checking the already implemented merge strategies to exist.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyNotFoundErrorMessageRegression() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute", "xmlTestOutput_SingleAttribute.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute",
        "xmlTestOutput_SingleAttribute.txt", null);

    asserts.containsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_attachTexts to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_attachTexts() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_TextNodes", "xmlTestOutput_TextNodes.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_TextNodes",
        "xmlTestOutput_TextNodes.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);

  }

  /**
   * Tests the merge strategy xmlmerge_attachTexts_validate to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_attachTexts_validate() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_TextNodesValidate", "xmlTestOutput_TextNodes.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_TextNodesValidate",
        "xmlTestOutput_TextNodes.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_override_attachTexts to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_override_attachTexts() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_SingleChild", "xmlTestOutput_SingleChild.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_SingleChild",
        "xmlTestOutput_SingleChild.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_override_attachTexts_validate to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_override_attachTexts_validate() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_SingleChildValidate", "xmlTestOutput_SingleChild.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_SingleChildValidate",
        "xmlTestOutput_SingleChild.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_ChildList", "xmlTestOutput_ChildList.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_ChildList",
        "xmlTestOutput_ChildList.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_validate to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_validate() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_ChildListValidate", "xmlTestOutput_ChildList.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_ChildListValidate",
        "xmlTestOutput_ChildList.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_override to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_override() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant", "xmlTestOutput_VariablesConstant.txt", null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant",
        "xmlTestOutput_VariablesConstant.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Tests the merge strategy xmlmerge_override_validate to exist and being registered.
   *
   * @throws Exception test fails
   */
  @Test
  public void testMergeStrategyDefined_xmlmerge_override_validate() throws Exception {

    generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstantValidate", "xmlTestOutput_VariablesConstant.txt",
        null);
    GenerationReportToAssert asserts = generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstantValidate",
        "xmlTestOutput_VariablesConstant.txt", null);

    asserts.notContainsException(PluginNotAvailableException.class);
  }

  /**
   * Generates the template with the given templateId and reads the generated File with the outputFileName. It will be
   * asserted, that this file has the expectedFileContents passed as parameter.
   *
   * @param templateId Template to generate
   * @param outputFileName file name of the generated output File
   * @param expectedFileContents generated contents to be expected (asserted)
   * @return the resulting report
   * @throws Exception if anything fails.
   */
  private GenerationReportToAssert generateTemplateAndTestOutput(String templateId, String outputFileName,
      String expectedFileContents) throws Exception {

    CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI(), true);

    // wenn der temporäre Output Ordner breits existiert, dann wird dieser wiederverwendet.
    File tmpFolderCobiGen = new File(this.tmpFolder.getRoot().getAbsolutePath() + File.separator + "cobigen_output");
    if (!tmpFolderCobiGen.exists()) {
      tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
    }

    // read xml File as Document
    Object inputDocument = cobiGen.read(this.testinput.toPath(), Charset.forName("UTF-8"));

    // find matching templates and use test template for generation
    List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
    boolean templateFound = false;
    GenerationReportToAssert asserts = null;
    for (TemplateTo template : templates) {
      if (template.getId().equals(templateId)) {
        GenerationReportTo report = cobiGen.generate(inputDocument, template,
            Paths.get(tmpFolderCobiGen.getAbsolutePath()), false, (taskname, progress) -> {
            });

        asserts = assertThat(report);

        File expectedFile = new File(tmpFolderCobiGen.getAbsoluteFile() + File.separator + outputFileName);

        Assert.assertTrue(expectedFile.exists());
        // validate results if expected file contents are defined
        if (expectedFileContents != null) {
          Assert.assertEquals(expectedFileContents, FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8));
        }
        templateFound = true;
        break;
      }
    }

    if (!templateFound) {
      throw new AssertionFailedError("Test template not found");
    }
    return asserts;
  }
}
