package com.capgemini.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
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
     *             if errors occur while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithoutNewLineNoAnchors() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "Test3", "UTF-8");
        assertThat(mergedString).isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "MergedFile.txt")));
    }

    /**
     * Tests a merge with adding a new line before appending the patch
     * @throws Exception
     *             if errors occur while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithNewLineNoAnchors() throws Exception {
        TextAppender appender = new TextAppender("", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "Test3", "UTF-8");
        assertThat(mergedString)
            .isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "MergedFile_withNewLine.txt")));
    }

    /**
     * Tests a merge with adding a new line before appending the patch
     * @throws Exception
     *             if errors occur while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_appendWithNewLineNoAnchors_onlyIfPathIsNotEmpty() throws Exception {
        TextAppender appender = new TextAppender("", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "", "UTF-8");
        assertThat(mergedString).isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "BaseFile.txt")));
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
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectHeaderFooter.txt")));
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
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectHeaderFooter.txt")));
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
        assertThat(mergedString)
            .isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedCorrectOrder.txt")));
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
        assertThat(mergedString)
            .isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedReplaced.txt")));
    }

    /**
     * Tests if using the mergestrategy "appendbefore/appendafter" appends the patch in the correct position
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testAppendOrder() throws Exception {
        TextAppender appender = new TextAppender("", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/AnchoredBaseFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/PatchAppend.txt")), "UTF-8");
        assertThat(mergedString)
            .isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedAppendOrder.txt")));
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
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/MergedEmptyMergeStrategy.txt")));
    }
}
