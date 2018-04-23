package com.capgemini.cobigen.textmerger;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.textmerger.util.MergeUtil;

/**
 *
 */
public class MergeUtilTest {

    /**
     * Simple map that represents a text file split by anchors
     */
    private static Map<String, String> toBe = new HashMap<String, String>() {

        {
            put("// anchor:test:tester:anchorend", "line1");
            put("// anchor:test2:tester2:anchorend", "line2");
        }
    };

    /**
     * Tests if the method to split the patch by anchor tags actually works as it is supposed to
     */
    @Test
    public void testProperMappingOfAnchorsAndText() {
        String testString = "// anchor:test:tester:anchorend \n line1 \n // anchor:test2:tester2:anchorend \n line2";
        Map<String, String> result = MergeUtil.splitByAnchors(testString);
        Assert.assertEquals(toBe, result);
    }

    /**
     *
     */
    @Test
    public void testHasAnchorFittingRegex() {
        String docPartRegex = MergeUtil.getAnchorRegexDocumentpart("test");
        String mergeStratRegex = MergeUtil.getAnchorRegexMergestrategy("tester2");
        Assert.assertTrue(MergeUtil.hasKeyMatchingRegularExpression(docPartRegex, toBe));
        Assert.assertTrue(MergeUtil.hasKeyMatchingRegularExpression(mergeStratRegex, toBe));
    }

    /**
     * Tests if proper regular expressions can be created and searched for in MergeUtil method
     */
    @Test
    public void testGetAnchorFittingRegex() {
        String docPartRegex = MergeUtil.getAnchorRegexDocumentpart("test");
        String mergeStratRegex = MergeUtil.getAnchorRegexMergestrategy("tester2");
        String result = MergeUtil.getKeyMatchingRegularExpression(docPartRegex, toBe);
        String result2 = MergeUtil.getKeyMatchingRegularExpression(mergeStratRegex, toBe);
        Assert.assertEquals("// anchor:test:tester:anchorend", result);
        Assert.assertEquals("// anchor:test2:tester2:anchorend", result2);
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
        Assert.assertEquals("// anchor:test2:tester2:anchorend\nline2\n// anchor:test:tester:anchorend\nline1\n",
            result);
        Assert.assertEquals("// anchor:test:tester:anchorend\nline1\n// anchor:test2:tester2:anchorend\nline2\n",
            result2);

    }

}
