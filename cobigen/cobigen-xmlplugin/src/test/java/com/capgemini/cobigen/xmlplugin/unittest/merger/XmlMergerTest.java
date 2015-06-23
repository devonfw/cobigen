package com.capgemini.cobigen.xmlplugin.unittest.merger;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.xmlplugin.merger.XmlMerger;
import com.capgemini.cobigen.xmlplugin.merger.action.CompleteMergeAction;
import com.capgemini.cobigen.xmlplugin.merger.action.OverrideMergeAction;

/**
 * Test suite for {@link XmlMerger} class
 * @author mbrunnli (06.04.2014)
 */
public class XmlMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Test Issue https://github.com/oasp/tools-cobigen/issues/31 Test method for
     * {@link com.capgemini.cobigen.xmlplugin.merger.XmlMerger#merge(java.io.File, java.lang.String, java.lang.String)}
     * .
     * @throws Exception
     *             test fails
     * @author sbasnet (24.10.2014)
     */
    @Test
    public void testMergeHandlesUnspecificXmlParserMessage() throws Exception {
        File base = new File(testFileRootPath + "invalid.xml");
        XmlMerger xmlMerger = new XmlMerger("", new CompleteMergeAction());
        try {
            xmlMerger.merge(base, IOUtils.toString(new FileReader(base)), "UTF-8");
        } catch (MergeException e) {
            Assert.assertNotEquals(
                "Original unspecific xml parser message occured, which should be handled.",
                "The processing instruction target matching \"[xX][mM][lL]\" is not allowed", e.getMessage());
        }
    }

    /**
     * Tests Issue https://github.com/devonfw/tools-cobigen/issues/119
     * @throws Exception
     *             when something goes wrong
     * @author sholzer (Jun 23, 2015)
     */
    @Test
    public void testMergeJaxrsServiceBeans() throws Exception {

        // Copy pasted from testMergeAlsoMergesSchemaLocations()
        File base = new File(testFileRootPath + "BaseFile_OneOneNine.xml");
        File patch = new File(testFileRootPath + "PatchFile_OneOneNine.xml");

        XmlMerger merger = new XmlMerger("", new CompleteMergeAction());
        String mergedDoc = merger.merge(base, IOUtils.toString(new FileReader(patch)), "UTF-8");

        System.out.println(mergedDoc);
        assertEquals("To many jaxrs:server elements", mergedDoc.split("<jaxrs:server").length - 1, 1);
    }

    /**
     * Tests Issue https://github.com/devonfw/tools-cobigen/issues/119
     * @throws Exception
     *             when something goes wrong
     * @author sholzer (Jun 23, 2015)
     */
    @Test
    public void testMergeDozerMapping() throws Exception {

        // Copy pasted from testMergeAlsoMergesSchemaLocations()
        File base = new File(testFileRootPath + "BaseFile_OneOneNine_dozer.xml");
        File patch = new File(testFileRootPath + "PatchFile_OneOneNine_dozer.xml");

        XmlMerger merger = new XmlMerger("", new OverrideMergeAction());
        String mergedDoc = merger.merge(base, IOUtils.toString(new FileReader(patch)), "UTF-8");

        assertEquals("not the expected amount of mappings", 5, mergedDoc.split("<mapping").length - 1);

    }

    /**
     * This test tests, whether the schema location definition (xsi:schemaLocation) is not destroyed. One bug
     * occured which removes the xsi namespace from the schemaLocation definition
     *
     * @throws Exception
     *             test fails
     * @author mbrunnli (21.06.2013)
     */
    @Test
    public void testMergeDoesNotDestroySchemaLocation() throws Exception {

        File base = new File(testFileRootPath + "BaseFile_namespaces.xml");

        XmlMerger merger = new XmlMerger("", new CompleteMergeAction());
        String mergedDoc = merger.merge(base, IOUtils.toString(new FileReader(base)), "UTF-8");

        Assert.assertTrue("Schema definition 'xsi:schemaLocation' has been destroyed.",
            mergedDoc.contains("xsi:schemaLocation"));
    }

    /**
     * Tests Issue #18 - https://github.com/oasp/tools-cobigen/issues/18
     *
     * @throws FileNotFoundException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     */
    @Test
    public void testMergeAlsoMergesSchemaLocations() throws FileNotFoundException, IOException,
        MergeException {

        File base = new File(testFileRootPath + "BaseFile_namespaces.xml");
        File patch = new File(testFileRootPath + "PatchFile_namespaces.xml");

        XmlMerger merger = new XmlMerger("", new CompleteMergeAction());
        String mergedDoc = merger.merge(base, IOUtils.toString(new FileReader(patch)), "UTF-8");

        Assert
            .assertTrue(
                "Merged document does not contain schema locations defined in base.",
                mergedDoc
                    .contains("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"));
        Assert
            .assertTrue(
                "Merged document does not contain schema locations defined in patch.",
                mergedDoc
                    .contains("http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd"));
        Assert
            .assertTrue(
                "Merged schema locations are not separated by whitespace.",
                mergedDoc
                    .contains("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd "
                        + "http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd"));
    }

    /**
     * Tests whether merging of schemaLocations is not producing duplicates
     *
     * @throws FileNotFoundException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     */
    @Test
    public void testNoDuplicateNamespacesMerged() throws FileNotFoundException, IOException, MergeException {

        File base = new File(testFileRootPath + "BaseFile_namespaces.xml");

        XmlMerger merger = new XmlMerger("", new CompleteMergeAction());
        String mergedDoc = merger.merge(base, IOUtils.toString(new FileReader(base)), "UTF-8");

        Assert
            .assertFalse(
                "Merge duplicates schema locations.",
                mergedDoc
                    .contains("http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd "
                        + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"));
    }

}
