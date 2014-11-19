package com.capgemini.cobigen.xmlplugin.integrationtest;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.xmlplugin.XmlPluginActivator;

/**
 * Test suite for testing the xml plugin correctly integrated with cobigen-core
 * @author fkreis (19.11.2014)
 */
public class XmlPluginIntegrationTest {

    /**
     * Test configuration to CobiGen
     */
    private File cobigenConfigFolder = new File(
        "src/test/resources/com/capgemini/cobigen/xmlplugin/integrationtest/templates");

    /**
     * Temporary folder interface
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Common test setup
     * @author fkreis (19.11.2014)
     */
    @Before
    public void setup() {
        PluginRegistry.loadPlugin(XmlPluginActivator.class);
    }

    /**
     * Tests the xml reader integration
     * @throws Exception
     *             test fails
     * @author fkreis (19.11.2014)
     */
    @Test
    public void testXmlReaderIntegration() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        // TODO can later be used
        // File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        File tmpFolderCobiGen = new File("src/test/resources/com/capgemini/cobigen/xmlplugin/ouput");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        File inputXmlFile =
            new File("src/test/resources/com/capgemini/cobigen/xmlplugin/integrationtest/testInput.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(inputXmlFile);

        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);

        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile = new File(tmpFolderCobiGen.getAbsoluteFile() + "\\xmlTestOutput.txt");
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("truetruetrue", FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            new AssertionFailedError("Test template not found");
        }
    }
}
