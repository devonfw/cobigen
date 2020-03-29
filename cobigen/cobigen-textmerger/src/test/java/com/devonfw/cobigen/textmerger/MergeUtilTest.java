package com.devonfw.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.devonfw.cobigen.textmerger.anchorextension.Anchor;
import com.devonfw.cobigen.textmerger.anchorextension.MergeStrategy;
import com.devonfw.cobigen.textmerger.anchorextension.MergeUtil;

/**
 *
 */
public class MergeUtilTest {

    private static final MergeStrategy testStrat = MergeStrategy.APPEND;

    /**
     * Map that represents a text file split by anchors, with one large entry and one containing a wrong
     * anchor definition to test if it properly parses the text around it
     */
    private static final LinkedHashMap<Anchor, String> toBe = new LinkedHashMap<Anchor, String>() {
        {
            put(new Anchor("// ", "test", testStrat, false, false), "line1");
            put(new Anchor("// ", "test2", testStrat, false, false), "line2");
            put(new Anchor("// ", "test22", testStrat, false, false), "line3");
            put(new Anchor("// ", "test24", testStrat, false, false), "line4");
            put(new Anchor("// ", "test21", testStrat, false, false), "line5");
            put(new Anchor("// ", "test25", testStrat, false, false), "line6");
            put(new Anchor("// ", "test211", testStrat, false, false), "line7");
            put(new Anchor("// ", "test2111", testStrat, false, false), "line8");
            put(new Anchor("// ", "test21111", testStrat, false, false), "line9");
            put(new Anchor("// ", "test2221", testStrat, false, false), "line10");
            put(new Anchor("// ", "test2213123", testStrat, false, false), "Lorem ipsum dolor sit amet");
        }
    };

    /**
     * Tests if the method to split the patch by anchor tags actually works as it is supposed to, putting
     * anchors as keys and the following text up to the next anchor into its value
     */
    @Test
    public void testProperMappingOfAnchorsAndText() {
        String testString =
            "// anchor:test:append:anchorend" + System.lineSeparator() + " line1 " + System.lineSeparator()
                + "// anchor:test2:append:anchorend" + System.lineSeparator() + " line2 " + System.lineSeparator()
                + "// anchor:test22:append:anchorend" + System.lineSeparator() + " line3 " + System.lineSeparator()
                + "// anchor:test24:append:anchorend" + System.lineSeparator() + " line4 " + System.lineSeparator()
                + "// anchor:test21:append:anchorend" + System.lineSeparator() + " line5 " + System.lineSeparator()
                + "// anchor:test25:append:anchorend" + System.lineSeparator() + " line6 " + System.lineSeparator()
                + "// anchor:test211:append:anchorend" + System.lineSeparator() + " line7 " + System.lineSeparator()
                + "// anchor:test2111:append:anchorend" + System.lineSeparator() + " line8 " + System.lineSeparator()
                + "// anchor:test21111:append:anchorend" + System.lineSeparator() + " line9 " + System.lineSeparator()
                + "// anchor:test2221:append:anchorend" + System.lineSeparator() + " line10 " + System.lineSeparator()
                + "// anchor:test2213123:append:anchorend" + System.lineSeparator() + " Lorem ipsum dolor sit amet";
        Map<Anchor, String> result = new LinkedHashMap<>();
        try {
            result = MergeUtil.splitByAnchors(testString, testStrat);
        } catch (Exception e) {
            fail("Expected no Exception, got the following Exception instead: " + e.getMessage());
        }
        assertThat(result).isEqualTo(toBe);
    }

    /**
     * Tests whether anchor:${mergestrategy}:anchorend will throw an exception about its wrong anchor
     * definition.
     */
    @Test
    public void testProperMappingOfAnchorsAndTextWhenAnchorDoesntHaveEnoughPartsAndNoDefaultMergeStrategyDefined() {
        String testString = "// anchor:header:append:anchorend" + System.lineSeparator() + "test1"
            + System.lineSeparator() + "// anchor:test:append_newline:anchorend" + System.lineSeparator() + "test2"
            + System.lineSeparator() + "// anchor:blabla:override:anchorend" + System.lineSeparator() + "test2.5"
            + System.lineSeparator() + "// anchor:anotherone:hgfds:anchorend" + System.lineSeparator() + "test3"
            + System.lineSeparator() + "// anchor:footer:append:anchorend" + System.lineSeparator() + "test4";
        try {
            MergeUtil.splitByAnchors(testString, testStrat);

            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception e) {
            assertThat(e).hasMessage("Error at anchor for documentpart: // anchor:anotherone::anchorend."
                + " Incorrect anchor definition, no proper mergestrategy defined.\nSee "
                + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#mergestrategies "
                + "for additional info");
        }
    }

