package com.capgemini.cobigen.xmlplugin.integrationtest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;

import junit.framework.AssertionFailedError;

/**
 * Test suite for testing the xml plugin correctly integrated with cobigen-core.
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
     * Tests the xml reader integration for single attributes
     * @throws Exception
     *             test fails
     */
    @Test
    public void testXmlReaderIntegration_SingleAttribute() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute", "xmlTestOutput_SingleAttribute.txt",
            "rootAttr1ContentrootAttr2ContentrootAttr3Content");
    }

    /**
     * Tests the xml reader integration for attribute list
     * @throws Exception
     *             test fails
     * @author fkreis (25.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_AttributeList() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_AttributeList", "xmlTestOutput_AttributeList.txt",
            "rootAttr1rootAttr1ContentrootAttr2rootAttr2ContentrootAttr3rootAttr3Content");
    }

    /**
     * Tests the xml reader integration for text content
     * @throws Exception
     *             test fails
     * @author fkreis (25.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_TextContent() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_TextContent", "xmlTestOutput_TextContent.txt",
            "rootTextContent1rootTextContent2");
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_TextNodes() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_TextNodes", "xmlTestOutput_TextNodes.txt",
            "rootTextContent1 rootTextContent2 ");
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_SingleChild() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_SingleChild", "xmlTestOutput_SingleChild.txt", "child1");
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_ChildList() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_ChildList", "xmlTestOutput_ChildList.txt",
            "child1childdublicatechilddublicate");
    }

    /**
     * Tests the xml reader integration for text nodes
     * @throws Exception
     *             test fails
     * @author fkreis (26.11.2014)
     */
    @Test
    public void testXmlReaderIntegration_VariablesConstant() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant", "xmlTestOutput_VariablesConstant.txt",
            "testConstantValue");
    }

    /**
     * Regression test that the error message of cobigen-core has not be changed, which indicates a merge
     * strategy to not being found. This is necessary for the tests checking the already implemented merge
     * strategies to exist.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testMergeStrategyNotFoundErrorMessageRegression() throws Exception {
        generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute", "xmlTestOutput_SingleAttribute.txt", null);
        try {
            generateTemplateAndTestOutput("xmlTestTemplate_SingleAttribute", "xmlTestOutput_SingleAttribute.txt", null);
        } catch (InvalidConfigurationException e) {
            assertThat(e.getMessage(), containsString("No merger for merge strategy"));
        }
    }

    /**
     * Tests the merge strategy xmlmerge_attachTexts to exist and being registered.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jan 10, 2016)
     */
    @Test
    public void testMergeStrategyDefined_xmlmerge_attachTexts() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_TextNodes", "xmlTestOutput_TextNodes.txt", null);
        try {
            generateTemplateAndTestOutput("xmlTestTemplate_TextNodes", "xmlTestOutput_TextNodes.txt", null);
        } catch (MergeException e) {
            assertThat(e.getMessage(), not(containsString("No merger for merge strategy")));
        }
    }

    /**
     * Tests the merge strategy xmlmerge_override_attachTexts to exist and being registered.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jan 10, 2016)
     */
    @Test
    public void testMergeStrategyDefined_xmlmerge_override_attachTexts() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_SingleChild", "xmlTestOutput_SingleChild.txt", null);
        try {
            generateTemplateAndTestOutput("xmlTestTemplate_SingleChild", "xmlTestOutput_SingleChild.txt", null);
        } catch (MergeException e) {
            assertThat(e.getMessage(), not(containsString("No merger for merge strategy")));
        }
    }

    /**
     * Tests the merge strategy xmlmerge to exist and being registered.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jan 10, 2016)
     */
    @Test
    public void testMergeStrategyDefined_xmlmerge() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_ChildList", "xmlTestOutput_ChildList.txt", null);
        try {
            generateTemplateAndTestOutput("xmlTestTemplate_ChildList", "xmlTestOutput_ChildList.txt", null);
        } catch (MergeException e) {
            assertThat(e.getMessage(), not(containsString("No merger for merge strategy")));
        }
    }

    /**
     * Tests the merge strategy xmlmerge_override to exist and being registered.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jan 10, 2016)
     */
    @Test
    public void testMergeStrategyDefined_xmlmerge_override() throws Exception {

        generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant", "xmlTestOutput_VariablesConstant.txt", null);
        try {
            generateTemplateAndTestOutput("xmlTestTemplate_VariablesConstant", "xmlTestOutput_VariablesConstant.txt",
                null);
        } catch (MergeException e) {
            assertThat(e.getMessage(), not(containsString("No merger for merge strategy")));
        }
    }

    /**
     * Generates the template with the given templateId and reads the generated File with the outputFileName.
     * It will be asserted, that this file has the expectedFileContents passed as parameter.
     * @param templateId
     *            Template to generate
     * @param outputFileName
     *            file name of the generated output File
     * @param expectedFileContents
     *            generated contents to be expected (asserted)
     * @throws Exception
     *             if anything fails.
     */
    private void generateTemplateAndTestOutput(String templateId, String outputFileName, String expectedFileContents)
        throws Exception {
        CobiGen cobiGen = CobiGenFactory.create(cobigenConfigFolder.toURI());

        // wenn der temporäre Output Ordner breits existiert, dann wird dieser wiederverwendet.
        File tmpFolderCobiGen =
            new File(tmpFolder.getRoot().getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "cobigen_output");
        if (!tmpFolderCobiGen.exists()) {
            tmpFolderCobiGen = tmpFolder.newFolder("cobigen_output");
        }

        // read xml File as Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document inputDocument = dBuilder.parse(testinput);

        // find matching templates and use test template for generation
        List<TemplateTo> templates = cobiGen.getMatchingTemplates(inputDocument);
        boolean templateFound = false;
        for (TemplateTo template : templates) {
            if (template.getId().equals(templateId)) {
                cobiGen.generate(inputDocument, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()), false);
                File expectedFile =
                    new File(tmpFolderCobiGen.getAbsoluteFile() + SystemUtils.FILE_SEPARATOR + outputFileName);

                Assert.assertTrue(expectedFile.exists());
                // validate results if expected file contents are defined
                if (expectedFileContents != null) {
                    Assert.assertEquals(expectedFileContents, FileUtils.readFileToString(expectedFile));
                }
                templateFound = true;
                break;
            }
        }

        if (!templateFound) {
            throw new AssertionFailedError("Test template not found");
        }
    }
}
