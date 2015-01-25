package com.capgemini.cobigen.javaplugin.integrationtest;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;

/**
 * This test suite includes all tests, which focus on the correct model creation including correct extraction
 * of Java inheritance, generic type resolving etc.
 * @author mbrunnli (22.01.2015)
 */
public class ModelCreationTest extends AbstractIntegrationTest {

    /**
     * Field for testing purposes
     */
    @SuppressWarnings("unused")
    private List<String> testField;

    /**
     * Tests the correct reading and writing of parametric types as found in the input sources.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.01.2015)
     */
    @Test
    public void testCorrectGenericTypeExtraction() throws Exception {
        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        Object[] input =
            new Object[] {
                this.getClass(),
                JavaParserUtil.getFirstJavaClass(getClass().getClassLoader(), new FileReader(new File(
                    "src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java"))) };
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("genericTypes.txt")) {
                cobiGen.generate(input, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "genericTypes.txt");
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("List<String> testField", FileUtils.readFileToString(expectedFile));
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

}
