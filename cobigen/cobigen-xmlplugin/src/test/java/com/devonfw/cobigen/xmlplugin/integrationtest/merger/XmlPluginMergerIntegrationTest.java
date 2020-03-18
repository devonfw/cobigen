package com.devonfw.cobigen.xmlplugin.integrationtest.merger;

import static org.junit.Assert.assertEquals;

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

import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.xmlplugin.merger.delegates.MergeType;
import com.devonfw.cobigen.xmlplugin.merger.delegates.XmlMergerDelegate;

/**
 * Tests if the used XML patchPreferingMerger behaves as desired.
 */
public class XmlPluginMergerIntegrationTest {

    /**
     * The merger under test, prefers patch values over base values
     */
    private Merger patchPreferingMerger;

    /**
     * The merger under test, prefers base values over patch values
     */
    private Merger basePreferingMerger;

    /**
     *
     */
    private final String charset = StandardCharsets.UTF_8.name();

    /**
     * the path to the used resources
     */
    private final String resourcesRoot = "src/test/resources/testdata/unittest/merger/";

    /**
     * Sets up a patchPreferingMerger and a basePreferingMerger without validation
     */
    @Before
    public void setUp() {
        final String mergeSchemaLocation = "src/main/resources/mergeSchemas/";
        patchPreferingMerger = new XmlMergerDelegate(mergeSchemaLocation, MergeType.PATCHATTACHOROVERWRITE);
        // ((XmlLawMergerDelegate) patchPreferingMerger).setValidation(false);
        basePreferingMerger = new XmlMergerDelegate(mergeSchemaLocation, MergeType.BASEATTACHOROVERWRITE);
        ((XmlMergerDelegate) basePreferingMerger).setValidation(false);
    }

    /**
     * @throws Exception
     *             test fails
     */
    @Test
    public void mergeDoesNotDestroySchemaLocation() throws Exception {
        String basePath = resourcesRoot + "BaseFile_namespaces.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(basePath, charset);
        String mergedString = basePreferingMerger.merge(baseFile, patchString, charset);
        Assert.assertTrue("Schema definition 'xsi:schemaLocation' has been destroyed.",
            mergedString.contains("xsi:schemaLocation"));

    }

