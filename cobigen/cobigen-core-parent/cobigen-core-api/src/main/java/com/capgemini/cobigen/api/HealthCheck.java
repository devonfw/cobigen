package com.capgemini.cobigen.api;

import java.nio.file.Path;

import com.capgemini.cobigen.api.annotation.ExceptionFacade;
import com.capgemini.cobigen.api.to.HealthCheckReport;

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
     * @param flag
     *            {@code false} if the method should try a context upgrade with backup or {@code true} if no
     *            backup should be made
     * @return the {@link HealthCheckReport} of this HealthCheck
     */
    HealthCheckReport upgradeContextConfiguration(Path contextConfiguration, boolean flag);

    /**
     * Upgrades a specific template configuration file.
     *
     * @param templatesConfigurationFolder
     *            the path to the templates configuration
     *
     * @return the {@link HealthCheckReport} of this HealthCheck
     */
    HealthCheckReport upgradeTemplatesConfiguration(Path templatesConfigurationFolder);

    /**
     * Upgrades both the context configuration and the templates configuration. For future usage in other
     * plug-ins.
     *
     * @param configurationPath
     *            the path in which the files are that should be upgraded. Further changes of the path might
     *            be necessary for further adaption to different plug-ins.
     *
     * @return the {@link HealthCheckReport} created by the HealthCheck
     */
    HealthCheckReport upgradeAllTemplatesConfigurations(Path configurationPath);

    /**
     * Performs a health check of the CobiGen plug-in.
     *
     * @param configurationPath
     *            the path in which the files are that should be checked regarding their version and validity.
     *            Further changes of the path might be necessary for further adaption to different plug-ins.
     * @return the {@link HealthCheckReport} created by the HealthCheck
     */
    HealthCheckReport perform(Path configurationPath);

}
