package com.capgemini.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.config.upgrade.version.TemplatesConfigurationVersion;
import com.google.common.io.Files;

/**
 * Test suite for {@link TemplateConfigurationUpgrader}
 * @author mbrunnli (Jun 22, 2015)
 */
public class TemplateConfigurationUpgraderTest {

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
     * @author mbrunnli (Jun 22, 2015)
     */
    @Test
    public void testCorrectUpgrade_v1_2_TO_v2_1() throws Exception {

        // preparation
        File tmpTargetConfig = tempFolder.newFile("templates.xml");
        File sourceTestdata = new File(testFileRootPath + "valid-v1.2/templates.xml");
        Files.copy(sourceTestdata, tmpTargetConfig);

        TemplatesConfigurationVersion version =
            TemplateConfigurationUpgrader.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Source Version").isEqualTo(TemplatesConfigurationVersion.v1_2);

        TemplateConfigurationUpgrader.upgradeTemplatesConfigurationToLatestVersion(tempFolder.getRoot()
            .toPath(), false);
        assertThat(tmpTargetConfig.toPath().resolveSibling("templates.bak.xml").toFile()).exists()
            .hasSameContentAs(sourceTestdata);

        version =
            TemplateConfigurationUpgrader.resolveLatestCompatibleSchemaVersion(tempFolder.getRoot().toPath());
        assertThat(version).as("Target version").isEqualTo(TemplatesConfigurationVersion.v2_1);

        XMLUnit.setIgnoreWhitespace(true);
        new XMLTestCase() {
        }.assertXMLEqual(new FileReader(testFileRootPath + "valid-v2.1/templates.xml"), new FileReader(
            tmpTargetConfig));
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

        TemplatesConfigurationVersion version =
            TemplateConfigurationUpgrader.resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
        assertThat(version).isEqualTo(TemplatesConfigurationVersion.v2_1);
    }
}
