package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
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
 * Test suit for annotation retrieval from source code to be exposed in the generation model.
 */
public class AnnotationQueryingTest extends AbstractIntegrationTest {

    /**
     * Tests whether annotations with object array values are correctly accessible within the templates
     * @throws Exception
     *             test fails
     */
    @Test
    public void testAnnotationWithObjectArraysAsValues() throws Exception {
        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object input = cobiGen.read(new File(
            "src/test/resources/testdata/unittest/inputreader/TestClassWithAnnotationsContainingObjectArrays.java")
                .toPath(),
            Charset.forName("UTF-8"), getClass().getClassLoader());
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("annotationQuerying.txt")) {
                GenerationReportTo report =
                    cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile = new File(
                    tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "annotationQuerying.txt");
                assertThat(report).isSuccessful();
                assertThat(expectedFile).exists();
                assertThat(expectedFile)
                    .hasContent("TestClassWithAnnotationsContainingObjectArrays.class,TestClassWithAnnotations.class,");
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }
}
