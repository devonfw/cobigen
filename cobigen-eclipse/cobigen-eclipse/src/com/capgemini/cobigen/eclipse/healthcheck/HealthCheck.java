package com.capgemini.cobigen.eclipse.healthcheck;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.capgemini.cobigen.impl.exceptions.BackupFailedException;

/**
 * This class implements the Health Check to provide more information about the current status of CobiGen and
 * potentially why it cannot be used with the current selection.
 * @author mbrunnli (Jun 24, 2015)
 */
public class HealthCheck {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheck.class);

    /**
     * Executes the simple health check, checking configuration project existence, validity of context
     * configuration, as well as validity of the current workbench selection as generation input.
     */
    public void execute() {

        String firstStep = "1. CobiGen configuration project '" + ResourceConstants.CONFIG_PROJECT_NAME + "'... ";
        String secondStep =
            "\n2. CobiGen context configuration '" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME + "'... ";

        String healthyCheckMessage = "";
        IProject generatorConfProj = null;

        try {
            // check configuration project existence
            generatorConfProj = ResourcesPluginUtil.getGeneratorConfigurationProject();

            // refresh and check context configuration
            ResourcesPluginUtil.refreshConfigurationProject();
            CobiGenFactory.create(generatorConfProj.getLocationURI());

            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "OK.";
            openSuccessDialog(healthyCheckMessage, false);

        } catch (GeneratorProjectNotExistentException e) {
            LOG.warn("Configuration project not found!", e);
            healthyCheckMessage = firstStep + "NOT FOUND!\n"
                + "=> Please import the configuration project into your workspace as stated in the "
                + "documentation of CobiGen or in the one of your project.";
            PlatformUIUtil.openErrorDialog(healthyCheckMessage, null);
        } catch (InvalidConfigurationException e) {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "INVALID!";
            if (generatorConfProj != null) {
                Path configurationProject = Paths.get(generatorConfProj.getLocationURI());
                ContextConfigurationVersion currentVersion =
                    new ContextConfigurationUpgrader().resolveLatestCompatibleSchemaVersion(configurationProject);
                if (currentVersion != null) {
                    // upgrade possible
                    healthyCheckMessage +=
                        "\n\nAutomatic upgrade of the context configuration available.\n" + "Detected: "
                            + currentVersion + " / Currently Supported: " + ContextConfigurationVersion.getLatest();
                    boolean upgraded = openErrorDialogWithContextUpgrade(healthyCheckMessage, configurationProject);

                    if (upgraded) {
                        // re-run Health Check
                        Display.getCurrent().asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                execute();
                            }
                        });
                    }
                    return;
                } else {
                    healthyCheckMessage += "\n\nNo automatic upgrade of the context configuration possible. "
                        + "Maybe just a mistake in the context configuration?";
                    healthyCheckMessage += "\n\n=> " + e.getLocalizedMessage();
                }
            }
            PlatformUIUtil.openErrorDialog(healthyCheckMessage, null);
            LOG.warn(healthyCheckMessage, e);
        } catch (Throwable e) {
            healthyCheckMessage = "An unexpected error occurred!";
            PlatformUIUtil.openErrorDialog(healthyCheckMessage, e);
            LOG.error(healthyCheckMessage, e);
        }
    }

    /**
     * Open up an error dialog, which encompasses the ability to upgrade the context configuration.
     * @param healthyCheckMessage
     *            message to be shown to the user
     * @param configurationFolder
     *            path of the configuration folder to perform the upgrade
     * @return <code>true</code> if the upgrade has been triggered, <code>false</code> if the dialog has been
     *         aborted
     * @author mbrunnli (Jun 24, 2015)
     */
    private boolean openErrorDialogWithContextUpgrade(String healthyCheckMessage, Path configurationFolder) {
        MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Health Check", null,
            healthyCheckMessage, MessageDialog.ERROR, new String[] { "Upgrade Context Configuration", "Abort" }, 1);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) {
            upgradeContextConfiguration(configurationFolder);
            return true;
        }
        return false;
    }

    /**
     * Performs the upgrade of the context configuration.
     * @param configurationFolder
     *            {@link Path} of the configuration folder to be upgraded.
     * @author mbrunnli (Jun 24, 2015), updated by sholzer (29.09.2015) for issue #156
     */
    private void upgradeContextConfiguration(Path configurationFolder) {
        Activator.getDefault().stopConfigurationListener();
        ContextConfigurationUpgrader contextConfigurationUpgrader = new ContextConfigurationUpgrader();
        try {
            try {
                contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, false);
            } catch (BackupFailedException e) {
                boolean continueUpgrade =
                    MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
                        com.capgemini.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.HealthCheck.DIALOG_TITLE,
                        "Backup failed while upgrading. "
                            + "An upgrade deletes all comments in the configuration file, "
                            + "which will be gone without a backup. Continue anyhow?");
                if (continueUpgrade) {
                    contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, true);
                }
            }
        } catch (Throwable e) {
            PlatformUIUtil.openErrorDialog("An unexpected error occurred while upgrading the context configuration", e);
        }

        ResourcesPluginUtil.refreshConfigurationProject();
        Activator.getDefault().startConfigurationProjectListener();
    }

    /**
     * Open up a success dialog with the given message, which provides the ability to perform an advanced
     * health check in addition.
     * @param healthyCheckMessage
     *            message to be shown to the user
     * @param warn
     *            if the message box should be displayed as a warning
     * @author mbrunnli (Jun 23, 2015)
     */
    private void openSuccessDialog(String healthyCheckMessage, boolean warn) {
        MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
            com.capgemini.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.HealthCheck.DIALOG_TITLE,
            null, healthyCheckMessage, warn ? MessageDialog.WARNING : MessageDialog.INFORMATION,
            new String[] { "Advanced Health Check", "OK" }, 1);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) {
            new AdvancedHealthCheck().execute();
        }
    }

}
