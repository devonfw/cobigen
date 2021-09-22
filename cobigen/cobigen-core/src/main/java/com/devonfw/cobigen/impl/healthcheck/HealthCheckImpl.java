package com.devonfw.cobigen.impl.healthcheck;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.HealthCheckReport;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.devonfw.cobigen.impl.exceptions.BackupFailedException;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This is the implementation of the HealthCheck. It can upgrade the context configuration and the templates
 * configuration.
 */
public class HealthCheckImpl implements HealthCheck {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(HealthCheckImpl.class);

  /** HealthCheckReport created by this HealthCheck */
  private HealthCheckReport healthCheckReport = new HealthCheckReport();

  @Override
  public HealthCheckReport upgradeContextConfiguration(Path configurationFolder, BackupPolicy backupPolicy)
      throws BackupFailedException {

    ContextConfigurationUpgrader contextConfigurationUpgrader = new ContextConfigurationUpgrader();
    contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, backupPolicy);
    return this.healthCheckReport;
  }

  @Override
  public HealthCheckReport upgradeTemplatesConfiguration(Path templatesConfigurationFolder, BackupPolicy backupPolicy) {

    LOG.info("Upgrade of the templates configuration in '{}' triggered.", templatesConfigurationFolder);
    System.out.println(templatesConfigurationFolder.toString());

    TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
    try {
      templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(templatesConfigurationFolder, backupPolicy);
      LOG.info("Upgrade finished successfully.");
    } catch (BackupFailedException e) {
      this.healthCheckReport.addError(e);
      if (containsAnyExceptionOfClass(BackupFailedException.class)) {
        templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(templatesConfigurationFolder,
            BackupPolicy.NO_BACKUP);
        LOG.info("Upgrade finished successfully but without backup.");
      } else {
        this.healthCheckReport.addError(new CobiGenRuntimeException("Upgrade aborted"));
        LOG.info("Upgrade aborted.");
      }
    }
    return this.healthCheckReport;
  }

  /**
   * @param typeToBeFound the type to find
   * @return true if any exception in the list of errors of the health check is equal to or subtype of the parameter
   */
  private boolean containsAnyExceptionOfClass(Class<? extends RuntimeException> typeToBeFound) {

    return this.healthCheckReport.getErrors().stream().anyMatch(e -> typeToBeFound.isAssignableFrom(e.getClass()));
  }

  @Override
  public HealthCheckReport upgradeAllConfigurations(Path contextConfigurationPath, BackupPolicy backupPolicy) {

    try {
      upgradeContextConfiguration(contextConfigurationPath, backupPolicy);
    } catch (BackupFailedException e) {
      upgradeContextConfiguration(contextConfigurationPath, BackupPolicy.NO_BACKUP);
    }

    ContextConfiguration contextConfiguration = new ContextConfiguration(contextConfigurationPath);
    List<String> expectedTemplatesConfigurations = new ArrayList<>();
    Set<String> hasConfiguration = Sets.newHashSet();
    Map<String, Path> upgradeableConfigurations = this.healthCheckReport.getUpgradeableConfigurations();

    for (Trigger t : contextConfiguration.getTriggers()) {
      expectedTemplatesConfigurations.add(t.getTemplateFolder());
      hasConfiguration.add(t.getTemplateFolder());
    }
    this.healthCheckReport.setHasConfiguration(hasConfiguration);
    upgradeableConfigurations.put("TempOne", contextConfigurationPath.resolve("TempOne"));
    this.healthCheckReport.setUpgradeableConfigurations(upgradeableConfigurations);

    if (expectedTemplatesConfigurations.containsAll(this.healthCheckReport.getHasConfiguration())) {
      for (final String key : expectedTemplatesConfigurations) {
        if (this.healthCheckReport.getUpgradeableConfigurations().containsKey(key)) {
          upgradeTemplatesConfiguration(this.healthCheckReport.getUpgradeableConfigurations().get(key), backupPolicy);
        }
      }
    } else {
      LOG.error("Expected template configuration does not equal the actual template configuration");
      throw new CobiGenRuntimeException("Update of the templates configuration was not successful, please retry");
    }
    return this.healthCheckReport;
  }

  @Override
  public HealthCheckReport perform(Path configurationPath) {

    // 1. Get configuration resources
    // determine expected template configurations to be defined
    ContextConfiguration contextConfiguration = new ContextConfiguration(configurationPath);
    List<String> expectedTemplatesConfigurations = Lists.newArrayList();
    Set<String> hasConfiguration = Sets.newHashSet();
    Set<String> isAccessible = Sets.newHashSet();
    Map<String, Path> upgradeableConfigurations = Maps.newHashMap();
    Set<String> upToDateConfigurations = Sets.newHashSet();
    Path pathForCobigenTemplates = null;
    for (Trigger t : contextConfiguration.getTriggers()) {
      expectedTemplatesConfigurations.add(t.getTemplateFolder());
    }
    // 2. Determine current state
    TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
    pathForCobigenTemplates = configurationPath.resolve("src" + File.separator + "main" + File.separator + "templates");
    for (String expectedTemplateFolder : expectedTemplatesConfigurations) {
      if (Files.exists(pathForCobigenTemplates)) {
        String configPath = (configurationPath + File.separator + "src" + File.separator + "main" + File.separator
            + "templates").toString();
        hasConfiguration.add(configPath);
        isAccessible.add(configPath);
        Path expectedTemplateFolderForResolvedVer = pathForCobigenTemplates
            .resolve(expectedTemplateFolder.replace("/", File.separator).toString());
        TemplatesConfigurationVersion resolvedVersion = templateConfigurationUpgrader
            .resolveLatestCompatibleSchemaVersion(expectedTemplateFolderForResolvedVer);
        if (resolvedVersion != null) {
          if (resolvedVersion != TemplatesConfigurationVersion.getLatest()) {
            upgradeableConfigurations.put(expectedTemplateFolder, pathForCobigenTemplates);
          } else {
            upToDateConfigurations.add(expectedTemplateFolder);
          }
        }

      } else {

        Path templateFolder = configurationPath.resolve(expectedTemplateFolder);
        Path templatesConfigurationPath = templateFolder.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        File templatesConfigurationFile = templatesConfigurationPath.toFile();
        if (templatesConfigurationFile.exists()) {
          hasConfiguration.add(expectedTemplateFolder);
          if (templatesConfigurationFile.canWrite()) {
            isAccessible.add(expectedTemplateFolder);

            TemplatesConfigurationVersion resolvedVersion = templateConfigurationUpgrader
                .resolveLatestCompatibleSchemaVersion(templateFolder);
            if (resolvedVersion != null) {
              if (resolvedVersion != TemplatesConfigurationVersion.getLatest()) {
                upgradeableConfigurations.put(expectedTemplateFolder, templateFolder);
              } else {
                upToDateConfigurations.add(expectedTemplateFolder);
              }
            }
          }
        }

      }
    }

    this.healthCheckReport.setExpectedTemplatesConfigurations(expectedTemplatesConfigurations);
    this.healthCheckReport.setHasConfiguration(hasConfiguration);
    this.healthCheckReport.setIsAccessible(isAccessible);
    this.healthCheckReport.setUpgradeableConfigurations(upgradeableConfigurations);
    this.healthCheckReport.setUpToDateConfigurations(upToDateConfigurations);

    return this.healthCheckReport;
  }

  @Override
  public HealthCheckReport perform() {

    URI templatesJarFile = ConfigurationFinder.findTemplatesLocation();
    HealthCheckReport report = null;

    Path fileSystemPath = FileSystemUtil.createFileSystemDependentPath(templatesJarFile);
    report = perform(fileSystemPath);
    return report;
  }

}
