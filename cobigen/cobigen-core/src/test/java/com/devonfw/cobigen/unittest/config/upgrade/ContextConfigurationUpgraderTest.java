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
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;
import com.google.common.io.Files;

/**
 * Test suite for {@link ContextConfigurationUpgrader}
 */
public class ContextConfigurationUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/ContextConfigurationUpgraderTest/";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v2_0_TO_v2_1() throws Exception {

    // preparation
    File tmpTargetConfig = this.tempFolder.newFile(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    File sourceTestdata = new File(testFileRootPath + "valid-v2.0/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    Files.copy(sourceTestdata, tmpTargetConfig);

    ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

    ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_0);

    sut.upgradeConfigurationToLatestVersion(this.tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP);
    assertThat(tmpTargetConfig.toPath().resolveSibling("context.bak.xml").toFile()).exists()
        .hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
    assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.v2_1);

    XMLUnit.setIgnoreWhitespace(true);
    new XMLTestCase() {
    }.assertXMLEqual(new FileReader(testFileRootPath + "valid-v2.1/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME),
        new FileReader(tmpTargetConfig));
  }

  /**
   * Tests the valid upgrade of a templates configuration from version v1.2 to v2.1.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_1SchemaDetection() throws Exception {

    // preparation
    File targetConfig = new File(testFileRootPath + "valid-v2.1");

    ContextConfigurationVersion version = new ContextConfigurationUpgrader()
        .resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
    assertThat(version).isEqualTo(ContextConfigurationVersion.v2_1);
  }
}
