package com.capgemini.cobigen.javaplugin.unittest.util;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;

/**
 * This class contains testcases for {@link JavaParserUtil}
 * @author fkreis (25.06.2015)
 */
public class JavaParserUtilTest {

    /**
     * Test method for
     * {@link com.capgemini.cobigen.javaplugin.util.JavaParserUtil#resolveToSimpleType(java.lang.String)}.
     */
    @Test
    public void testResolveToSimpleType() {
        String testString1 = "java.lang.String";
        String expectedString1 = "String";

        String testString2 = "java.util.List<java.lang.String>";
        String expectedString2 = "List<String>";

        String testString3 = "java.util.HashMap<java.lang.String, java.util.List<java.lang.Integer>>";
        String expectedString3 = "HashMap<String, List<Integer>>";

        Assert.assertEquals(expectedString1, JavaParserUtil.resolveToSimpleType(testString1));
        Assert.assertEquals(expectedString2, JavaParserUtil.resolveToSimpleType(testString2));
        Assert.assertEquals(expectedString3, JavaParserUtil.resolveToSimpleType(testString3));

    }

}
