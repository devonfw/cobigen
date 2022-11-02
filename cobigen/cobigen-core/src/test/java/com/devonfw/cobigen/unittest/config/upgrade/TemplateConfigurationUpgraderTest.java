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
   * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v1_2_TO_2_1() throws Exception {

    // preparation
    File tmpTargetConfig = this.tempFolder.newFile(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    File sourceTestdata = new File(testFileRootPath + "valid-v1.2/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    Files.copy(sourceTestdata, tmpTargetConfig);

    TemplatesConfigurationVersion currentVersion = TemplatesConfigurationVersion.v1_2;
    TemplatesConfigurationVersion targetVersion = TemplatesConfigurationVersion.v2_1;

    TemplateConfigurationUpgrader sut = new TemplateConfigurationUpgrader();

    TemplatesConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath(),
        targetVersion);
    assertThat(version).as("Source Version").isEqualTo(currentVersion);

    sut.upgradeConfigurationToLatestVersion(this.tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP,
        targetVersion);
    assertThat(tmpTargetConfig.toPath().resolveSibling("templates.bak.xml").toFile()).exists()
        .hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath(), targetVersion);
    assertThat(version).as("Target version").isEqualTo(targetVersion);

    XMLUnit.setIgnoreWhitespace(true);
    try (
        FileReader vgl = new FileReader(
            testFileRootPath + "valid-" + targetVersion + "/" + ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        FileReader tmp = new FileReader(tmpTargetConfig)) {
      new XMLTestCase() {
      }.assertXMLEqual(vgl, tmp);
    }
  }

  /**
   * Tests if latest templates configuration is compatible to latest schema version.
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

  /**
   * Tests if v1.2 template configuration is compatible to v1.2 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV1_2SchemaDetection() throws Exception {

    // preparation
    TemplatesConfigurationVersion currentVersion = TemplatesConfigurationVersion.v1_2;
    File targetConfig = new File(testFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      TemplatesConfigurationVersion version = new TemplateConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v2.1 template configuration is compatible to v2.1 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_1SchemaDetection() throws Exception {

    // preparation
    TemplatesConfigurationVersion currentVersion = TemplatesConfigurationVersion.v2_1;
    File targetConfig = new File(testFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      TemplatesConfigurationVersion version = new TemplateConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v4.0 template configuration is compatible to v4.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV4_0SchemaDetection() throws Exception {

    // preparation
    TemplatesConfigurationVersion currentVersion = TemplatesConfigurationVersion.v4_0;
    File targetConfig = new File(testFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      TemplatesConfigurationVersion version = new TemplateConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v5.0 template configuration is compatible to v5.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV5_0SchemaDetection() throws Exception {

    // preparation
    TemplatesConfigurationVersion currentVersion = TemplatesConfigurationVersion.v5_0;
    File targetConfig = new File(testFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      TemplatesConfigurationVersion version = new TemplateConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

}
