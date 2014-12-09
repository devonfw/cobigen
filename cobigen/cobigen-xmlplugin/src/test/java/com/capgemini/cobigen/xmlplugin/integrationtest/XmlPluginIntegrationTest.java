package com.capgemini.cobigen.xmlplugin.integrationtest;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
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
    private File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /**
     * Test input file
     */
    private File testinput = new File("src/test/resources/testdata/integrationtest/testInput.xml");

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
     * Tests the xml reader integration for single attributes
     * @throws Exception
     *             test fails
     * @author fkreis (19.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_SingleAttribute() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_SingleAttribute")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_SingleAttribute.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("rootAttr1ContentrootAttr2ContentrootAttr3Content",
                    FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for attribute list
     * @throws Exception
     *             test fails
     * @author fkreis (25.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_AttributeList() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_AttributeList")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_AttributeList.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals(
                    "rootAttr1rootAttr1ContentrootAttr2rootAttr2ContentrootAttr3rootAttr3Content",
                    FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for text content
     * @throws Exception
     *             test fails
     * @author fkreis (25.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_TextContent() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_TextContent")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_TextContent.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("rootTextContent1rootTextContent2",
                    FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_TextNodes() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_TextNodes")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_TextNodes.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("rootTextContent1 rootTextContent2 ",
                    FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_SingleChild() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_SingleChild")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_SingleChild.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("child1", FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_ChildList() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_ChildList")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_ChildList.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("child1childdublicatechilddublicate",
                    FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_VariablesConstant() throws Exception {

        CobiGen cobiGen = new CobiGen(cobigenConfigFolder);
        File tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        cobiGen
            .setContextSetting(ContextSetting.GenerationTargetRootPath, tmpFolderCobiGen.getAbsolutePath());

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use testtemplate for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals("xmlTestTemplate_VariablesConstant")) {
                cobiGen.generate(inputDocument, template, false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR
                        + "xmlTestOutput_VariablesConstant.txt");

                // validate results
                Assert.assertTrue(expectedFile.exists());
                Assert.assertEquals("testConstantValue", FileUtils.readFileToString(expectedFile));
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }
}
