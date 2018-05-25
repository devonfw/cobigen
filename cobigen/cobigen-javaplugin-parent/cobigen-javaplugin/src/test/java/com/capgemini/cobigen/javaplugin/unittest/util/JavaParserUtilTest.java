package com.devonfw.cobigen.javaplugin.unittest.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil;

/**
 * This class contains testcases for {@link JavaParserUtil}
 */
public class JavaParserUtilTest {

    /**
     * Test method for
     * {@link com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil#resolveToSimpleType(java.lang.String)}.
     */
    @Test
    public void testResolveToSimpleType() {
        String testString1 = "java.lang.String";
        String expectedString1 = "String";

        String testString2 = "java.util.List<java.lang.String>";
        String expectedString2 = "List<String>";

        String testString3 = "java.util.HashMap<java.lang.String, java.util.List<java.lang.Integer>>";
        String expectedString3 = "HashMap<String, List<Integer>>";

        String testString4 = "List<Map<Map<String, String>,List<String>>>";
        String expectedString4 = "List<Map<Map<String, String>,List<String>>>";

        String testString5 =
            "java.util.List<java.util.Map<java.util.Map<java.lang.String, java.lang.String>,java.util.List<java.lang.String>>>";
        String expectedString5 = "List<Map<Map<String, String>,List<String>>>";

        assertThat(JavaParserUtil.resolveToSimpleType(testString1)).isEqualTo(expectedString1);
        assertThat(JavaParserUtil.resolveToSimpleType(testString2)).isEqualTo(expectedString2);
        assertThat(JavaParserUtil.resolveToSimpleType(testString3)).isEqualTo(expectedString3);
        assertThat(JavaParserUtil.resolveToSimpleType(testString4)).isEqualTo(expectedString4);
        assertThat(JavaParserUtil.resolveToSimpleType(testString5)).isEqualTo(expectedString5);

    }

}
