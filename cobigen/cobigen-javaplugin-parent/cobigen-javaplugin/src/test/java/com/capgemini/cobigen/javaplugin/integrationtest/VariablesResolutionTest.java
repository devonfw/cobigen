package com.capgemini.cobigen.javaplugin.integrationtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.thoughtworks.qdox.model.JavaClass;

import junit.framework.AssertionFailedError;

/**
 * Test suite for variable resolution.
 */
public class VariablesResolutionTest extends AbstractIntegrationTest {

    /**
     * Tests that the path resolution is performed successfully in case of including path variables derived
     * from variable assignments retrieved by regex groups, which have been resolved to null. This bug has
     * been introduced by changing the model building from DOM to Bean model. The latter required to
     * explicitly not to set <code>null</code> as a value for variable resolution. Basically, this is odd, but
     * we have to comply with backward compatibility and the issue that we cannot encode unary-operators like
     * ?? in a file path sufficiently.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testSuccessfulPathResolution_variableEqNull() throws Exception {
        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        JavaClass input = JavaParserUtil.getFirstJavaClass(
            new FileReader(new File("src/test/resources/testdata/integrationtest/javaSources/SampleEntity.java")));
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("${variables.entityName}.java")) {
                GenerationReportTo report =
                    cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                assertThat(report).isSuccessful();
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }
}
