package com.capgemini.cobigen.javaplugin.integrationtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

import junit.framework.AssertionFailedError;

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
     */
    @Test
    public void testCorrectGenericTypeExtraction() throws Exception {
        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object[] input = new Object[] { this.getClass(),
            JavaParserUtil.getFirstJavaClass(getClass().getClassLoader(), new FileReader(
                new File("src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java"))) };
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("genericTypes.txt")) {
                GenerationReportTo report =
                    cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                assertThat(report).isSuccessful();
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "genericTypes.txt");
                assertThat(expectedFile).exists();
                assertThat(expectedFile).hasContent("List<String> testField");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests that annotation string values for methods are not quoted. See issue #251
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectAnnotationValueExtraction() throws Exception {
        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object[] input = new Object[] { this.getClass(),
            JavaParserUtil.getFirstJavaClass(getClass().getClassLoader(), new FileReader(
                new File("src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java"))) };
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("correctAnnotationValueExtraction.txt")) {
                GenerationReportTo report =
                    cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                assertThat(report).isSuccessful();
                File expectedFile = new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                    + "correctAnnotationValueExtraction.txt");
                assertThat(expectedFile).exists();
                assertThat(expectedFile).hasContent("/foo/{id}/");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

}
