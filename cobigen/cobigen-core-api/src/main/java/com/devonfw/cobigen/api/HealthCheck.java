package com.devonfw.cobigen.api;

import java.nio.file.Path;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.to.HealthCheckReport;

/**
 * The HealthCheck upgrades the context configuration and the templates configuration.
 */
@ExceptionFacade
public interface HealthCheck {

    /**
     * Upgrades the context configuration file.
     *
     * @param contextConfiguration
     *            the path to the context configuration file
     * @param backupPolicy
     *            the {@link BackupPolicy} that should be used for the function
     * @return the {@link HealthCheckReport} of this HealthCheck
     */
    HealthCheckReport upgradeContextConfiguration(Path contextConfiguration, BackupPolicy backupPolicy);

    /**
     * Upgrades a specific template configuration file.
     *
     * @param templatesConfigurationFolder
     *            the path to the templates configuration
     * @param backupPolicy
     *            the {@link BackupPolicy} that should be used for the function
     * @return the {@link HealthCheckReport} of this HealthCheck
     */
    HealthCheckReport upgradeTemplatesConfiguration(Path templatesConfigurationFolder, BackupPolicy backupPolicy);

    /**
     * Upgrades both the context configuration and the templates configuration. For future usage in other
     * plug-ins.
     *
     * @param configurationPath
     *            the path in which the files are that should be upgraded. Further changes of the path might
     *            be necessary for further adaption to different plug-ins.
     * @param backupPolicy
     *            the {@link BackupPolicy} that should be used for the function
     * @return the {@link HealthCheckReport} created by the HealthCheck
     */
    HealthCheckReport upgradeAllConfigurations(Path configurationPath, BackupPolicy backupPolicy);

    /**
     * Performs a health check of the CobiGen plug-in.
     *
     * @param configurationPath
     *            the path in which the files are that should be checked regarding their version and validity.
     *            Further changes of the path might be necessary for further adaption to different plug-ins.
     * @return the {@link HealthCheckReport} created by the HealthCheck
     */
    HealthCheckReport perform(Path configurationPath);
    
    /**
     * Performs a health check of the CobiGen plug-in and automatically downwloads the latest CobiGen_Templates jar.
     *
     * @return the {@link HealthCheckReport} created by the HealthCheck
     */
    HealthCheckReport perform();

}
