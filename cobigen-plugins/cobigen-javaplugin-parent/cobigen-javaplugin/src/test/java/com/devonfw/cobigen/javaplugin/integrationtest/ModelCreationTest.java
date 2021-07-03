package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

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

        Object input = cobiGen.read(
            new File("src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java").toPath(),
            Charset.forName("UTF-8"), getClass().getClassLoader());
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

        Object input = cobiGen.read(
            new File("src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java").toPath(),
            StandardCharsets.UTF_8, getClass().getClassLoader());
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("correctAnnotationValueExtraction.txt")) {
                GenerationReportTo report =
                    cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                assertThat(report).isSuccessful();
                Path expectedFile = tmpFolderCobiGen.toPath().resolve("correctAnnotationValueExtraction.txt");
                assertThat(expectedFile).exists();
                assertThat(expectedFile).hasContent("\"/foo/{id}/\"");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

}
