package com.capgemini.cobigen.unittest.healthcheck;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.api.constants.BackupPolicy;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.to.HealthCheckReport;
import com.capgemini.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.impl.healthcheck.HealthCheckImpl;

/**
 * Test suite for {@link HealthCheckImpl} class.
 */
public class HealthCheckTest {

    /**
     * Root Path where to test data is stored
     */
    private static final Path rootTestPath = new File("src/test/resources/testdata/unittest/HealthCheckTest").toPath();

    /**
     * Tempfolder used for running executionFolder.
     */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Testing if the upgrade to the new version has succeeded.
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeContextConfiguration() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulContextConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeContextConfiguration");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfContextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");
    }

    /**
     * test successful context configuration upgrade with no linked templates configurations
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeContextConfigurationWithNoTemplatesConfig() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulContextConfigWithNoTemps");
        Path executionFolder =
            tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeContextConfigurationWithNoTemplatesConfig");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfContextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");

    }

    /**
     * test report of one successful context configuration upgrade with at least two template configurations
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeContextConfigurationWithTwoTemplateConfigs() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulContextConfigWithTwoTemps");
        Path executionFolder =
            tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeContextConfigurationWithTwoTemps");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfContextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");
    }

    /**
     * Testing if the upgrade to the new version has succeeded in the Templates.
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeTemplatesConfiguration() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulTemplatesConfig/oldContextConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeTemplatesConfiguration");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeTemplatesConfiguration(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfTemplatesVersion(executionFolder, "2.1");
    }

    /**
     * Testing if the upgrade to the new version has succeeded in the Templates.
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeAll() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulUpgradeAll");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeAll");
        Path templateFolder = executionFolder.resolve("TempOne");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeAllConfigurations(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfContextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");
        assertThat(report).isOfTemplatesVersion(templateFolder, "2.1");
    }

    /**
     * Test Error Message when Context Configuration is incorrect.
     * @throws IOException
     *             if failing test fails
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testFailingContextConfigUpgrade() throws IOException {
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backuppolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingContextConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testFailingUpgradeContextConfig");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        healthcheck.upgradeContextConfiguration(executionFolder, backuppolicy);
    }

    /**
     * Testing Error Message for an Invalid Templates Configuration.
     * @throws IOException
     *             not thrown
     */
    @Test
    public void testFailingTemplatesConfigUpgrade() throws IOException {

        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backuppolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingTemplatesConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testFailingUpgradeTemplatesConfig");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeTemplatesConfiguration(executionFolder, backuppolicy);
        System.out.print(report.getErrors());
        assertThat(report.getErrors().toString())
            .contains("Templates Configuration does not match any current or legacy schema definitions.");
    }

    /**
     * test report of one failing context configuration upgrade with no linked templates. Version number
     * should stay the same.
     * @throws Exception
     *             if failing test fails
     */
    @Test
    public void testFailingUpgradeContextConfigurationWithNoTemplatesConfig() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backuppolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingContextConfigWithNoTemps");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testFailingUpgradeContextConfiguration");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backuppolicy);
        System.out.print(report.getErrors());
        assertThat(report).isOfContextVersion(executionFolder, "2.0");
    }

}
