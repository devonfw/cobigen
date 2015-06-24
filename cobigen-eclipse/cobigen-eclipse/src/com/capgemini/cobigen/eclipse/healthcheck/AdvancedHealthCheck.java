package com.capgemini.cobigen.eclipse.healthcheck;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.config.upgrade.version.TemplatesConfigurationVersion;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.google.common.collect.Maps;

/**
 *
 * @author mbrunnli (Jun 24, 2015)
 */
public class AdvancedHealthCheck {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AdvancedHealthCheck.class);

    private static final String COMMON_DIALOG_TITLE = "Advanced Health Check";

    public void execute() {

        try {
            // 1. Get configuration resources
            Path configurationProjectPath =
                Paths.get(ResourcesPluginUtil.getGeneratorConfigurationProject().getLocationURI());
            File[] directoryChildren = configurationProjectPath.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            // 2. Determine current state
            TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
            Map<String, Boolean> hasConfiguration = Maps.newTreeMap();
            Map<String, Boolean> isAccessible = Maps.newHashMap();
            Map<String, Path> upgradeableConfigurations = Maps.newHashMap();

            for (File dir : directoryChildren) {
                Path templatesConfigurationPath =
                    dir.toPath().resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
                File templatesConfigurationFile = templatesConfigurationPath.toFile();
                String key =
                    templatesConfigurationPath.subpath(templatesConfigurationPath.getNameCount() - 2,
                        templatesConfigurationPath.getNameCount()).toString();
                if (templatesConfigurationFile.exists()) {
                    hasConfiguration.put(key, true);
                    if (templatesConfigurationFile.canWrite()) {
                        isAccessible.put(key, true);

                        TemplatesConfigurationVersion resolvedVersion =
                            templateConfigurationUpgrader.resolveLatestCompatibleSchemaVersion(dir.toPath());
                        if (resolvedVersion != null) {
                            upgradeableConfigurations.put(key, dir.toPath());
                        }
                    }
                }
            }

            // 3. Show current status to the user

            // ResourcesPluginUtil.getGeneratorConfigurationProject()
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
