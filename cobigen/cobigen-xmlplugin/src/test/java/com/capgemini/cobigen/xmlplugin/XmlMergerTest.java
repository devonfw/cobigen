/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.xmlplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.xmlplugin.action.CompleteMergeAction;

/**
 *
 * @author mbrunnli (06.04.2014)
 */
public class XmlMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/XmlMergerTest/";

    /**
     * Test Issue #31 Test method for
     * {@link com.capgemini.cobigen.xmlplugin.XmlMerger#merge(java.io.File, java.lang.String, java.lang.String)}
     * .
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws MergeException
     * @author sbasnet (24.10.2014)
     */
    @Test
    public void testMerge() throws UnsupportedEncodingException, FileNotFoundException, IOException,
        MergeException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        File base = new File(testFileRootPath + "templates.xml");
        File patch = new File(testFileRootPath + "NamedQueries.hbm.xml.ftl");

        try {
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(false);
            docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                false);
            docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
            docBuilder = docBuilderFactory.newDocumentBuilder();

            Document baseDoc;
            try {
                baseDoc =
                    docBuilder.parse(new InputSource(
                        new InputStreamReader(new FileInputStream(base), "UTF-8")));
            } catch (SAXException e) {
                if (e.getMessage().contains("[xX][mM][lL]")) {
                    System.out
                        .println("The template document has first line empty or has malformed statements");
                }
                throw new MergeException("An exception occured while parsing the base file "
                    + base.getAbsolutePath() + ":\n" + e.getMessage());
            }

            Document patchDoc;
            try {
                patchDoc =
                    docBuilder.parse(new InputSource(
                        new StringReader(IOUtils.toString(new FileReader(patch)))));
            } catch (SAXException e) {
                throw new MergeException("An exception occured while parsing the patch:\n" + e.getMessage());
            }
        } catch (ParserConfigurationException e) {
            // ignore - developer fault
        }
    }

    /**
     * This test tests, whether the schema location definition (xsi:schemaLocation) is not destroyed. One bug
     * occured which removes the xsi namespace from the schemaLocation definition
     *
     * @throws AbstractXmlMergeException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws FileNotFoundException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (21.06.2013)
     */
    @Test
    public void testMergeDoesNotDestroySchemaLocation() throws AbstractXmlMergeException,
        FileNotFoundException, IOException, MergeException {

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
