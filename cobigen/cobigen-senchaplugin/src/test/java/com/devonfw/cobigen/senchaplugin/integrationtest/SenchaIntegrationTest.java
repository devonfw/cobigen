package com.capgemini.cobigen.senchaplugin.integrationtest;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.senchaplugin.integrationtest.testdata.ModelCreationTest;

import junit.framework.AssertionFailedError;

@SuppressWarnings("javadoc")
public class SenchaIntegrationTest {

    /**
     * Test configuration to CobiGen
     */
    private File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /**
     * Temporary folder interface
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testCorrectModelGeneration() throws Exception {

        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object[] input = new Object[] { ModelCreationTest.class,
            cobiGen.read("java",
                Paths.get("src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java"),
                Charsets.UTF_8) };
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("testModel.js")) {
                cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "testModel.js");
                Assert.assertTrue(expectedFile.exists());
                methodTemplateFound = true;
                break;
            }
        }
        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

}
