/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.xmlplugin.merge;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elca.el4j.services.xmlmerge.mapper.IdentityMapper;

import com.capgemini.cobigen.xmlplugin.action.CompleteMergeAction;
import com.capgemini.cobigen.xmlplugin.action.OverrideMergeAction;
import com.capgemini.cobigen.xmlplugin.matcher.XmlMatcher;
import com.capgemini.cobigen.xmlplugin.merge.BasicXmlMerge;

/**
 * TestCase testing {@link BasicXmlMerge}
 * 
 * @author trippl
 */
public class BasicXmlMergeTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/XmlMergerTest/";

    /**
     * Creates a {@link Document} out of the given {@link File}
     * @param documentFile
     *            the {@link File} to be parsed into a {@link Document}
     * @throws Exception
     * @return the {@link Document} representing the given file
     * @author trippl (17.04.2013)
     */
    private Document createDocumentOutOfFile(File documentFile) throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setValidating(false);
        docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        docBuilder = docBuilderFactory.newDocumentBuilder();

        return docBuilder.parse(documentFile);
    }

    /**
     * Test of {@link BasicXmlMerge} merging Flow.xml files
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeFlow_NonOverride() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_flow.xml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_flow.xml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new CompleteMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

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
     * Test of {@link BasicXmlMerge} merging Overview.xhtml files
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeOverview_NonOverride() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_overview.xhtml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_overview.xhtml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new CompleteMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

        Assert.assertEquals(1, mergeDoc.getElementsByTagName("ui:composition").getLength());
        Assert.assertEquals(baseDoc.getDoctype().getLocalName(), mergeDoc.getDoctype().getLocalName());
        Assert.assertEquals(4, mergeDoc.getElementsByTagName("ui:define").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(0))
            .getElementsByTagName("title").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(1))
            .getElementsByTagName("ui:include").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(2))
            .getElementsByTagName("ui:include").getLength());
        Assert.assertEquals(1, ((Element) mergeDoc.getElementsByTagName("ui:define").item(3))
            .getElementsByTagName("ui:include").getLength());
    }

    /**
     * Test of {@link BasicXmlMerge} merging NamedQuery.xml files
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeQueries_NonOverride() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_queries.xml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_queries.xml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new CompleteMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

        Assert.assertEquals(1, mergeDoc.getElementsByTagName("hibernate-mapping").getLength());
        Assert.assertEquals(5, mergeDoc.getElementsByTagName("query").getLength());
    }

    /**
     * Test of {@link BasicXmlMerge} merging xhtml Files containing a table
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeTable_NonOverride() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_table.xhtml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_table.xhtml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new CompleteMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

        Assert.assertEquals(baseDoc.getDocumentElement().getLocalName(), mergeDoc.getDocumentElement()
            .getLocalName());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("div").getLength());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("h:dataTable").getLength());
        Assert.assertEquals(7, ((Element) mergeDoc.getDocumentElement().getElementsByTagName("h:dataTable")
            .item(0)).getElementsByTagName("h:column").getLength());
    }

    /**
     * Test of {@link BasicXmlMerge} merging a simple xml file
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeSimple_NonOverride() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_simple.xml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_simple.xml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new CompleteMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

        Assert.assertEquals(baseDoc.getDocumentElement().getLocalName(), mergeDoc.getDocumentElement()
            .getLocalName());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("a").getLength());
        Assert.assertEquals(2, mergeDoc.getDocumentElement().getElementsByTagName("b").getLength());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("c").getLength());
        Assert.assertEquals("alt",
            ((Element) mergeDoc.getDocumentElement().getElementsByTagName("b").item(0)).getAttribute("text"));
        Assert.assertEquals("alt",
            ((Element) mergeDoc.getDocumentElement().getElementsByTagName("b").item(1)).getAttribute("text"));
    }

    /**
     * Test of {@link BasicXmlMerge} merging a simpel xml file in override mode
     * @throws Exception
     * @author trippl (17.04.2013)
     */
    @Test
    public void testMergeSimple_Override() throws Exception {
        Document baseDoc = createDocumentOutOfFile(new File(testFileRootPath + "BaseFile_simple.xml"));
        Document patchDoc = createDocumentOutOfFile(new File(testFileRootPath + "PatchFile_simple.xml"));
        Document[] toMerge = { baseDoc, patchDoc };

        Document mergeDoc =
            new BasicXmlMerge(new OverrideMergeAction(), new IdentityMapper(), new XmlMatcher())
                .merge(toMerge);

        Assert.assertEquals(baseDoc.getDocumentElement().getLocalName(), mergeDoc.getDocumentElement()
            .getLocalName());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("a").getLength());
        Assert.assertEquals(2, mergeDoc.getDocumentElement().getElementsByTagName("b").getLength());
        Assert.assertEquals(1, mergeDoc.getDocumentElement().getElementsByTagName("c").getLength());
        Assert.assertEquals("neu",
            ((Element) mergeDoc.getDocumentElement().getElementsByTagName("b").item(0)).getAttribute("text"));
        Assert.assertEquals("neu",
            ((Element) mergeDoc.getDocumentElement().getElementsByTagName("b").item(1)).getAttribute("text"));
    }
}
