package com.devonfw.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;
import com.google.common.io.Files;

/**
 * Test suite for {@link TemplateConfigurationUpgrader}
 */
public class TemplateConfigurationUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplatesConfigurationUpgraderTest/";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests the valid upgrade of a templates configuration from version v1.2 to the latest version.
   *
   * @throws Exception test fails
   */
  // @Test
  public void testCorrectUpgrade_v1_2_TO_LATEST() throws Exception {

    // preparation
    File tmpTargetConfig = this.tempFolder.newFile(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    File sourceTestdata = new File(testFileRootPath + "valid-v1.2/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    Files.copy(sourceTestdata, tmpTargetConfig);

    TemplateConfigurationUpgrader sut = new TemplateConfigurationUpgrader();

    TemplatesConfigurationVersion version = sut
        .resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Source Version").isEqualTo(TemplatesConfigurationVersion.v1_2);

    sut.upgradeConfigurationToLatestVersion(this.tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP);
    assertThat(tmpTargetConfig.toPath().resolveSibling("templates.bak.xml").toFile()).exists()
        .hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Target version").isEqualTo(TemplatesConfigurationVersion.getLatest());

    XMLUnit.setIgnoreWhitespace(true);
    try (FileReader vgl = new FileReader(testFileRootPath + "valid-" + TemplatesConfigurationVersion.getLatest() + "/"
        + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME); FileReader tmp = new FileReader(tmpTargetConfig)) {
      new XMLTestCase() {
      }.assertXMLEqual(vgl, tmp);
    }
  }

  /**
   * Tests the valid upgrade of a templates configuration from version v2.1 to the latest version.
   *
   * @throws Exception test fails
   */
  // @Test
  public void testCorrectUpgrade_v2_1_TO_LATEST() throws Exception {

    // preparation
    File tmpTargetConfig = this.tempFolder.newFile(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    File sourceTestdata = new File(testFileRootPath + "valid-v2.1/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    Files.copy(sourceTestdata, tmpTargetConfig);

    TemplateConfigurationUpgrader sut = new TemplateConfigurationUpgrader();

    TemplatesConfigurationVersion version = sut
        .resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Source Version").isEqualTo(TemplatesConfigurationVersion.v2_1);

    sut.upgradeConfigurationToLatestVersion(this.tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP);
    assertThat(tmpTargetConfig.toPath().resolveSibling("templates.bak.xml").toFile()).exists()
        .hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Target version").isEqualTo(TemplatesConfigurationVersion.getLatest());

    XMLUnit.setIgnoreWhitespace(true);
    try (FileReader vgl = new FileReader(testFileRootPath + "valid-" + TemplatesConfigurationVersion.getLatest() + "/"
        + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME); FileReader tmp = new FileReader(tmpTargetConfig)) {
      new XMLTestCase() {
      }.assertXMLEqual(vgl, tmp);
    }
  }

  /**
   * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectLatestSchemaDetection() throws Exception {

    // preparation
    File targetConfig = new File(testFileRootPath + "valid-" + TemplatesConfigurationVersion.getLatest());

    TemplatesConfigurationVersion version = new TemplateConfigurationUpgrader()
        .resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
    assertThat(version).isEqualTo(TemplatesConfigurationVersion.getLatest());
  }
}
