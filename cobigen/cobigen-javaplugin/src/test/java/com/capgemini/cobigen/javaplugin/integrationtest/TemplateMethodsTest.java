package com.capgemini.cobigen.javaplugin.integrationtest;

import java.io.File;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;

/**
 * Test suite for testing the provided template methods correctly integrated with cobigen-core
 * @author mbrunnli (25.10.2014)
 */
public class TemplateMethodsTest {

    /**
     * Test configuration to CobiGen
     */
    private File cobigenConfigFolder = new File(
        "src/test/resources/com/capgemini/cobigen/javaplugin/integrationtest/templates");

    /**
     * Temporary folder interface
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Common test setup
     * @author mbrunnli (25.10.2014)
     */
    @Before
    public void setup() {
        PluginRegistry.loadPlugin(JavaPluginActivator.class);
    }

    /**
     * Tests the isAbstract template method integration
     * @throws Exception
     *             test fails
     * @author mbrunnli (25.10.2014)
     */
    @Test
    public void testIsAbstractMethod() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(this.getClass());

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("isAbstractTemplate")) {
                cobiGen.generate(getClass(), template, false);
                File expectedFile = new File(tmpFolderCobiGen.getAbsoluteFile() + "\\isAbstractOutput.txt");
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("falsetrue", FileUtils.readFileToString(expectedFile));
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the isSubtypeOf template method integration
     * @throws Exception
     *             test fails
     * @author mbrunnli (25.10.2014)
     */
    @Test
    public void testIsSubtypeOfMethod() throws Exception {

        File configFolder =
            new File("src/test/resources/com/capgemini/cobigen/javaplugin/integrationtest/templates");
        CobiGen cobiGen = new CobiGen(configFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(this.getClass());

        boolean methodTemplateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("isSubtypeOfTemplate")) {
                cobiGen.generate(getClass(), template, false);
                File expectedFile = new File(tmpFolderCobiGen.getAbsoluteFile() + "\\isSubtypeOfOutput.txt");
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("truetruefalse", FileUtils.readFileToString(expectedFile));
                methodTemplateFound = true;
                break;
            }
        }

        if (!methodTemplateFound) {
            new AssertionFailedError("Test template not found");
        }
    }
}
