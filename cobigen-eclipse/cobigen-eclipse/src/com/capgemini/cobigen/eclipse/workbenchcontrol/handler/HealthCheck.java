package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.upgrade.ContextConfigurationUpgrader;
import com.capgemini.cobigen.config.upgrade.version.ContextConfigurationVersion;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener;
import com.capgemini.cobigen.exceptions.BackupFailedException;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.google.common.collect.Lists;

/**
 * This handler implements the Health Check to provide more information about the current status of CobiGen
 * and potentially why it cannot be used with the current selection.
 * @author mbrunnli (Jun 16, 2015)
 */
public class HealthCheck extends AbstractHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheck.class);

    /** Dialog title of the Health Check */
    private static final String DIALOG_TITLE = "Health Check";

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 16, 2015)
     */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());

        String firstStep =
            "1. CobiGen configuration project '" + ResourceConstants.CONFIG_PROJECT_NAME + "'... ";
        String secondStep =
            "\n2. CobiGen context configuration '" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME + "'... ";

        SelectionServiceListener selectionServiceListener = null;
        String healthyCheckMessage = "";
        IProject generatorConfProj = null;
        try {
            generatorConfProj = ResourceConstants.getGeneratorConfigurationProject();
            selectionServiceListener = new SelectionServiceListener();
        } catch (GeneratorProjectNotExistentException e) {
            healthyCheckMessage =
                firstStep + "NOT FOUND IN WORKSPACE!\n"
                    + "=> Please import the configuration project as stated in the documentation of CobiGen"
                    + " or in the one of your project.";
            openErrorDialog(healthyCheckMessage, null);
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
                            try {
                                execute(event);
                            } catch (ExecutionException e) {
                                LOG.warn("Unexpected exception occurred during re-run of Health Check after context configuration upgrade");
                            }
                        }
                    });
                    return null;
                } else {
                    healthyCheckMessage +=
                        "\n\nNo automatic upgrade of the context configuration possible. "
                            + "Maybe just a mistake in the context configuration?";
                    healthyCheckMessage += "\n\n=> " + e.getLocalizedMessage();
                }

            }
            openErrorDialog(healthyCheckMessage, null);
        } catch (Throwable e) {
            healthyCheckMessage = "An unexpected error occurred while loading CobiGen! ";
            openErrorDialog(healthyCheckMessage, e);
        }

        if (selectionServiceListener != null) {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "OK.";
            healthyCheckMessage += "\n3. Check validity of current selection... ";
            ISelection sel = HandlerUtil.getCurrentSelection(event);
            if (sel instanceof IStructuredSelection) {
                try {
                    if (selectionServiceListener.isValidInput((IStructuredSelection) sel)) {
                        healthyCheckMessage += "OK.";
                        openErrorDialog(healthyCheckMessage, null);
                    } else {
                        healthyCheckMessage += "NO MATCHING TRIGGER.";
                        openSuccessDialog(healthyCheckMessage);
                    }
                } catch (InvalidInputException e) {
                    healthyCheckMessage += "invalid!\n=> CAUSE: " + e.getLocalizedMessage();
                    if (e.hasRootCause()) {
                        openErrorDialog(healthyCheckMessage, e);
                    } else {
                        openErrorDialog(healthyCheckMessage, null);
                    }
                } catch (Throwable e) {
                    healthyCheckMessage += "\n=> An unexpected error occurred while loading CobiGen! ";
                    openErrorDialog(healthyCheckMessage, e);
                }
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
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
                ErrorDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE,
                    "Backup failed while upgrading. Continue anyhow?", createMultiStatus(e));

            if (continueResult == Window.OK) {
                contextConfigurationUpgrader.upgradeConfigurationToLatestVersion(configurationFolder, true);
            }
        } catch (Throwable e) {
            openErrorDialog(
                "Upgrade failed: An unexpected error occurred while upgrading the context configuration", e);
        }

        refreshConfigurationProject();

        Activator.getDefault().startSelectionServiceListener();
        Activator.getDefault().startConfigurationProjectListener();
    }

    /**
     * Refreshes the configuration project from the file system.
     * @author mbrunnli (Jun 24, 2015)
     */
    private void refreshConfigurationProject() {
        try {
            ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME)
                .refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        } catch (CoreException e) {
            openErrorDialog("Could not refresh the CobiGen configuration project automatically. "
                + "Please try it again manually", e);
        }
    }

    /**
     * Open up an error dialog, which shows the stack trace of the cause if not null.
     * @param healthyCheckMessage
     *            message to be shown to the user
     * @param cause
     *            of the error or <code>null</code> if the error was not caused by any {@link Throwable}
     * @author mbrunnli (Jun 23, 2015)
     */
    private void openErrorDialog(String healthyCheckMessage, Throwable cause) {
        if (cause == null) {
            MessageDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE, healthyCheckMessage);
        } else {
            ErrorDialog.openError(Display.getDefault().getActiveShell(), DIALOG_TITLE, healthyCheckMessage,
                createMultiStatus(cause));
        }
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
            new MessageDialog(Display.getDefault().getActiveShell(), "Health Check", null,
                healthyCheckMessage, MessageDialog.INFORMATION, new String[] { "Advanced Check", "OK" }, 0);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) { // Advanced
            // TODO do advanced check of templates
        }
    }

    /**
     * Creates a {@link MultiStatus} for the stack trace of the given exception.
     * @param t
     *            exception to format
     * @return the {@link MultiStatus} containing an error {@link Status} for each stack trace entry.
     * @author mbrunnli (Jun 17, 2015)
     */
    private static MultiStatus createMultiStatus(Throwable t) {

        List<Status> childStatus = Lists.newArrayList();
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTrace : stackTraces) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, stackTrace.toString());
            childStatus.add(status);
        }

        MultiStatus ms =
            new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatus.toArray(new Status[0]),
                t.toString(), t);
        return ms;
    }
}
