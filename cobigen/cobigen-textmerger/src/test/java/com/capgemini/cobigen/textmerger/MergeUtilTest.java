package com.capgemini.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Test;

import com.capgemini.cobigen.textmerger.util.MergeUtil;

/**
 *
 */
public class MergeUtilTest {

    /**
     * Map that represents a text file split by anchors, with one large entry and one containing a wrong
     * anchor definition to test if it properly parses the text around it
     */
    private static LinkedHashMap<String, String> toBe = new LinkedHashMap<String, String>() {

        {
            put("// anchor:test:tester:anchorend", "line1");
            put("// anchor:test2:tester2:anchorend", "line2");
            put("// anchor:test22:tester22:anchorend", "line3");
            put("// anchor:test24:tester2:anchorend", "line4");
            put("// anchor:test21:tester2:anchorend", "line5");
            put("// anchor:test25:tester2:anchorend", "line6");
            put("// anchor:test211:tester2:anchorend", "line7");
            put("// anchor:test2111:tester2:anchorend", "line8");
            put("// anchor:test21111:tester2:anchorend", "line9");
            put("// anchor:test2221:tester2:anchorend", "line10 anchor:::anchorend");
            put("// anchor:test2213123:tester2:anchorend",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                    + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
                    + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
                    + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        }
    };

    private static LinkedHashMap<String, String> toBe2 = new LinkedHashMap<String, String>() {

        {
            put("// anchor:header:nothing:anchorend", "test1");
            put("// anchor:test:nothingnewline:anchorend", "test2");
            put("// anchor:blabla:replace:anchorend", "test2.5");
            put("// anchor::anotherone:anchorend", "test3");
            put("// anchor:footer:nothing:anchorend", "test4");
        }
    };

    /**
     * Tests if the method to split the patch by anchor tags actually works as it is supposed to, putting
     * anchors as keys and the following text up to the next anchor into its value
     */
    @Test
    public void testProperMappingOfAnchorsAndText() {
        String testString = "// anchor:test:tester:anchorend" + System.lineSeparator() + " line1 "
            + System.lineSeparator() + " // anchor:test2:tester2:anchorend" + System.lineSeparator() + " line2 "
            + System.lineSeparator() + " // anchor:test22:tester22:anchorend" + System.lineSeparator() + " line3 "
            + System.lineSeparator() + " // anchor:test24:tester2:anchorend" + System.lineSeparator() + " line4 "
            + System.lineSeparator() + " // anchor:test21:tester2:anchorend" + System.lineSeparator() + " line5 "
            + System.lineSeparator() + " // anchor:test25:tester2:anchorend" + System.lineSeparator() + " line6 "
            + System.lineSeparator() + " // anchor:test211:tester2:anchorend" + System.lineSeparator() + " line7 "
            + System.lineSeparator() + " // anchor:test2111:tester2:anchorend" + System.lineSeparator() + " line8 "
            + System.lineSeparator() + " // anchor:test21111:tester2:anchorend" + System.lineSeparator() + " line9 "
            + System.lineSeparator() + " // anchor:test2221:tester2:anchorend" + System.lineSeparator()
            + " line10 anchor:::anchorend " + System.lineSeparator() + " // anchor:test2213123:tester2:anchorend"
            + System.lineSeparator() + " Lorem ipsum dolor sit amet,"
            + " consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            + " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            + " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
            + " Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        Map<String, String> result = MergeUtil.splitByAnchors(testString);
        assertThat(result).isEqualTo(toBe);
    }

    /**
     * Tests whether anchor:${mergestrategy}:anchorend will be updated to anchor::${mergestrategy}:anchorend
     */
    @Test
    public void testProperMappingOfAnchorsAndTextWhenAnchorDoesntHaveEnoughParts() {
        String testString = "// anchor:header:nothing:anchorend" + System.lineSeparator() + "test1"
            + System.lineSeparator() + "// anchor:test:nothingnewline:anchorend" + System.lineSeparator() + "test2"
            + System.lineSeparator() + "// anchor:blabla:replace:anchorend" + System.lineSeparator() + "test2.5"
            + System.lineSeparator() + "// anchor:anotherone:anchorend" + System.lineSeparator() + "test3"
            + System.lineSeparator() + "// anchor:footer:nothing:anchorend" + System.lineSeparator() + "test4";
        Map<String, String> result = MergeUtil.splitByAnchors(testString);
        assertThat(result).isEqualTo(toBe2);
    }

    /**
     * Tests if, when anchors are defined, but there is no anchor at the start, a default anchor is added to
     * the beginning of the text. Also makes sure that no default anchor is placed when there is an anchor at
     * the start.
     */
    @Test
    public void testOnlyAddsDefaultAnchorWhenNoAnchorAsFirstLine() {
        String test = "// anchor:test:test:anchorend" + System.lineSeparator() + "test" + System.lineSeparator()
            + "// anchor:test2:test2:anchorend" + System.lineSeparator() + "test2";
        String test2 =
            "blabla" + System.lineSeparator() + "// anchor:test2:test2:anchorend" + System.lineSeparator() + "test2";
        LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>() {
            {
                put("// anchor:test:test:anchorend", "test");
                put("// anchor:test2:test2:anchorend", "test2");
            }
        };
        LinkedHashMap<String, String> expected2 = new LinkedHashMap<String, String>() {
            {
                put("// anchor:default:replace:anchorend", "blabla");
                put("// anchor:test2:test2:anchorend", "test2");
            }
        };
        LinkedHashMap<String, String> result = MergeUtil.splitByAnchors(test);
        LinkedHashMap<String, String> result2 = MergeUtil.splitByAnchors(test2);
        assertThat(result).isEqualTo(expected);
        assertThat(result2).isEqualTo(expected2);
    }

    /**
     *
     */
    @Test
    public void testHasAnchorFittingRegex() {
        String docPartRegex = MergeUtil.getAnchorRegexDocumentpart("test");
        String mergeStratRegex = MergeUtil.getAnchorRegexMergestrategy("tester2");
        assertThat(MergeUtil.hasKeyMatchingRegularExpression(docPartRegex, toBe));
        assertThat(MergeUtil.hasKeyMatchingRegularExpression(mergeStratRegex, toBe));
    }

    /**
     * Tests if proper regular expressions can be created and searched for in MergeUtil method
     */
    @Test
    public void testGetAnchorFittingRegex() {
        String docPartRegex = MergeUtil.getAnchorRegexDocumentpart("test");
        String mergeStratRegex = MergeUtil.getAnchorRegexMergestrategy("tester22");
        String result = MergeUtil.getKeyMatchingRegularExpression(docPartRegex, toBe);
        String result2 = MergeUtil.getKeyMatchingRegularExpression(mergeStratRegex, toBe);
        assertThat(result).isEqualTo("// anchor:test:tester:anchorend");
        assertThat(result2).isEqualTo("// anchor:test22:tester22:anchorend");
    }

    /**
     * Tests if the text is appended in the way that is wanted in the MergeUtil.appendText method. Tests
     * appending before and after.
     */
    @Test
    public void testProperAppendingOfText() {
        LinkedHashMap<String, String> test = new LinkedHashMap<String, String>() {
            {
                put("// anchor:test:test:anchorend", "test1");
            }
        };
        LinkedHashMap<String, String> test2 = new LinkedHashMap<String, String>() {
            {
                put("// anchor:test:test:anchorend", "test2");
            }
        };
        String testString = "", testString2 = "", testString3 = "", testString4 = "";
        testString = MergeUtil.appendText(testString, MergeUtil.getAnchorRegexDocumentpart("test"), test, false, false);
        testString = MergeUtil.appendText(testString, MergeUtil.getAnchorRegexDocumentpart("test"), test2, true, true);
        testString2 =
            MergeUtil.appendText(testString2, MergeUtil.getAnchorRegexDocumentpart("test"), test, false, false);
        testString2 =
            MergeUtil.appendText(testString2, MergeUtil.getAnchorRegexDocumentpart("test"), test2, false, true);
        testString3 =
            MergeUtil.appendText(testString3, MergeUtil.getAnchorRegexDocumentpart("test"), test, false, false);
        testString3 =
            MergeUtil.appendText(testString3, MergeUtil.getAnchorRegexDocumentpart("test"), test2, true, false);
        testString4 =
            MergeUtil.appendText(testString4, MergeUtil.getAnchorRegexDocumentpart("test"), test, false, false);
        testString4 =
            MergeUtil.appendText(testString4, MergeUtil.getAnchorRegexDocumentpart("test"), test2, false, false);

        assertThat(testString).isEqualTo(System.lineSeparator() + "// anchor:test:test:anchorend"
            + System.lineSeparator() + "test2" + System.lineSeparator() + "test1");
        assertThat(testString2).isEqualTo(System.lineSeparator() + "// anchor:test:test:anchorend"
            + System.lineSeparator() + "test1" + System.lineSeparator() + "test2");
        assertThat(testString3).isEqualTo(System.lineSeparator() + "test2" + System.lineSeparator() + "test1");
        assertThat(testString4).isEqualTo(System.lineSeparator() + "test1" + System.lineSeparator() + "test2");
    }

    /**
     * Tests if MergeUtil.hasAnchors properly identifies whether a text has anchors or not.
     */
    @Test
    public void testHasAnchors() {
        String test = "test";
        String test2 =
            System.lineSeparator() + "// anchor:test:test:anchorend" + System.lineSeparator() + " lorem ipsum";
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
        LinkedHashMap<String, String> testOne = new LinkedHashMap<String, String>() {
            {
                put("1", "");
                put("2", "");
                put("16", "");
                put("5", "");
                put("7", "");
                put("3", "");
            }
        };
        LinkedHashMap<String, String> testTwo = new LinkedHashMap<String, String>() {
            {
                put("1", "");
                put("8", "");
                put("16", "");
                put("3", "");
                put("9", "");
                put("6", "");
            }
        };
        LinkedHashSet<String> resultSet = MergeUtil.joinKeySetsRetainOrder(testOne, testTwo);
        String result = "";
        for (String s : resultSet) {
            result += s + " ";
        }
        String expected = "1 2 8 16 5 7 3 9 6 ";
        assertThat(result).isEqualTo(expected);
    }

    /**
     * Tests if MergeUtil.canBeSkipped properly shows whether an anchor can be skipped or not
     */
    @Test
    public void testSkipping() {
        LinkedHashSet<String> test = new LinkedHashSet<String>() {
            {
                add("// anchor:test1:test1:anchorend");
                add("// anchor:test2:test2:anchorend");
                add("// anchor:test2:test3:anchorend");
                add("// anchor:test2:test4:anchorend");
            }
        };
        assertThat(!MergeUtil.canBeSkipped(test, "// anchor:test1:test1:anchorend"));
        assertThat(MergeUtil.canBeSkipped(test, "// anchor:test2:test2:anchorend"));
        assertThat(!MergeUtil.canBeSkipped(test, "// anchor:test2:test3:anchorend"));
        assertThat(MergeUtil.canBeSkipped(test, "// anchor:test2:test4:anchorend"));
    }

}
