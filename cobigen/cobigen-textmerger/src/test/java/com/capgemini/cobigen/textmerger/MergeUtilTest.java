package com.capgemini.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
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

    /**
     * Tests if the method to split the patch by anchor tags actually works as it is supposed to
     */
    @Test
    public void testProperMappingOfAnchorsAndText() {
        String testString = "// anchor:test:tester:anchorend\n line1 " + "\n // anchor:test2:tester2:anchorend\n line2 "
            + "\n // anchor:test22:tester22:anchorend\n line3 " + "\n // anchor:test24:tester2:anchorend\n line4 "
            + "\n // anchor:test21:tester2:anchorend\n line5 " + "\n // anchor:test25:tester2:anchorend\n line6 "
            + "\n // anchor:test211:tester2:anchorend\n line7 " + "\n // anchor:test2111:tester2:anchorend\n line8 "
            + "\n // anchor:test21111:tester2:anchorend\n line9 "
            + "\n // anchor:test2221:tester2:anchorend\n line10 anchor:::anchorend "
            + "\n // anchor:test2213123:tester2:anchorend\n Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        Map<String, String> result = MergeUtil.splitByAnchors(testString);
        assertThat(result).isEqualTo(toBe);
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
        String testString = "// anchor:test:tester:anchorend\nline1";
        String result = MergeUtil.appendText(testString, MergeUtil.getAnchorRegexDocumentpart("test2"), toBe, true);
        String result2 = MergeUtil.appendText(testString, MergeUtil.getAnchorRegexDocumentpart("test2"), toBe, false);
        assertThat(result)
            .isEqualTo("// anchor:test2:tester2:anchorend\nline2\n// anchor:test:tester:anchorend\nline1\n");
        assertThat(result2)
            .isEqualTo("// anchor:test:tester:anchorend\nline1\n// anchor:test2:tester2:anchorend\nline2\n");
    }

}
