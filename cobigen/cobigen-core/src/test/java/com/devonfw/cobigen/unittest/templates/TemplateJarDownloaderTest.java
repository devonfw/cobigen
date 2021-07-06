package com.devonfw.cobigen.unittest.templates;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link TemplatesJarUtil}
 */
public class TemplateJarDownloaderTest extends AbstractUnitTest {

    /** Root path to all resources used in this test case */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/config/templatesJar/";

    /** JUnit Rule to create and automatically cleanup temporarily files/folders */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Tests the valid upgrade of a templates jar
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectTemplatesUpgrade() throws Exception {

        // preparation
        tempFolder.newFile("templates-devon4j-3.0.0.jar");
        File tmpJarFolder = tempFolder.getRoot();

        // Perform download
        TemplatesJarUtil.downloadLatestDevon4jTemplates(false, tmpJarFolder);

        // Assert
        assertThat(tmpJarFolder.list().length).isEqualTo(2); // It should download also the sources
        assertThat(tmpJarFolder.list()[0].contains("templates-devon4j-3.0.0")).isFalse();
        assertThat(tmpJarFolder.list()[1].contains("templates-devon4j-3.0.0")).isFalse();

    }

}
