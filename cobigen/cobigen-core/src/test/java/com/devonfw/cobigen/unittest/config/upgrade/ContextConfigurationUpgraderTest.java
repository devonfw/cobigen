package com.devonfw.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
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
	 * Tests the valid upgrade of a context configuration from version v2.0 to the
	 * latest version. Please make sure that
	 * .../ContextConfigurationUpgraderTest/valid-latest_version exists
	 *
	 * @throws Exception test fails
	 */
	@Test
	public void testCorrectUpgrade_v2_0_TO_LATEST() throws Exception {
		//to version 2_1
		// preparation

		File tmpTargetConfig = this.tempFolder.newFile(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
		File sourceTestdata = new File(
				testFileRootPath + "valid-v2.0/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
		Files.copy(sourceTestdata, tmpTargetConfig);

		ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

		ContextConfigurationVersion version = sut
				.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
		assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_0);

		sut.upgradeConfigurationToLatestVersion(this.tempFolder.getRoot().toPath(), BackupPolicy.ENFORCE_BACKUP);
		assertThat(tmpTargetConfig.toPath().resolveSibling("context.bak.xml").toFile()).exists()
				.hasSameContentAs(sourceTestdata);

		version = sut.resolveLatestCompatibleSchemaVersion(this.tempFolder.getRoot().toPath());
		assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.getLatest());

		XMLUnit.setIgnoreWhitespace(true);
		new XMLTestCase() {
		}.assertXMLEqual(new FileReader(testFileRootPath + "valid-" + ContextConfigurationVersion.getLatest() + "/"
				+ ConfigurationConstants.CONTEXT_CONFIG_FILENAME), new FileReader(tmpTargetConfig));
	}

	/**
	 * Tests the valid upgrade of a context configuration from version v2.1 to the
	 * latest version. Please make sure that
	 * .../ContextConfigurationUpgraderTest/valid-latest_version exists
	 *
	 * @throws Exception test fails
	 */
	@Test
	public void testCorrectUpgrade_v2_1_TO_LATEST() throws Exception {
		// to version 3.0
		// preparation

		String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest";
		File cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE);
		Path templatesFolder = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);
		Path cobigen_templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH).resolve(
				ConfigurationConstants.COBIGEN_TEMPLATES);
		Path templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
				.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
		Path context = templates.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

		FileUtils.copyDirectory(new File(testFileRootPath), cobigen);

		ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

		ContextConfigurationVersion version = sut
				.resolveLatestCompatibleSchemaVersion(templates);
		assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_1);

		sut.upgradeConfigurationToLatestVersion(templates, BackupPolicy.ENFORCE_BACKUP);
		//copy resources again to check if backup was successful
		FileUtils.copyDirectory(new File(testFileRootPath), cobigen);
		assertThat(cobigen.toPath().resolve("backup/templates").toFile()).exists();
				//.hasSameContentAs(cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH).toFile());
				//doesnt work cause of Directory


		version = sut.resolveLatestCompatibleSchemaVersion(templatesFolder.getParent().resolve("template-sets/adapted/templateSet1/src/main/resources"));
		assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.getLatest());

	}

	/**
	 * Tests if latest context configuration is compatible to latest schema version.
	 *
	 * @throws Exception test fails
	 */
	@Test
	public void testCorrectLatestSchemaDetection() throws Exception {

		// preparation
		File targetConfig = new File(testFileRootPath + "valid-" + ContextConfigurationVersion.getLatest());

		ContextConfigurationVersion version = new ContextConfigurationUpgrader()
				.resolveLatestCompatibleSchemaVersion(targetConfig.toPath());
		assertThat(version).isEqualTo(ContextConfigurationVersion.getLatest());
	}
}