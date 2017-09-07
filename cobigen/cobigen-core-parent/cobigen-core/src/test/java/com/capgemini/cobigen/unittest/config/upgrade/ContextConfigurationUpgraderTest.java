package com.capgemini.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.capgemini.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.unittest.config.common.AbstractUnitTest;
import com.google.common.io.Files;

/**
 * Test suite for {@link TemplateConfigurationUpgrader}
 * @author mbrunnli (Jun 22, 2015)
 */
public class ContextConfigurationUpgraderTest extends AbstractUnitTest {

    /** Root path to all resources used in this test case */
    private static String testFileRootPath =
        "src/test/resources/testdata/unittest/config/upgrade/ContextConfigurationUpgraderTest/";

    /** JUnit Rule to create and automatically cleanup temporarily files/folders */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jun 22, 2015)
     */
    @Test
    public void testCorrectUpgrade_v2_0_TO_v2_1() throws Exception {

        // preparation
        File tmpTargetConfig = tempFolder.newFile(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        File sourceTestdata =
            new File(testFileRootPath + "valid-v2.0/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        Files.copy(sourceTestdata, tmpTargetConfig);

        ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

        ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_0);

        sut.upgradeConfigurationToLatestVersion(tempFolder.getRoot().toPath(), false);
        assertThat(tmpTargetConfig.toPath().resolveSibling("context.bak.xml").toFile()).exists()
            .hasSameContentAs(sourceTestdata);

        version = sut.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.v2_1);

        XMLUnit.setIgnoreWhitespace(true);
        new XMLTestCase() {
        }.assertXMLEqual(
            new FileReader(testFileRootPath + "valid-v2.1/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME),
            new FileReader(tmpTargetConfig));
    }

    /**
     * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
     * @throws Exception
     *             test fails
     * @author mbrunnli (Jun 22, 2015)
     */
    @Test
    public void testCorrectV2_1SchemaDetection() throws Exception {

        // preparation
        File targetConfig = new File(testFileRootPath + "valid-v2.1");

        ContextConfigurationVersion version =
            new ContextConfigurationUpgrader().resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
        assertThat(version).isEqualTo(ContextConfigurationVersion.v2_1);
    }
}
