package com.capgemini.cobigen.propertyplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.propertyplugin.PropertyMerger;

/**
 * 
 * * Testing of PropertyMerger of its override property
 * 
 * @author sbasnet(06.05.2014)
 */
public class PropertyMergerTest extends TestCase {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/";

    /**
     * This test checks if the overridden has occurred successfully or not
     * 
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws MergeException
     * @author sbasnet (06.05.2014)
     */
    @Test
    public void testPropertyMergeOverride() throws FileNotFoundException, IOException, MergeException {
        File base = new File(testFileRootPath + "test.properties");
        PropertyMerger pMerger = new PropertyMerger("", true);
        String mergedPropFile =
            pMerger.merge(base, IOUtils.toString(new FileReader(new File(testFileRootPath + "Name.ftl"))),
                "UTF-8");
        Assert.assertTrue("NachNameOverride", mergedPropFile.contains("NachNameOverride"));
        Assert.assertFalse("nachNameOriginal", mergedPropFile.contains("nachNameOriginal"));
        Assert.assertTrue("FirstName", mergedPropFile.contains("firstName"));
        Assert.assertTrue("lastName", mergedPropFile.contains("lastName"));
    }

    /**
     * This test checks if the overridden has occurred successfully or not
     * 
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws MergeException
     * @author sbasnet (06.05.2014)
     */
    @Test
    public void testPropertyMergeWithoutOverride() throws FileNotFoundException, IOException, MergeException {
        File base = new File(testFileRootPath + "test.properties");
        PropertyMerger pMerger = new PropertyMerger("", false);
        String mergedPropFile =
            pMerger.merge(base, IOUtils.toString(new FileReader(new File(testFileRootPath + "Name.ftl"))),
                "UTF-8");
        Assert.assertFalse("NachNameOverride", mergedPropFile.contains("NachNameOverride"));
        Assert.assertTrue("nachNameOriginal", mergedPropFile.contains("nachNameOriginal"));
        Assert.assertTrue("FirstName", mergedPropFile.contains("firstName"));
        Assert.assertTrue("lastName", mergedPropFile.contains("lastName"));
    }

}
