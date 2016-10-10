package com.capgemini.senchaplugin.integrationtest;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.capgemini.cobigen.senchaplugin.SenchaPluginActivator;
import com.capgemini.senchaplugin.integrationtest.testdata.ModelCreationTest;

import junit.framework.AssertionFailedError;

/**
 *
 * @author rudiazma (Sep 13, 2016)
 */
public class JSSenchaIntegrationTest {

    /**
     * Test configuration to CobiGen
     */
    private File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /**
     * Temporary folder interface
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Common test setup
     * @author rudiazma (Sep 13, 2016)
     */
    @Before
    public void setup() {
        PluginRegistry.loadPlugin(SenchaPluginActivator.class);
    }

    /**
     * @author rudiazma (Sep 13, 2016)
     */
    @SuppressWarnings("javadoc")
    @Test
    public void testCorrectModelGeneration() throws Exception {

        PluginRegistry.loadPlugin(SenchaPluginActivator.class);
        PluginRegistry.loadPlugin(JavaPluginActivator.class);

        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");

        Object[] input = new Object[] { ModelCreationTest.class,
            JavaParserUtil.getFirstJavaClass(getClass().getClassLoader(), new FileReader(new File(
                "src/test/resources/testdata/integrationtest/javaSources/ModelCreationTest.java"))) };
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("testModel.js")) {
                cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile = new File(
                    tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + "testModel.js");
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals(,
                    FileUtils.readFileToString(expectedFile));
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

}
