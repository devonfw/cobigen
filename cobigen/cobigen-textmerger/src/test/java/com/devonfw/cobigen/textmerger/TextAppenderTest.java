package com.devonfw.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.textmerger.TextAppender;

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
        TextAppender appender = new TextAppender("textmerge_append", false);
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
        TextAppender appender = new TextAppender("textmerge_append", true);
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
        TextAppender appender = new TextAppender("textmerge_append", true);
        String mergedString = appender.merge(new File(testFileRootPath + "BaseFile.txt"), "", "UTF-8");
        assertThat(mergedString).isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "BaseFile.txt")));
    }

    /**
     * Tests a merge with adding a new line before appending the patch
     * @throws Exception
     *             if errors occur while merging
     * @author mbrunnli (03.06.2014)
     */
    @Test
    public void testMerge_override() throws Exception {
        TextAppender appender = new TextAppender("textmerge_override", false);
        String mergedString =
            appender.merge(new File(testFileRootPath + "BaseFile.txt"), "Lorem Ipsum Dolor Sit Amet", "UTF-8");
        assertThat(mergedString)
            .isEqualTo(FileUtils.readFileToString(new File(testFileRootPath + "MergedOverride.txt")));
    }

    /**
     * Tests if not defining a mergestrategy(anchor:${documentpart}::anchorend) adds a default merge strategy
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testEmptyMergestrategyGetsDefaultMergeStrategy() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestEmptyMergeStrategy.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchEmptyMergeStrategy.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedEmptyMergeStrategy.txt")));
    }

    /**
     * Tests if not defining a mergestrategy(anchor:${documentpart}::anchorend) adds a default merge strategy
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testDefaultMergeStrategyBecomesAPPENDWhenAppendNewLineInTemplatesxml()
        throws Exception {
        TextAppender appender = new TextAppender("textmerge_appendWithNewLine", true);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestEmptyMergeStrategy.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchEmptyMergeStrategy.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedEmptyMergeStrategy.txt")));
    }

    /**
     * Tests if only having a documentpart defined in either patch or base properly puts in into the merged
     * file, ignoring newline to avoid multiple unnessecary linebreaks when patch doesn't contain that
     * documentpart anymore
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testAnchorOnlyInOneFileAndIgnoresNewline() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(
            new File(testFileRootPath + "anchortests/base/TestAnchorOnlyInOneFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchAnchorOnlyInOneFile.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(FileUtils
            .readFileToString(new File(testFileRootPath + "anchortests/merged/MergedAnchorOnlyInOneFile.txt")));
    }

    /**
     * Tests if the document parts are in the correct order after merging, e.g. to avoid table ends to come
     * before table starts
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_CorrectDocumentpartOrder() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestCorrectPartOrder.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchCorrectPartOrder.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedCorrectPartOrder.txt")));
    }

    /**
     * Tests if using the mergestrategy "appendbefore/appendafter" appends the patch and in the correct
     * position
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testAppend() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestAppending.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchAppending.txt")), "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedAppending.txt")));
    }

    /**
     * Tests if header and footer are excluded in a merge and in the correct position with other parts being
     * usually merged
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_excludeHeaderFooter() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestHeaderFooter.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchHeaderFooter.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedHeaderFooter.txt")));
    }

    /**
     * Tests if header and footer are excluded in a merge and in the correct position with other parts being
     * usually merged
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_OnlyPatchHeaderFooterInRightPlace() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString =
            appender.merge(
                new File(testFileRootPath + "anchortests/base/TestOnlyPatchHeaderFooter.txt"), FileUtils
                    .readFileToString(new File(testFileRootPath + "anchortests/patch/PatchOnlyPatchHeaderFooter.txt")),
                "UTF-8");
        assertThat(mergedString).isEqualTo(FileUtils
            .readFileToString(new File(testFileRootPath + "anchortests/merged/MergedOnlyPatchHeaderFooter.txt")));
    }

    /**
     * Tests if using the mergestrategy "newline_x/x_newline" puts the newline in the correct position for
     * applicable strategies, but deletes newlines at start and end of file
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_newLines() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestNewlineFile.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchNewLineFile.txt")), "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedNewlineFile.txt")));
    }

    /**
     * Tests if using newline_x with incompatible mergestrategies defaults to simply deleting the newline and
     * executes the desired mergestrategy properly
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_newlineIncompatibleDefaultedToWithoutNewlineAndStrategyWorks() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestNewlineDefaults.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchNewlineDefaults.txt")),
            "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedNewlineDefaults.txt")));
    }

    /**
     * Tests if using the mergestrategy "override" properly replaces the document part
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_override() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        String mergedString = appender.merge(new File(testFileRootPath + "anchortests/base/TestOverride.txt"),
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchOverride.txt")), "UTF-8");
        assertThat(mergedString).isEqualTo(
            FileUtils.readFileToString(new File(testFileRootPath + "anchortests/merged/MergedOverride.txt")));
    }

    /**
     * Tests if creating an anchor with a non-existing merge strategy throws an exception
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testNonExistentMergestrategyThrowsException() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        File file = new File(testFileRootPath + "anchortests/base/TestEmptyMergeStrategy.txt");
        try {
            appender.merge(file, FileUtils.readFileToString(
                new File(testFileRootPath + "anchortests/patch/PatchInvalidMergeStrategy.txt")), "UTF-8");
            failBecauseExceptionWasNotThrown(MergeException.class);
        } catch (MergeException e) {
            assertThat(e).hasMessage(getMergeExceptionMessage(file,
                "Error at anchor for documentpart: // anchor:part1::anchorend. "
                    + "Incorrect anchor definition, no proper mergestrategy defined.\nSee "
                    + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#mergestrategies "
                    + "for additional info"));
        }
    }

    /**
     * Tests if not correctly defining an anchor throws an exception
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testNoAnchorAtStartOfBase() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        File file = new File(testFileRootPath + "anchortests/base/TestNoAnchorAtStart.txt");
        try {
            appender.merge(file,
                FileUtils.readFileToString(new File(testFileRootPath + "anchortests/patch/PatchNoAnchorAtStart.txt")),
                "UTF-8");
            failBecauseExceptionWasNotThrown(MergeException.class);
        } catch (MergeException e) {
            assertThat(e).hasMessage(getMergeExceptionMessage(file,
                "Incorrect document structure. Anchors are defined but there is no anchor at the start of the document.\n"
                    + "See https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#general and "
                    + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#error-list "
                    + "for more details"));
        }
    }

    /**
     * Tests if not correctly defining an anchor throws an exception
     * @throws Exception
     *             if errors occur while merging
     */
    @Test
    public void testAnchorMerge_testNoAnchorAtStartOfPatch() throws Exception {
        TextAppender appender = new TextAppender("textmerge_append", false);
        File file = new File(testFileRootPath + "anchortests/patch/PatchNoAnchorAtStart.txt");
        try {
            appender.merge(file,
                FileUtils.readFileToString(new File(testFileRootPath + "anchortests/base/TestNoAnchorAtStart.txt")),
                "UTF-8");
            failBecauseExceptionWasNotThrown(MergeException.class);
        } catch (MergeException e) {
            assertThat(e).hasMessage(getMergeExceptionMessage(file,
                "Incorrect document structure. Anchors are defined but there is no anchor at the start of the document.\n"
                    + "See https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#general and "
                    + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#error-list "
                    + "for more details"));
        }
    }

    /**
     * Helper method that creates a String matching the usual look of a merge exception
     * @param base
     *            The file that is supposed to be patched
     * @param msg
     *            The message specifying why exactly merging failed
     * @return The full exception message
     */
    private String getMergeExceptionMessage(final File base, final String msg) {
        return "Unable to merge a generated patch into file " + base + ": " + msg;
    }
}
