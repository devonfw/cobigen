package com.capgemini.cobigen.systemtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.CobiGenFactory;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
import com.capgemini.cobigen.systemtest.util.PluginMockFactory;

/**
 * Test suite for template-scan related system tests
 * @author mbrunnli (07.12.2014)
 */
public class TemplateScanTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "TemplateScanTest/";

    /**
     * Tests the correct destination resolution for resources obtained by template-scans
     * @throws Exception
     *             test fails
     * @author mbrunnli (07.12.2014)
     */
    @Test
    public void testCorrectDestinationResoution() throws Exception {
        Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        // File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath);
        CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(input);
        Assert.assertNotNull(templates);

        TemplateTo targetTemplate =
            getTemplateById(templates, "prefix_${variables.component#cap_first#replace('1','ONE')}.java");
        Assert.assertNotNull(targetTemplate);

        // Execution
        target.generate(input, targetTemplate, false);

        // Validation
        Assert.assertTrue(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
            + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java"
            + SystemUtils.FILE_SEPARATOR + "TestCOMP1" + SystemUtils.FILE_SEPARATOR + "CompONE.java")
                .exists());
    }

    /**
     *
     * @throws Exception
     *             test fails
     * @author mbrunnli (16.02.2015)
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
        assertThat(templates.size(), equalTo(7));
    }

    /**
     * Tests the correct destination resolution for resources obtained by template-scans in the case of an
     * empty path element
     * @throws Exception
     *             test fails
     * @author mbrunnli (20.12.2015)
     */
    @Test
    public void testCorrectDestinationResoution_emptyPathElement() throws Exception {
        Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        // File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath);
        CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(input);
        Assert.assertNotNull(templates);

        TemplateTo targetTemplate = getTemplateById(templates, "prefix_Test.java");
        Assert.assertNotNull(targetTemplate);

        // Execution
        target.generate(input, targetTemplate, false);

        // Validation
        Assert.assertTrue(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
            + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java"
            + SystemUtils.FILE_SEPARATOR + "base" + SystemUtils.FILE_SEPARATOR + "Test.java").exists());
    }

    /**
     * Tests the correct destination resolution for resources obtained by template-scans in the case of
     * multiple empty path elements
     * @throws Exception
     *             test fails
     * @author mbrunnli (20.12.2015)
     */
    @Test
    public void testCorrectDestinationResoution_emptyPathElements() throws Exception {
        Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        // File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath);
        CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(input);
        Assert.assertNotNull(templates);

        TemplateTo targetTemplate = getTemplateById(templates, "prefix_MultiEmpty.java");
        Assert.assertNotNull(targetTemplate);

        // Execution
        target.generate(input, targetTemplate, false);

        // Validation
        Assert.assertTrue(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "src"
            + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java"
            + SystemUtils.FILE_SEPARATOR + "base" + SystemUtils.FILE_SEPARATOR + "MultiEmpty.java").exists());
    }

}