    /**
     * Tests wether anchor:${}:anchorend will be updated to anchor:${}:defaultStrategy:anchorend
     */
    @Test
    public void testProperMappingOfAnchorsAndTextAddsDefaultMergeStrategy() {
        String testString = "// anchor:header:append:anchorend" + System.lineSeparator() + "test1"
            + System.lineSeparator() + "// anchor:test:append_newline:anchorend" + System.lineSeparator() + "test2"
            + System.lineSeparator() + "// anchor:blabla:override:anchorend" + System.lineSeparator() + "test2.5"
            + System.lineSeparator() + "// anchor:anotherone:anchorend" + System.lineSeparator() + "test3"
            + System.lineSeparator() + "// anchor:footer:append:anchorend" + System.lineSeparator() + "test4";
        LinkedHashMap<Anchor, String> expected = new LinkedHashMap<>();

        expected.put(new Anchor("// ", "header", testStrat, false, false), "test1");
        expected.put(new Anchor("// ", "test", testStrat, true, false), "test2");
        expected.put(new Anchor("// ", "blabla", MergeStrategy.OVERRIDE, false, false), "test2.5");
        expected.put(new Anchor("// ", "anotherone", testStrat, false, false), "test3");
        expected.put(new Anchor("// ", "footer", testStrat, false, false), "test4");

        Map<Anchor, String> test = new LinkedHashMap<>();
        try {
            test = MergeUtil.splitByAnchors(testString, testStrat);
            assertThat(test).isEqualTo(expected);
        } catch (Exception e) {
            fail("Expected no Exception, got the following Exception instead: " + e.getMessage());
        }
    }

    /**
     * Tests if, when anchors are defined, but there is no anchor at the start, a default anchor is added to
     * the beginning of the text. Also makes sure that no default anchor is placed when there is an anchor at
     * the start.
     */
    @Test
    public void throwsExceptionWhenNoAnchorAsFirstLine() {
        String test = "// anchor:test:append:anchorend" + System.lineSeparator() + "test" + System.lineSeparator()
            + "// anchor:test2:append:anchorend" + System.lineSeparator() + "test2";
        String test2 =
            "blabla" + System.lineSeparator() + "// anchor:test2:test2:anchorend" + System.lineSeparator() + "test2";
        try {
            MergeUtil.splitByAnchors(test, testStrat);
        } catch (Exception e) {
            fail("Expected no Exception, got the following Exception instead: " + e.getClass());
        }
        try {
            MergeUtil.splitByAnchors(test2, testStrat);

            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception e) {
            assertThat(e).hasMessage(
                "Incorrect document structure. Anchors are defined but there is no anchor at the start of the document.\n"
                    + "See https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#general and "
                    + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#error-list "
                    + "for more details");
        }
    }

    /**
     * Tests if checking the existence of an anchor of a specific document part works as intended
     */
    @Test
    public void testHasAnchorFittingDocPart() {
        assertThat(MergeUtil.hasKeyMatchingDocumentPart("test", toBe));
        assertThat(!MergeUtil.hasKeyMatchingDocumentPart("table", toBe));
    }

    /**
     * Tests if getting an anchor of a specific Document part works as intended
     */
    @Test
    public void testGetAnchorFittingDocPart() {
        Anchor result = MergeUtil.getKeyMatchingDocumentPart("test", toBe);
        assertThat(result.getAnchor()).isEqualTo("// anchor:test:append:anchorend");
        assertThat(MergeUtil.getKeyMatchingDocumentPart("table", toBe)).isEqualTo(null);
    }

    /**
     * Tests if the text is appended in the way that is wanted in the MergeUtil.appendText method. Tests
     * appending before and after.
     */
    @Test
    public void testProperAppendingOfText() {
        LinkedHashMap<Anchor, String> test = new LinkedHashMap<>();
        test.put(new Anchor("// ", "test", testStrat, false, false), "test1");
        LinkedHashMap<Anchor, String> test2 = new LinkedHashMap<>();
        test2.put(new Anchor("// ", "test", testStrat, false, false), "test2");

        String testString = "", testString2 = "", testString3 = "", testString4 = "";

        testString = MergeUtil.appendText(testString, "test", test, false, false);
        testString = MergeUtil.appendText(testString, "test", test2, true, true);

        testString2 = MergeUtil.appendText(testString2, "test", test, false, false);
        testString2 = MergeUtil.appendText(testString2, "test", test2, false, true);

        testString3 = MergeUtil.appendText(testString3, "test", test, false, false);
        testString3 = MergeUtil.appendText(testString3, "test", test2, true, false);

        testString4 = MergeUtil.appendText(testString4, "test", test, false, false);
        testString4 = MergeUtil.appendText(testString4, "test", test2, false, false);

        assertThat(testString).isEqualTo(System.lineSeparator() + "// anchor:test:append:anchorend"
            + System.lineSeparator() + "test2" + System.lineSeparator() + "test1");
        assertThat(testString2).isEqualTo(System.lineSeparator() + "// anchor:test:append:anchorend"
            + System.lineSeparator() + "test1" + System.lineSeparator() + "test2");
        assertThat(testString3).isEqualTo(System.lineSeparator() + "test2" + System.lineSeparator() + "test1");
        assertThat(testString4).isEqualTo(System.lineSeparator() + "test1" + System.lineSeparator() + "test2");
    }

