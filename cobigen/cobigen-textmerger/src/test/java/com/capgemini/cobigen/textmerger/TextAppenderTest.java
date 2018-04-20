package com.capgemini.cobigen.textmerger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.textmerger.TextAppender.MergeUtil;

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
     *
     */
    private static Map<String, String> toBe = new HashMap<String, String>() {

        {
            put("// anchor:test:test:anchorend", "\nline1\n");
            put("// anchor:test2:test2:anchorend", "\nline2\n");
        }
    };

    /**
     * Tests a merge without adding a new line before appending the patch
     * @throws Exception
     *             if errors occur while merging
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
     *             if errors occur while merging
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
     *             if errors occur while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithNewLine_onlyIfPathIsNotEmpty() throws Exception {
        TextAppender appender = new TextAppender("", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "", "UTF-8");
        Assert.assertEquals(FileUtils.readFileToString(new File(testFileRootPath + "BaseFile.txt")), mergedString);
    }

    /**
     * Tests if header and footer are excluded in a merge and in the correct position
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_excludeHeaderFooter() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchDifferentHeaderFooter.txt")),
            "UTF-8");
        Assert.assertEquals(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectHeaderFooter.txt")),
            mergedString);
    }

    /**
     * Tests if using the mergestrategy "newlinex/xnewline" puts the newline in the correct position
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_newLineCorrectOrder() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt")), "UTF-8");
        Assert.assertEquals(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectHeaderFooter.txt")),
            mergedString);
    }

    /**
     * Tests if the document parts are in the correct order after merging, e.g. to avoid table ends to come
     * before table starts
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_CorrectOrder() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchTestOrder.txt")), "UTF-8");
        Assert.assertEquals(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectOrder.txt")),
            mergedString);
    }

    /**
     * Tests if using the mergestrategy "replace" properly replaces the document part
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_replacement() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchTestReplacement.txt")), "UTF-8");
        Assert.assertEquals(FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedReplaced.txt")),
            mergedString);
    }

    /**
     * Tests if using the mergestrategy "appendx/xappend" appends the patch in the correct position
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testAppendOrder() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchAppend.txt")), "UTF-8");
        Assert.assertEquals(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedAppendOrder.txt")), mergedString);
    }

    /**
     * Tests if not defining a mergestrategy(anchor:${documentpart}:anchorend) throws an exception
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testEmptyMergestrategyOnlyAppends() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchEmptyMergeStrategy.txt")),
            "UTF-8");
        Assert.assertEquals(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedEmptyMergeStrategy.txt")),
            mergedString);
    }

    /**
     * Tests if the method to split the patch by anchor tags actually works as it is supposed to
     */
    @Test
    public void testProperMappingOfAnchorsAndText() {
        String testString = "// anchor:test:test:anchorend \n line1 \n // anchor:test2:test2:anchorend \n line2";
        Map<String, String> result = MergeUtil.splitByAnchors(testString);
        Assert.assertEquals(toBe, result);
    }
}
