package com.capgemini.cobigen.eclipse.healthcheck;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.upgrade.ContextConfigurationUpgrader;
import com.capgemini.cobigen.config.upgrade.version.ContextConfigurationVersion;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener;
import com.capgemini.cobigen.exceptions.BackupFailedException;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;

/**
 * This class implements the Health Check to provide more information about the current status of CobiGen and
 * potentially why it cannot be used with the current selection.
 * @author mbrunnli (Jun 24, 2015)
 */
public class HealthCheck {

    /** Dialog title of the Health Check */
    private static final String HEALTH_CHECK_DIALOG_TITLE = "Health Check";

    /**
     * Executes the simple health check, checking configuration project existence, validity of context
     * configuration, as well as validity of the current workbench selection as generation input.
     * @param selection
     *            current selection in the workbench
     * @author mbrunnli (Jun 24, 2015)
     */
    public void execute(final ISelection selection) {

        String firstStep =
            "1. CobiGen configuration project '" + ResourceConstants.CONFIG_PROJECT_NAME + "'... ";
        String secondStep =
            "\n2. CobiGen context configuration '" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME + "'... ";

        SelectionServiceListener selectionServiceListener = null;
        String healthyCheckMessage = "";
        IProject generatorConfProj = null;
        try {
            generatorConfProj = ResourcesPluginUtil.getGeneratorConfigurationProject();
            selectionServiceListener = new SelectionServiceListener();
        } catch (GeneratorProjectNotExistentException e) {
            healthyCheckMessage =
                firstStep + "NOT FOUND IN WORKSPACE!\n"
                    + "=> Please import the configuration project as stated in the documentation of CobiGen"
                    + " or in the one of your project.";
            PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, null);
        } catch (InvalidConfigurationException e) {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "INVALID!";
            if (generatorConfProj != null) {
                Path configurationProject = Paths.get(generatorConfProj.getLocationURI());
                ContextConfigurationVersion currentVersion =
                    new ContextConfigurationUpgrader()
                        .resolveLatestCompatibleSchemaVersion(configurationProject);
                if (currentVersion != null) {
                    // upgrade possible
                    ContextConfigurationVersion[] allVersions = ContextConfigurationVersion.values();
                    healthyCheckMessage +=
                        "\n\nAutomatic upgrade of the context configuration available.\n" + "Detected: "
                            + currentVersion + " / Currently Supported: "
                            + allVersions[allVersions.length - 1];
                    openErrorDialogWithContextUpgrade(healthyCheckMessage, configurationProject);

                    // re-run Health Check
                    Display.getCurrent().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            execute(selection);
                        }
                    });
                } else {
                    healthyCheckMessage +=
                        "\n\nNo automatic upgrade of the context configuration possible. "
                            + "Maybe just a mistake in the context configuration?";
                    healthyCheckMessage += "\n\n=> " + e.getLocalizedMessage();
                }

            }
            PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, null);
        } catch (Throwable e) {
            healthyCheckMessage = "An unexpected error occurred while loading CobiGen! ";
            PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, e);
        }

        if (selectionServiceListener != null) {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "OK.";
            healthyCheckMessage += "\n3. Check validity of current selection... ";
            if (selection instanceof IStructuredSelection) {
                try {
                    if (selectionServiceListener.isValidInput((IStructuredSelection) selection)) {
                        healthyCheckMessage += "OK.";
                        PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, null);
                    } else {
                        healthyCheckMessage += "NO MATCHING TRIGGER.";
                        openSuccessDialog(healthyCheckMessage);
                    }
                } catch (InvalidInputException e) {
                    healthyCheckMessage += "invalid!\n=> CAUSE: " + e.getLocalizedMessage();
                    if (e.hasRootCause()) {
                        PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, e);
                    } else {
                        PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, null);
                    }
                } catch (Throwable e) {
                    healthyCheckMessage += "\n=> An unexpected error occurred while loading CobiGen! ";
                    PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, e);
                }
            } else {
                healthyCheckMessage += "invalid!\n=> Unsupported selection type " + selection.getClass();
                PlatformUIUtil.openErrorDialog(HEALTH_CHECK_DIALOG_TITLE, healthyCheckMessage, null);
            }
        }
    }

    /**
     * Open up an error dialog, which encompasses the ability to upgrade the context configuration.
     * @param healthyCheckMessage
     *            message to be shown to the user
     * @param configurationFolder
     *            path of the configuration folder to perform the upgrade
     * @author mbrunnli (Jun 24, 2015)
     */
    private void openErrorDialogWithContextUpgrade(String healthyCheckMessage, Path configurationFolder) {
        MessageDialog dialog =
            new MessageDialog(Display.getDefault().getActiveShell(), "Health Check", null,
                healthyCheckMessage, MessageDialog.ERROR, new String[] { "Upgrade Context Configuration",
                    "Abort" }, 0);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) {
            upgradeContextConfiguration(configurationFolder);
        }
    }

    /**
     * Performs the upgrade of the context configuration.
     * @param configurationFolder
     *            {@link Path} of the configuration folder to be upgraded.
     * @author mbrunnli (Jun 24, 2015)
     */
    private void upgradeContextConfiguration(Path configurationFolder) {
        Activator.getDefault().stopSelectionServiceListener();
        Activator.getDefault().stopConfigurationListener();
        ContextConfigurationUpgrader contextConfigurationUpgrader = new ContextConfigurationUpgrader();
        try {
            contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, false);
        } catch (BackupFailedException e) {
            int continueResult =
                ErrorDialog.openError(Display.getDefault().getActiveShell(), HEALTH_CHECK_DIALOG_TITLE,
                    "Backup failed while upgrading. Continue anyhow?", PlatformUIUtil.createMultiStatus(e));

            if (continueResult == Window.OK) {
                contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, true);
            }
        } catch (Throwable e) {
            PlatformUIUtil.openErrorDialog("Upgrade failed",
                "An unexpected error occurred while upgrading the context configuration", e);
        }

        ResourcesPluginUtil.refreshConfigurationProject();

        Activator.getDefault().startSelectionServiceListener();
        Activator.getDefault().startConfigurationProjectListener();
    }

    /**
     * Open up a success dialog with the given message, which provides the ability to perform an advanced
     * health check in addition.
     * @param healthyCheckMessage
     *            message to be shown to the user
     * @author mbrunnli (Jun 23, 2015)
     */
    private void openSuccessDialog(String healthyCheckMessage) {
        MessageDialog dialog =
            new MessageDialog(Display.getDefault().getActiveShell(), HEALTH_CHECK_DIALOG_TITLE, null,
                healthyCheckMessage, MessageDialog.INFORMATION,
                new String[] { "Advanced Health Check", "OK" }, 0);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) {
            new AdvancedHealthCheck().execute();
        }
    }

}
