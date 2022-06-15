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
	 * Tests the valid upgrade of a context configuration from version v2.0 to the
	 * latest version. Please make sure that
	 * .../ContextConfigurationUpgraderTest/valid-latest_version exists
	 *
	 * @throws Exception test fails
	 */
	@Test
	public void testCorrectUpgrade_v2_0_TO_LATEST() throws Exception {
		// preparation
		File cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE);
		Path templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
				.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
		Path context = templates.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

		FileUtils.copyDirectory(new File(templateTestFileRootPath+"/valid-2.0"), cobigen);

		ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

		ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(templates);
		assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_1);

		sut.upgradeConfigurationToLatestVersion(templates, BackupPolicy.ENFORCE_BACKUP);
		// copy resources again to check if backup was successful
		String pom = "templates/CobiGen_Templates/pom.xml";
		FileUtils.copyDirectory(new File(templateTestFileRootPath+"/valid-2.0"), cobigen);
		assertThat(cobigen.toPath().resolve("backup").resolve(pom).toFile()).exists()
				.hasSameContentAs(cobigen.toPath().resolve(pom).toFile());

		Path newTemplatesLocation = cobigen.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
				.resolve(ConfigurationConstants.ADAPTED_FOLDER);
		Path backupContextPath = cobigen.toPath().resolve("backup")
				.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
				.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
				.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

		assertThat(backupContextPath.toFile()).exists().hasSameContentAs(context.toFile());

		for (String s : newTemplatesLocation.toFile().list()) {
			Path newContextPath = newTemplatesLocation.resolve(s + "/" + "src/main/resources");

			version = sut.resolveLatestCompatibleSchemaVersion(newContextPath);
			assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.getLatest());

			newContextPath = newContextPath.resolve("context.xml");
			XMLUnit.setIgnoreWhitespace(true);
			new XMLTestCase() {
			}.assertXMLEqual(
					new FileReader(contextTestFileRootPath + "/valid-" + ContextConfigurationVersion.getLatest() + "/"
							+ s + "/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME),
					new FileReader(newContextPath.toFile()));

		}
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

		// preparation
		File cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE);
		Path templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
				.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
		Path context = templates.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

		FileUtils.copyDirectory(new File(templateTestFileRootPath+"/valid-2.1"), cobigen);

		ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

		ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(templates);
		assertThat(version).as("Source Version").isEqualTo(ContextConfigurationVersion.v2_1);

		sut.upgradeConfigurationToLatestVersion(templates, BackupPolicy.ENFORCE_BACKUP);
		// copy resources again to check if backup was successful
		String pom = "templates/CobiGen_Templates/pom.xml";
		FileUtils.copyDirectory(new File(templateTestFileRootPath+"/valid-2.1"), cobigen);
		assertThat(cobigen.toPath().resolve("backup").resolve(pom).toFile()).exists()
				.hasSameContentAs(cobigen.toPath().resolve(pom).toFile());

		Path newTemplatesLocation = cobigen.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
				.resolve(ConfigurationConstants.ADAPTED_FOLDER);
		Path backupContextPath = cobigen.toPath().resolve("backup")
				.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
				.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
				.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

		assertThat(backupContextPath.toFile()).exists().hasSameContentAs(context.toFile());

		for (String s : newTemplatesLocation.toFile().list()) {
			Path newContextPath = newTemplatesLocation.resolve(s + "/" + "src/main/resources");

			version = sut.resolveLatestCompatibleSchemaVersion(newContextPath);
			assertThat(version).as("Target version").isEqualTo(ContextConfigurationVersion.getLatest());

			newContextPath = newContextPath.resolve("context.xml");
			XMLUnit.setIgnoreWhitespace(true);
			new XMLTestCase() {
			}.assertXMLEqual(
					new FileReader(contextTestFileRootPath + "/valid-" + ContextConfigurationVersion.getLatest() + "/"
							+ s + "/" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME),
					new FileReader(newContextPath.toFile()));

		}
	}

	/**
	 * Tests if latest context configuration is compatible to latest schema version.
	 *
	 * @throws Exception test fails
	 */
	@Test
	public void testCorrectLatestSchemaDetection() throws Exception {

		// preparation
		File targetConfig = new File(contextTestFileRootPath + "/valid-" + ContextConfigurationVersion.getLatest());
		for( File context: targetConfig.listFiles()) {
			ContextConfigurationVersion version = new ContextConfigurationUpgrader()
					.resolveLatestCompatibleSchemaVersion(context.toPath());
			assertThat(version).isEqualTo(ContextConfigurationVersion.getLatest());
		}
	}
}