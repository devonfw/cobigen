package com.capgemini.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.constants.BackupPolicy;
import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.unittest.config.common.AbstractUnitTest;
import com.google.common.io.Files;

/**
 * Test suite for {@link TemplateConfigurationUpgrader}
 */
public class TemplateConfigurationUpgraderTest extends AbstractUnitTest {

    /** Root path to all resources used in this test case */
    private static String testFileRootPath =
        "src/test/resources/testdata/unittest/config/upgrade/TemplatesConfigurationUpgraderTest/";

    /** JUnit Rule to create and automatically cleanup temporarily files/folders */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectUpgrade_v1_2_TO_v2_1() throws Exception {

        // preparation
        File tmpTargetConfig = tempFolder.newFile(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        File sourceTestdata =
            new File(testFileRootPath + "valid-v1.2/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        Files.copy(sourceTestdata, tmpTargetConfig);

        TemplateConfigurationUpgrader sut = new TemplateConfigurationUpgrader();

        TemplatesConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Source Version").isEqualTo(TemplatesConfigurationVersion.v1_2);

        sut.upgradeConfigurationToLatestVersion(tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP);
        assertThat(tmpTargetConfig.toPath().resolveSibling("templates.bak.xml").toFile()).exists()
            .hasSameContentAs(sourceTestdata);

        version = sut.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Target version").isEqualTo(TemplatesConfigurationVersion.v4_0);

        XMLUnit.setIgnoreWhitespace(true);
        new XMLTestCase() {
        }.assertXMLEqual(
            new FileReader(testFileRootPath + "valid-v2.1/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME),
            new FileReader(tmpTargetConfig));
    }

    /**
     * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectV2_1SchemaDetection() throws Exception {

        // preparation
        File targetConfig = new File(testFileRootPath + "valid-v2.1");

        TemplatesConfigurationVersion version =
            new TemplateConfigurationUpgrader().resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
        assertThat(version).isEqualTo(TemplatesConfigurationVersion.v4_0);
    }
}