    /**
     * Tests Issue #18 - https://github.com/oasp/cobigen/issues/18
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testMergeAlsoMergesSchemaLocations() throws Exception {

        String basePath = resourcesRoot + "BaseFile_namespaces.xml";
        String patchPath = resourcesRoot + "PatchFile_namespaces.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = basePreferingMerger.merge(baseFile, patchString, charset);

        Assert.assertTrue("Merged document does not contain schema locations defined in base.", mergedDoc.contains(
            "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"));
        Assert.assertTrue("Merged document does not contain schema locations defined in patch.", mergedDoc.contains(
            "http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd"));
    }

    /**
     * Tests whether merging of schemaLocations is not producing duplicates
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testNoDuplicateNamespacesMerged() throws Exception {

        String basePath = resourcesRoot + "BaseFile_namespaces.xml";
        String patchPath = resourcesRoot + "PatchFile_namespaces.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = basePreferingMerger.merge(baseFile, patchString, charset);

        Assert.assertFalse("Merge duplicates schema locations.", mergedDoc.contains(
            "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd "
                + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"));
    }

    /**
     * Merges two Spring web flow documents.
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
        String mergedString = basePreferingMerger.merge(baseFile, patchString, charset);

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

    /**
     * Merges two xhtml documents
     * @throws Exception
     *             test fails
     * @author sholzer (Aug 28, 2015)
     */
    @Test
    public void xhtmlTest() throws Exception {
        String basePath = resourcesRoot + "BaseFile_overview.xhtml";
        String patchPath = resourcesRoot + "PatchFile_overview.xhtml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedString = basePreferingMerger.merge(baseFile, patchString, charset);

        Document mergeDoc = parseString(mergedString);
        // changed by sholzer on 10.09.2015. The element in
        // question is now the second in the list (to be
        // honest I can't tell why but the document remains valid
        Assert.assertEquals(1, mergeDoc.getElementsByTagName("ui:composition").getLength());
        Assert.assertEquals(4, mergeDoc.getElementsByTagName("ui:define").getLength());
        Assert.assertEquals(1,
            ((Element) mergeDoc.getElementsByTagName("ui:define").item(1)).getElementsByTagName("title").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(0))
            .getElementsByTagName("ui:include").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(2))
            .getElementsByTagName("ui:include").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(3))
            .getElementsByTagName("ui:include").getLength());
    }

    /**
     * @throws Exception
     *             test fails
     * @author sholzer (Aug 28, 2015)
     */
    @Test
    public void queryTest() throws Exception {
        String basePath = resourcesRoot + "BaseFile_queries.xml";
        String patchPath = resourcesRoot + "PatchFile_queries.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedString = basePreferingMerger.merge(baseFile, patchString, charset);

        Document mergeDoc = parseString(mergedString);
        Assert.assertEquals(1, mergeDoc.getElementsByTagName("hibernate-mapping").getLength());
        Assert.assertEquals(5, mergeDoc.getElementsByTagName("query").getLength());
    }

    /**
     * @throws Exception
     *             test fails
     */
    @Test
    public void xhtmlTableTest() throws Exception {
        String basePath = resourcesRoot + "BaseFile_table.xhtml";
        String patchPath = resourcesRoot + "PatchFile_table.xhtml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedString = basePreferingMerger.merge(baseFile, patchString, charset);

        Document mergeDoc = parseString(mergedString);

        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("div").getLength());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("h:dataTable").getLength());
        Assert.assertEquals(7, ((Element) mergeDoc.getDocumentElement().getElementsByTagName("h:dataTable").item(0))
            .getElementsByTagName("h:column").getLength());
    }

    /**
     * Tests Issue https://github.com/devonfw/cobigen/issues/119
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testMergeDozerMapping() throws Exception {
        String basePath = resourcesRoot + "BaseFile_OneOneNine_dozer.xml";
        String patchPath = resourcesRoot + "PatchFile_OneOneNine_dozer.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = basePreferingMerger.merge(baseFile, patchString, charset);
        assertEquals("not the expected amount of mappings", 5, mergedDoc.split("<mapping").length - 1);

    }

    /**
     * Tests Issue https://github.com/devonfw/cobigen/issues/119
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testMergeJaxrsServiceBeans() throws Exception {
        String basePath = resourcesRoot + "BaseFile_OneOneNine.xml";
        String patchPath = resourcesRoot + "PatchFile_OneOneNine.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = basePreferingMerger.merge(baseFile, patchString, charset);

        assertEquals("To many jaxrs:server elements", mergedDoc.split("<jaxrs:server").length - 1, 1);

    }

    /**
     * Tests the correct merge behavior with Spring Beans. Test files are used from XmlLawMerger test
     * resources
     * @throws Exception
     *             should not happen
     */
    @Test
    public void testBeans() throws Exception {
        String basePath = resourcesRoot + "BaseFile_beans.xml";
        String patchPath = resourcesRoot + "PatchFile_beans.xml";

        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = basePreferingMerger.merge(baseFile, patchString, charset);
        assertEquals("Not the expected amount of beans", 3, mergedDoc.split("<p:bean").length - 1);
        // Note: The test expects 2 bean elements but by the way they're retrieved their root <p:beans> is
        // returned as well and needed to be considered
    }

    /**
     * Test for Issue #189. Tests if maven configurations can be merged to accumulate dependencies
     * @throws Exception
     *             shouldn't happen
     */
    @Test
    public void testMavenAddDependency() throws Exception {
        String basePath = resourcesRoot + "BasePom.xml";
        String patchPath = resourcesRoot + "PatchPom.xml";
        File baseFile = new File(basePath);
        String patchString = readFile(patchPath, charset);
        String mergedDoc = patchPreferingMerger.merge(baseFile, patchString, charset);
        assertEquals("Not the expected number of dependencies elements", 1,
            mergedDoc.split("<dependencies").length - 1);
        assertEquals("Not the expected number of dependency elements", 4, mergedDoc.split("<dependency").length - 1);
    }

    // utils

    /**
     * Reads a file into a string
     * @param path
     *            to the file
     * @param charset
     *            String name of the used charset
     * @return String
     * @throws IOException
     *             when the file can't be read
     */
    public String readFile(String path, String charset) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, charset);
    }

    /**
     * Parses a String into a Document.
     * @param string
     *            String to be parsed
     * @return Document
     * @throws Exception
     *             shouldn't happen
     */
    public org.w3c.dom.Document parseString(String string) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document mergeDoc = docBuilder.parse(new InputSource(new StringReader(string)));
        return mergeDoc;
    }
}
