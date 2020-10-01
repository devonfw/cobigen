package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.util.SystemUtil;

/** Test suite for {@link SystemUtil}. */
public class SystemUtilTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/test/resources/testdata/unittest/SystemUtilTest";

    /**
     * Tests whether determineLineDelimiter returns Linux line endings
     * @throws Exception
     *             test fails
     */
    @Test
    public void testDetermineLineDelimiterLinux() throws Exception {
        Path path = Paths.get(testdataRoot, "TestLinuxLineEndings.txt");
        String targetCharset = "UTF-8";
        String lineEnding = SystemUtil.determineLineDelimiter(path, targetCharset);
        assertThat(lineEnding).isEqualTo("\n");
    }

    /**
     * Tests whether determineLineDelimiter returns Windows line endings
     * @throws Exception
     *             test fails
     */
    @Test
    public void testDetermineLineDelimiterWindows() throws Exception {
        Path path = Paths.get(testdataRoot, "TestWindowsLineEndings.txt");
        String targetCharset = "UTF-8";
        String lineEnding = SystemUtil.determineLineDelimiter(path, targetCharset);
        assertThat(lineEnding).isEqualTo("\r\n");
    }

}
