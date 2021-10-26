package com.devonfw.cobigen.systemtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.devonfw.cobigen.systemtest.util.PluginMockFactory;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;

/**
 * Test suite for template-scan related system tests
 */
public class TemplateScanTest extends AbstractApiTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = apiTestsRootPath + "TemplateScanTest/";

  /**
   * Tests the correct destination resolution for resources obtained by template-scans
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectDestinationResoution() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates,
        "prefix_${variables.component#cap_first#replace('1','ONE')}.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
        + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java" + SystemUtils.FILE_SEPARATOR
        + "TestCOMP1" + SystemUtils.FILE_SEPARATOR + "CompONE.java")).exists();
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
    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI());
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
  public void testCorrectDestinationResoution_emptyPathElement() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates, "prefix_Test.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
        + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java" + SystemUtils.FILE_SEPARATOR
        + "base" + SystemUtils.FILE_SEPARATOR + "Test.java")).exists();
  }

  /**
   * Tests the correct destination resolution for resources obtained by template-scans in the case of multiple empty
   * path elements
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectDestinationResoution_emptyPathElements() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File generationRootFolder = this.tmpFolder.newFolder("generationRootFolder");
    // Useful to see generates if necessary, comment the generationRootFolder above then
    // File generationRootFolder = new File(testFileRootPath + "generates");

    // pre-processing
    File templatesFolder = new File(testFileRootPath);
    CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
    List<TemplateTo> templates = target.getMatchingTemplates(input);
    assertThat(templates).isNotNull();

    TemplateTo targetTemplate = getTemplateById(templates, "prefix_MultiEmpty.java");
    assertThat(targetTemplate).isNotNull();

    // Execution
    GenerationReportTo report = target.generate(input, targetTemplate, Paths.get(generationRootFolder.toURI()), false);
    assertThat(report).isSuccessful();

    // Validation
    assertThat(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
        + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java" + SystemUtils.FILE_SEPARATOR
        + "base" + SystemUtils.FILE_SEPARATOR + "MultiEmpty.java")).exists();
  }

}
