package com.capgemini.cobigen.textmerger;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author mbrunnli (03.06.2014)
 */
public class TextAppenderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/";

    /**
     * Tests a merge without adding a new line before appending the patch
     * @throws Exception
     *             if errors occured while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithoutNewLine() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "Test3", "UTF-8");
        Assert.assertEquals(FileUtils.readFileToString(new File(testFileRootPath + "MergedFile.txt")), mergedString);
    }

    /**
     * Tests a merge with adding a new line before appending the patch
     * @throws Exception
     *             if errors occured while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithNewLine() throws Exception {
        TextAppender appender = new TextAppender("", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "Test3", "UTF-8");
        Assert.assertEquals(FileUtils.readFileToString(new File(testFileRootPath + "MergedFile_withNewLine.txt")),
            mergedString);
    }

    /**
     * Tests a merge with adding a new line before appending the patch
     * @throws Exception
     *             if errors occured while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithNewLine_onlyIfPathIsNotEmpty() throws Exception {
        TextAppender appender = new TextAppender("", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "", "UTF-8");
        Assert.assertEquals(FileUtils.readFileToString(new File(testFileRootPath + "BaseFile.txt")), mergedString);
    }
}
