package com.capgemini.cobigen.unittest.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static com.capgemini.cobigen.test.assertj.*;

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
import com.capgemini.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.impl.healthcheck.HealthCheckImpl;

/**
 * Test suite for {@link HealthCheckImpl} class.
 */
public class HealthCheckTest {

    /**
     * Root Path where to test data is stored
     */
    private static final Path rootTestPath =
        new File("src/test/resources/testdata/unittest/healthcheck/HealthCheckTest").toPath();

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
        assertThat(report).isOfConextVersion(executionFolder,
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
        assertThat(report).isOfConextVersion(executionFolder,
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
        assertThat(report).isOfConextVersion(executionFolder,
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
        Path configurationFolder = rootTestPath.resolve("successfulTemplatesConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeTemplatesConfiguration");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeTemplatesConfiguration(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfConextVersion(executionFolder,
            TemplatesConfigurationVersion.getLatest().getFloatRepresentation() + "");
    }

    /**
     * Test if UpgradeAll method upgrades all.
     * @throws Exception
     *             not thrown
     */
    @Test
    public void testSuccessfulUpgradeAllConfigurations() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("successfulUpgradeAll");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testSuccessfulUpgradeAll");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeAllConfigurations(executionFolder, backupPolicy);
        // assert
        assertThat(report).isSuccessful();
        assertThat(report).isOfConextVersion(executionFolder,
            TemplatesConfigurationVersion.getLatest().getFloatRepresentation() + "");
        assertThat(report).isOfConextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");
    }

    /**
     * Test Error Message when Context Configuration is incorrect.
     * @throws IOException
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testFailingContextConfigUpgrade() throws IOException {
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backuppolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingContextConfig");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testFailingUpgradeContextConfig");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backuppolicy);
    }

    /**
     * Templates configuration is read-only and therefore should throw an Invalid Config error.
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
     * test failing context configuration upgrade with at least two template configurations. Version should
     * stay the same when failed.
     * @throws Exception
     */
    @Test
    public void testFailingUpgradeContextConfigurationWithTwoTemplateConfigs() throws Exception {

        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backuppolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingContextConfigWithTwoTemps");
        Path executionFolder =
            tempFolder.getRoot().toPath().resolve("testFailingUpgradeContextConfigurationWithTwoTemps");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeContextConfiguration(executionFolder, backuppolicy);
        assertThat(report).isOfConextVersion(executionFolder,
            ContextConfigurationVersion.getLatest().getFloatRepresentation() + "");
    }

    /**
     * test report of one failing context configuration upgrade with no linked templates configuration.
     * Version number should stay the same.
     * @throws Exception
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
        assertThat(report).isOfConextVersion(executionFolder, "2.0");
    }

    /**
     * @throws Exception
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testFailingUpgradeAllConfigurations() throws Exception {
        // arrange
        HealthCheckImpl healthcheck = new HealthCheckImpl();
        BackupPolicy backupPolicy = BackupPolicy.ENFORCE_BACKUP;
        Path configurationFolder = rootTestPath.resolve("failingUpgradeAll");
        Path executionFolder = tempFolder.getRoot().toPath().resolve("testFailingUpgradeAll");
        FileUtils.copyDirectory(configurationFolder.toFile(), executionFolder.toFile());
        // act
        HealthCheckReport report = healthcheck.upgradeAllConfigurations(executionFolder, backupPolicy);
        // assert
        System.out.print("Erik Report outcome" + report.getErrors());
        // Test CobigenRunntimeException Update of the templates configuration was not successful, please
        // retry
        // test failing of template
    }

}
