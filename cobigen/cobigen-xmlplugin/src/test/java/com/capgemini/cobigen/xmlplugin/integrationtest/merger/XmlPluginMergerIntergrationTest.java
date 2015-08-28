package com.capgemini.cobigen.xmlplugin.integrationtest.merger;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.xmlplugin.merger.delegates.XmlLawMergerDelegate;
import com.capgemini.cobigen.xmlplugin.unittest.merger.BasicXmlMergeTest;
import com.capgemini.xmllawmerger.ConflictHandlingType;

/**
 * Tests if the used XML merger behaves as desired. The test cases are adapted from {@link BasicXmlMergeTest}
 * @author sholzer (Aug 27, 2015)
 */
public class XmlPluginMergerIntergrationTest {

    /**
     * The merger under test
     */
    private IMerger merger;

    private final String charset = StandardCharsets.UTF_8.name();

    /**
     * the path to the used resources
     */
    private final String resourcesRoot = "src/test/resources/testdata/unittest/merger/";

    /**
     * Sets up a merger with patch priority
     * @author sholzer (Aug 27, 2015)
     */
    @Before
    public void setUp() {
        final String mergeSchemaLocation = "src/main/resources/mergeSchemas/";
        merger = new XmlLawMergerDelegate(mergeSchemaLocation, ConflictHandlingType.PATCHOVERWRITE);
    }

    /**
     * Merges two Spring web flow documents.
     * @see BasicXmlMergeTest#testMergeFlow_NonOverride()
     * @author sholzer (Aug 27, 2015)
     * @throws Exception
     *             hopefully never
     */
    @Test
    public void webFlowTest() throws Exception {
        String basePath = resourcesRoot + "BaseFile_flow.xml";
        String patchPath = resourcesRoot + "PatchFile_flow.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedString = merger.merge(baseFile, patchString, charset);

        Document mergeDoc = parseString(mergedString);

        Assert.assertEquals(1, mergeDoc.getElementsByTagName("flow").getLength());
        Assert.assertEquals(1, mergeDoc.getElementsByTagName("view-state").getLength());
        Assert.assertEquals(2, mergeDoc.getElementsByTagName("subflow-state").getLength());
        Assert.assertEquals(1, mergeDoc.getElementsByTagName("end-state").getLength());
        Assert.assertEquals(3, ((Element) mergeDoc.getElementsByTagName("view-state").item(0))
            .getElementsByTagName("transition").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("subflow-state").item(0))
            .getElementsByTagName("transition").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("subflow-state").item(1))
            .getElementsByTagName("transition").getLength());
    }

    // utils

    /**
     * Reads a file into a string
     * @param path
     *            to the file
     * @return String
     * @throws IOException
     *             when the file can't be read
     * @author sholzer (Aug 26, 2015)
     */
    public String readFile(String path, String charset) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, charset);
    }

    public org.w3c.dom.Document parseString(String string) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document mergeDoc = docBuilder.parse(new InputSource(new StringReader(string)));
        return mergeDoc;
    }
}