    /**
     * Tests if text is added in the intended way and the current anchor is properly deleted from base and
     * patch maps
     */
    @Test
    public void testAddingText() {
        String test = "correct ";
        Anchor anchor = new Anchor("// ", "test", testStrat, false, false);
        LinkedHashMap<Anchor, String> base = new LinkedHashMap<>();
        base.put(anchor, "");
        LinkedHashMap<Anchor, String> patch = new LinkedHashMap<>();
        patch.put(anchor, "");

        String toAppend = "result";
        test = MergeUtil.addTextAndDeleteCurrentAnchor(test, toAppend, base, patch, anchor);
        String expected = "correct result";
        assertThat(test).isEqualTo(expected);
        assertThat(!base.containsKey(anchor));
        assertThat(!patch.containsKey(anchor));
    }

    /**
     * Tests if MergeUtil.hasAnchors properly identifies whether a text has anchors or not.
     */
    @Test
    public void testHasAnchors() {
        String test = "test";
        String test2 =
            System.lineSeparator() + "// anchor:test:append:anchorend" + System.lineSeparator() + " lorem ipsum";
        assertThat(!MergeUtil.hasAnchors(test));
        assertThat(MergeUtil.hasAnchors(test2));
    }

    /**
     * Tests if, when sets are joined using MergeUtil.joinKeySetsRetainOrder, the order of elements is
     * correct. Tests comparing string as opposed to set because sets (even linked ones) are equal no matter
     * of the order.
     */
    @Test
    public void testJoinedKeySetsRetainOrder() {
        LinkedHashMap<Anchor, String> testOne = new LinkedHashMap<>();
        testOne.put(new Anchor("// ", "1", MergeStrategy.APPEND, false, false), "");
        testOne.put(new Anchor("// ", "2", MergeStrategy.APPEND, false, false), "");
        testOne.put(new Anchor("// ", "3", MergeStrategy.APPEND, false, false), "");
        testOne.put(new Anchor("// ", "6", MergeStrategy.APPEND, false, false), "");
        testOne.put(new Anchor("// ", "9", MergeStrategy.APPEND, false, false), "");
        testOne.put(new Anchor("// ", "10", MergeStrategy.APPEND, false, false), "");

        LinkedHashMap<Anchor, String> testTwo = new LinkedHashMap<>();
        testTwo.put(new Anchor("// ", "1", MergeStrategy.APPEND, false, false), "");
        testTwo.put(new Anchor("// ", "4", MergeStrategy.APPEND, false, false), "");
        testTwo.put(new Anchor("// ", "5", MergeStrategy.APPEND, false, false), "");
        testTwo.put(new Anchor("// ", "6", MergeStrategy.APPEND, false, false), "");
        testTwo.put(new Anchor("// ", "7", MergeStrategy.APPEND, false, false), "");
        testTwo.put(new Anchor("// ", "8", MergeStrategy.APPEND, false, false), "");

        String result = "";
        ArrayList<Anchor> resultList = MergeUtil.joinKeySetsRetainOrder(testOne, testTwo);
        for (Anchor s : resultList) {
            result += s.getDocPart() + " ";
        }
        String expected = "1 2 3 4 5 6 9 10 7 8 ";
        assertThat(result).isEqualTo(expected);

        result = "";
        ArrayList<Anchor> resultList2 = MergeUtil.joinKeySetsRetainOrder(testOne, testOne);
        for (Anchor s : resultList2) {
            result += s.getDocPart() + " ";
        }
        String expected2 = "1 2 3 6 9 10 ";
        assertThat(result).isEqualTo(expected2);
    }

    /**
     * Tests if MergeUtil.canBeSkipped properly shows whether an anchor can be skipped or not
     */
    @Test
    public void testSkipping() {
        Anchor test1 = new Anchor("// ", "test", MergeStrategy.APPEND, false, false);
        Anchor test2 = new Anchor("// ", "test2", MergeStrategy.APPEND, false, false);
        Anchor test3 = new Anchor("// ", "test2", MergeStrategy.APPENDBEFORE, false, false);
        Anchor test4 = new Anchor("// ", "test2", MergeStrategy.NOMERGE, false, false);
        Anchor test5 = new Anchor("// ", "test", MergeStrategy.NOMERGE, false, false);

        ArrayList<Anchor> testMap = new ArrayList<>();
        testMap.add(test1);
        testMap.add(test2);
        testMap.add(test3);
        testMap.add(test4);
        testMap.add(test5);
        assertThat(!MergeUtil.canBeSkipped(testMap, test1));
        assertThat(MergeUtil.canBeSkipped(testMap, test2));
        assertThat(!MergeUtil.canBeSkipped(testMap, test3));
        assertThat(MergeUtil.canBeSkipped(testMap, test4));
        assertThat(MergeUtil.canBeSkipped(testMap, test5));
    }

    @Test
    public void testGetMergeStrategyOfName() {
        String append = "append";
        String nomerge = "nomerge";
        String test = "test";

        assertThat(MergeUtil.getMergeStrategyOfName(append)).isEqualTo(testStrat);
        assertThat(MergeUtil.getMergeStrategyOfName(nomerge)).isEqualTo(MergeStrategy.NOMERGE);
        assertThat(MergeUtil.getMergeStrategyOfName(test)).isNull();
    }
}
