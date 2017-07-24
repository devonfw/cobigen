package com.capgemini.cobigen.impl.healthcheck;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.HealthCheck;
import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.to.HealthCheckReport;
import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.capgemini.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.impl.exceptions.BackupFailedException;
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
    public HealthCheckReport upgradeContextConfiguration(Path configurationFolder, boolean flag)
        throws BackupFailedException {
        ContextConfigurationUpgrader contextConfigurationUpgrader = new ContextConfigurationUpgrader();
        if (flag) {
            contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, true);
        } else {
            contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, false);
        }
        return healthCheckReport;
    }

    @Override
    public HealthCheckReport upgradeTemplatesConfiguration(Path templatesConfigurationFolder) {
        LOG.info("Upgrade of the templates configuration in '{}' triggered.", templatesConfigurationFolder);

        TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
        try {
            try {
                templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(templatesConfigurationFolder, false);
                LOG.info("Upgrade finished successfully.");
            } catch (BackupFailedException e) {
                healthCheckReport.addError(e);
                if (healthCheckReport.getErrors().contains(BackupFailedException.class)) {
                    templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(templatesConfigurationFolder,
                        true);
                    LOG.info("Upgrade finished successfully but without backup.");
                } else {
                    healthCheckReport.addError(new CobiGenRuntimeException("Upgrade aborted"));
                    LOG.info("Upgrade aborted.");
                }
            }
        } catch (RuntimeException e) {
            healthCheckReport.addError(e);
            healthCheckReport
                .addErrorMessages("An unexpected error occurred while upgrading the templates configuration");
            LOG.error("An unexpected error occurred while upgrading the templates configuration.", e);
        }
        return healthCheckReport;
    }

    @Override
    public HealthCheckReport upgradeAllTemplatesConfigurations(Path contextConfigurationPath) {
        try {
            upgradeContextConfiguration(contextConfigurationPath, false);
        } catch (BackupFailedException e) {
            upgradeContextConfiguration(contextConfigurationPath, true);
        }
        ContextConfiguration contextConfiguration = new ContextConfiguration(contextConfigurationPath);
        List<String> expectedTemplatesConfigurations = new ArrayList<>();
        for (Trigger t : contextConfiguration.getTriggers()) {
            expectedTemplatesConfigurations.add(t.getTemplateFolder());
        }
        if (expectedTemplatesConfigurations.toArray().equals(healthCheckReport.getHasConfiguration().toArray())) {
            for (final String key : expectedTemplatesConfigurations) {
                if (healthCheckReport.getUpgradeableConfigurations().containsKey(key)) {
                    upgradeTemplatesConfiguration(healthCheckReport.getUpgradeableConfigurations().get(key));
                }
            }
        } else {
            LOG.error("Expected template configuration doesn't equal the actual template configuration");
            throw new CobiGenRuntimeException("Update of the templates configuration was not successful, please retry");
        }
        return healthCheckReport;
    }

    @Override
    public HealthCheckReport perform(Path configurationPath) {
        try {
            // 1. Get configuration resources
            // determine expected template configurations to be defined
            ContextConfiguration contextConfiguration = new ContextConfiguration(configurationPath);
            List<String> expectedTemplatesConfigurations = Lists.newArrayList();
            for (Trigger t : contextConfiguration.getTriggers()) {
                expectedTemplatesConfigurations.add(t.getTemplateFolder());
            }

            // 2. Determine current state
            TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
            Set<String> hasConfiguration = Sets.newHashSet();
            Set<String> isAccessible = Sets.newHashSet();
            Map<String, Path> upgradeableConfigurations = Maps.newHashMap();
            Set<String> upToDateConfigurations = Sets.newHashSet();

            for (String expectedTemplateFolder : expectedTemplatesConfigurations) {
                Path templateFolder = configurationPath.resolve(expectedTemplateFolder);
                Path templatesConfigurationPath =
                    templateFolder.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
                File templatesConfigurationFile = templatesConfigurationPath.toFile();
                if (templatesConfigurationFile.exists()) {
                    hasConfiguration.add(expectedTemplateFolder);
                    if (templatesConfigurationFile.canWrite()) {
                        isAccessible.add(expectedTemplateFolder);

                        TemplatesConfigurationVersion resolvedVersion =
                            templateConfigurationUpgrader.resolveLatestCompatibleSchemaVersion(templateFolder);
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

            healthCheckReport.setExpectedTemplatesConfigurations(expectedTemplatesConfigurations);
            healthCheckReport.setHasConfiguration(hasConfiguration);
            healthCheckReport.setIsAccessible(isAccessible);
            healthCheckReport.setUpgradeableConfigurations(upgradeableConfigurations);
            healthCheckReport.setUpToDateConfigurations(upToDateConfigurations);

        } catch (RuntimeException e) {
            healthCheckReport.addError(e);
            healthCheckReport.addErrorMessages("An unexpected exception occurred.");
            LOG.error("An unexpected exception occurred.", e);
        }
        return healthCheckReport;
    }

}
