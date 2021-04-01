package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.api.util.StringUtil;

/**
 *
 */
public class StringUtilTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/test/resources/testdata/unittest/StringUtilTest";

    /**
     * Tests whether consolidateLineEndings returns proper line endings for Windows, Linux and Osx
     * @throws Exception
     *             test fails
     */
    @Test
    public void testConsolidateLineEndings() throws Exception {
        String origin = "\n";
        String origin2 = "\r\n";
        String lineDelimiterWindows = "\r\n";
        String lineDelimiterLinux = "\n";
        String lineDelimiterOsx = "\r";
        String consolidatedWindows = StringUtil.consolidateLineEndings(origin, lineDelimiterWindows);
        String consolidatedLinux = StringUtil.consolidateLineEndings(origin2, lineDelimiterLinux);
        String consolidatedOsx = StringUtil.consolidateLineEndings(origin, lineDelimiterOsx);
        assertThat(consolidatedWindows).isEqualTo(lineDelimiterWindows);
        assertThat(consolidatedLinux).isEqualTo(lineDelimiterLinux);
        assertThat(consolidatedOsx).isEqualTo(lineDelimiterOsx);
    }
}
