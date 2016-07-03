package com.capgemini.cobigen.eclipse.healthcheck;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.ContextConfiguration;
import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The Advanced Health Check checks for the validity of template configurations.
 * @author mbrunnli (Jun 24, 2015)
 */
public class AdvancedHealthCheck {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AdvancedHealthCheck.class);

    /** Commonly used dialog title for the Advanced Health Check */
    static final String COMMON_DIALOG_TITLE = "Advanced Health Check";

    /**
     * Executes the Advanced Health Check.
     * @author mbrunnli (Jun 24, 2015)
     */
    public void execute() {

        try {
            // 1. Get configuration resources
            Path configurationProjectPath =
                Paths.get(ResourcesPluginUtil.getGeneratorConfigurationProject().getLocationURI());
            // determine expected template configurations to be defined
            ContextConfiguration contextConfiguration = new ContextConfiguration(configurationProjectPath);
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
                Path templateFolder = configurationProjectPath.resolve(expectedTemplateFolder);
                Path templatesConfigurationPath =
                    templateFolder.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
                File templatesConfigurationFile = templatesConfigurationPath.toFile();
                if (templatesConfigurationFile.exists()) {
                    hasConfiguration.add(expectedTemplateFolder);
                    if (templatesConfigurationFile.canWrite()) {
                        isAccessible.add(expectedTemplateFolder);

                        TemplatesConfigurationVersion resolvedVersion =
                            templateConfigurationUpgrader
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

            // 3. Show current status to the user
            AdvancedHealthCheckDialog advancedHealthCheckDialog =
                new AdvancedHealthCheckDialog(expectedTemplatesConfigurations, hasConfiguration,
                    isAccessible, upgradeableConfigurations, upToDateConfigurations);
            advancedHealthCheckDialog.setBlockOnOpen(false);
            advancedHealthCheckDialog.open();

        } catch (CoreException e) {
            PlatformUIUtil.openErrorDialog(COMMON_DIALOG_TITLE,
                "An eclipse internal exception occurred while retrieving the configuration folder resource.",
                e);
            LOG.error(
                "An eclipse internal exception occurred while retrieving the configuration folder resource.",
                e);
        }
    }
}
