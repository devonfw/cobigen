package com.capgemini.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.nio.file.Path;

import org.junit.Test;

/**
 * Test suite for Paths.
 */
public class PathTest {

    /**
     * Tests the content of a Path value.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testContentOfPathValue() throws Exception {
        File testFile =
            new File("/cobigen-core/src/test/resources/testdata/unittest/config/upgrade/PathTest", "Test.txt");
        Path testPath = testFile.toPath();
        assertFalse(testPath.toString().contains("C:/"));
        assertThat(testPath.toUri().toString()).contains("C:/");
    }
}
