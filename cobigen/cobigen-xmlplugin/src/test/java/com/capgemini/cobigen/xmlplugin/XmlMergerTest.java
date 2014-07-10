/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.xmlplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.xmlplugin.XmlMerger;
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
     * This test tests, whether the schema location definition (xsi:schemaLocation) is not destroyed. One bug
     * occured which removes the xsi namespace from the schemaLocation definition
     * @throws AbstractXmlMergeException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws MergeException
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
}
