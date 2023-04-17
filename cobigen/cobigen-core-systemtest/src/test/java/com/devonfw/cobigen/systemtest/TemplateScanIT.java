package com.devonfw.cobigen.systemtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.devonfw.cobigen.systemtest.util.PluginMockFactory;

/**
 * Test suite for template-scan related system tests
 */
public class TemplateScanIT extends AbstractApiTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = apiTestsRootPath + "TemplateScanTest/";

  /**
   * Root path to all template-set resources used in this test case
   */
  private static String testFileRootPathTemplateSets = apiTestsRootPath + "TemplateScanTemplateSetTest/";

  /**
   * Tests the correct destination resolution for resources obtained by template-scans
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectDestinationResolution() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates,
        "prefix_${variables.component#cap_first#replace('1','ONE')}.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + File.separator + "src" + File.separator + "main"
        + File.separator + "java" + File.separator + "TestCOMP1" + File.separator + "CompONE.java")).exists();
  }

  /**
   * Test template scan within an archive file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testScanTemplatesFromArchivFile() throws Exception {

    // pre-processing: mocking
    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    // test processing
    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI(), true);
    List<TemplateTo> templates = cobigen.getMatchingTemplates(input);

    // checking
    assertThat(templates, notNullValue());
    assertThat(templates.size(), equalTo(6));
  }

  /**
   * Tests the correct destination resolution for resources obtained by template-scans in the case of an empty path
   * element
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectDestinationResolution_emptyPathElement() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates, "prefix_Test.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + File.separator + "src" + File.separator + "main"
        + File.separator + "java" + File.separator + "base" + File.separator + "Test.java")).exists();
  }

  /**
   * Tests the correct destination resolution for resources obtained by template-scans in the case of multiple empty
   * path elements
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectDestinationResolution_emptyPathElements() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI(), true);
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates, "prefix_MultiEmpty.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    GenerationReportTo report = target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);
    assertThat(report).isSuccessful();

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + File.separator + "src" + File.separator + "main"
        + File.separator + "java" + File.separator + "base" + File.separator + "MultiEmpty.java")).exists();
  }

  /**
   * Tests if a template-set with a ts_scan node can be read and generated successfully
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetCorrectDestinationResolution() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPathTemplateSets + "template-sets").toURI());

    Object input = cobigen.read(
        new File("src/test/java/com/devonfw/cobigen/systemtest/testobjects/io/generator/logic/api/to/InputEto.java")
            .toPath(),
        Charset.forName("UTF-8"), getClass().getClassLoader());

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");

    List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates, "generated.txt");
    assertThat(targetTemplate).isNotNull();

    // Execution
    GenerationReportTo report = cobigen.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);

    // Validation
    assertThat(report).isSuccessful();
  }

}
