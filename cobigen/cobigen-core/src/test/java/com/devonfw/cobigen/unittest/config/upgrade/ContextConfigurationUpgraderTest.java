package com.devonfw.cobigen.unittest.config.upgrade;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.TemplateSetConfigurationUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link ContextConfigurationUpgrader}
 */
public class ContextConfigurationUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String contextTestFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/ContextConfigurationUpgraderTest";

  private static String templateTestFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests the valid upgrade of a context configuration from version v2.0 to v2.1. Please make sure that
   * .../ContextConfigurationUpgraderTest/valid-v2.1 exists
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v2_0_TO_v_2_1() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_0;
    ContextConfigurationVersion targetVersion = ContextConfigurationVersion.v2_1;
    String currentVersionPath = "valid-v2.0";
    String targetVersionPath = "valid-v2.1";

    Path cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE).toPath();
    Path context = cobigen.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    File sourceTestdata = new File(contextTestFileRootPath + File.separator + currentVersionPath + File.separator
        + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

    FileUtils.copyDirectory(new File(contextTestFileRootPath + File.separator + currentVersionPath), cobigen.toFile());

    ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

    ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(context, currentVersion);
    assertThat(version).as("Source Version").isEqualTo(currentVersion);

    sut.upgradeConfigurationToLatestVersion(cobigen, BackupPolicy.ENFORCE_BACKUP, targetVersion);
    assertThat(cobigen.resolve("context.bak.xml").toFile()).exists().hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(cobigen, targetVersion);
    assertThat(version).as("Target version").isEqualTo(targetVersion);

    XMLUnit.setIgnoreWhitespace(true);
    try (
        Reader firstReader = new FileReader(contextTestFileRootPath + File.separator + targetVersionPath
            + File.separator + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        Reader secondReader = new FileReader(context.toFile())) {
      new XMLTestCase() {
      }.assertXMLEqual(firstReader, secondReader);
    }
  }

  /**
   * Tests the valid upgrade of a context configuration from version v2.1 to template set v1.This also includes a merge
   * of a templateConfiguration v5.0 into a templateSetConfigration
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v2_1_TO_TSv1_0() throws Exception {

    File cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, cobigen.toPath().toString()).execute(() -> {
      // preparation
      ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
      ContextConfigurationVersion latestVersion = ContextConfigurationVersion.v3_0;
      TemplateSetConfigurationVersion targetVersion = TemplateSetConfigurationVersion.v1_0;

      String currentVersionPath = "valid-2.1";
      String targetVersionPathTS = "valid-v1.0";

      Path templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
          .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      Path context = templates.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

      FileUtils.copyDirectory(new File(templateTestFileRootPath + File.separator + currentVersionPath), cobigen);

      ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

      ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(context, currentVersion);
      assertThat(version).as("Source Version").isEqualTo(currentVersion);

      sut.upgradeConfigurationToLatestVersion(templates, BackupPolicy.ENFORCE_BACKUP, latestVersion);

      Path newTemplatesLocation = cobigen.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
          .resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Path backupContextPath = cobigen.toPath().resolve("backup")
          .resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
          .resolve(ConfigurationConstants.COBIGEN_TEMPLATES).resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

      assertThat(backupContextPath).exists();
      assertThat(templates).doesNotExist();

      for (String templateName : newTemplatesLocation.toFile().list()) {
        Path newContextPath = newTemplatesLocation.resolve(templateName)
            .resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER);

        TemplateSetConfigurationUpgrader tscU = new TemplateSetConfigurationUpgrader();
        TemplateSetConfigurationVersion tscVersion = tscU.resolveLatestCompatibleSchemaVersion(newContextPath,
            targetVersion);
        assertThat(tscVersion).as("Target version").isEqualTo(targetVersion);

        newContextPath = newContextPath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
        XMLUnit.setIgnoreWhitespace(true);
        try (
            Reader firstReader = new FileReader(contextTestFileRootPath + File.separator + targetVersionPathTS
                + File.separator + templateName + File.separator + ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
            Reader secondReader = new FileReader(newContextPath.toFile())) {
          new XMLTestCase() {
          }.assertXMLEqual(firstReader, secondReader);
        }
      }
    });
  }

  /**
   * Tests if v2.0 context configuration is compatible to v2.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_0SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v2.1 context configuration is compatible to v2.1 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_1SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v6.0 context configuration is compatible to v6.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV3_0SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v3_0;
    File context = new File(contextTestFileRootPath + "/valid-" + currentVersion + "/context.xml");

    ContextConfigurationVersion version = new ContextConfigurationUpgrader()
        .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
    assertThat(version).isEqualTo(currentVersion);
  }

  /**
   * Tests if v2.1 context configuration schema is not compatible to v6.0 configuration file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testV2_1IsIncompatibleToV3_0Schema() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
    ContextConfigurationVersion targetVersion = ContextConfigurationVersion.v3_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath());
      assertThat(version).isNotEqualTo(targetVersion);
    }
  }
}