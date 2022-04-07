package com.devonfw.cobigen.eclipse.healthcheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.HealthCheckReport;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.HealthCheckDialogs;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.tools.MessageUtil;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.exceptions.BackupFailedException;

/**
 * This class implements the Health Check to provide more information about the current status of CobiGen and
 * potentially why it cannot be used with the current selection.
 */
public class HealthCheckDialog {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(HealthCheckDialog.class);

  /** HealthCheckReport instance */
  private HealthCheckReport report;

  /** HealthCheck instance */
  private HealthCheck healthCheck;

  /**
   * Creates a new {@link HealthCheckDialog} with the given parameters.
   */
  public HealthCheckDialog() {

    this.healthCheck = CobiGenFactory.createHealthCheck();
  }

  /**
   * Executes the simple health check, checking configuration project existence, validity of context configuration, as
   * well as validity of the current workbench selection as generation input.
   */
  public void execute() {

    String firstStep = "1. CobiGen configuration project '" + ResourceConstants.CONFIG_PROJECT_NAME + "'... ";
    String secondStep = "\n2. CobiGen context configuration '" + ConfigurationConstants.CONTEXT_CONFIG_FILENAME
        + "'... ";

    String healthyCheckMessage = "";
    IProject generatorConfProj = null;

    try {

      // refresh and check context configuration
      ResourcesPluginUtil.refreshConfigurationProject();

      // check configuration project existence in workspace
      generatorConfProj = ResourcesPluginUtil.getGeneratorConfigurationProject();

      this.report = performHealthCheckReport();

      if (generatorConfProj != null && generatorConfProj.getLocationURI() != null) {
        CobiGenFactory.create(generatorConfProj.getLocationURI());
      } else {
        Path templatesDirectoryPath = CobiGenPaths.getTemplatesFolderPath();
        Path jarPath = TemplatesJarUtil.getJarFile(false, templatesDirectoryPath);
        boolean fileExists = (jarPath != null && Files.exists(jarPath));
        if (!fileExists) {
          MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
              "Not Downloaded the CobiGen Template Jar");
        }
      }

      healthyCheckMessage = firstStep + "OK.";
      healthyCheckMessage += secondStep;
      boolean healthyCheckWarning = false;

      if (this.report.getNumberOfErrors() == 0) {
        healthyCheckMessage += "OK.";
      } else {
        healthyCheckMessage += "INVALID.";
        healthyCheckWarning = true;
      }

      openSuccessDialog(healthyCheckMessage, healthyCheckWarning);
    } catch (GeneratorProjectNotExistentException e) {
      LOG.warn("Configuration project not found!", e);
      healthyCheckMessage = firstStep + "NOT FOUND!\n"
          + "=> Please import the configuration project into your workspace as stated in the "
          + "documentation of CobiGen or in the one of your project.";
      PlatformUIUtil.openErrorDialog(healthyCheckMessage, null);
    } catch (InvalidConfigurationException e) {
      // Won't be reached anymore
      healthyCheckMessage = firstStep + "OK.";
      healthyCheckMessage += secondStep + "INVALID!";
      if (generatorConfProj != null && generatorConfProj.getLocationURI() != null) {
        Path configurationProject = Paths.get(generatorConfProj.getLocationURI());
        ContextConfigurationVersion currentVersion = new ContextConfigurationUpgrader()
            .resolveLatestCompatibleSchemaVersion(configurationProject);
        if (currentVersion != null) {
          // upgrade possible
          healthyCheckMessage += "\n\nAutomatic upgrade of the context configuration available.\n" + "Detected: "
              + currentVersion + " / Currently Supported: " + ContextConfigurationVersion.getLatest();
          this.report = openErrorDialogWithContextUpgrade(healthyCheckMessage, configurationProject,
              BackupPolicy.ENFORCE_BACKUP);
          healthyCheckMessage = MessageUtil.enrichMsgIfMultiError(healthyCheckMessage, this.report);
          if (!this.report.containsError(RuntimeException.class)) {
            // re-run Health Check
            Display.getCurrent().asyncExec(new Runnable() {
              @Override
              public void run() {

                execute();
              }
            });
          }
        } else {
          healthyCheckMessage += "\n\nNo automatic upgrade of the context configuration possible. "
              + "Maybe just a mistake in the context configuration?";
          healthyCheckMessage += "\n\n=> " + e.getMessage();
          healthyCheckMessage = MessageUtil.enrichMsgIfMultiError(healthyCheckMessage, this.report);
          PlatformUIUtil.openErrorDialog(healthyCheckMessage, null);
        }
      } else {
        healthyCheckMessage += "\n\nCould not find configuration.";
        PlatformUIUtil.openErrorDialog(healthyCheckMessage, null);
      }
      LOG.warn(healthyCheckMessage, e);
    } catch (Throwable e) {
      healthyCheckMessage = "An unexpected error occurred! Templates were not found.";
      if (this.report != null && healthyCheckMessage != null) {
        healthyCheckMessage = MessageUtil.enrichMsgIfMultiError(healthyCheckMessage, this.report);
      }
      PlatformUIUtil.openErrorDialog(healthyCheckMessage, e);
      LOG.error(healthyCheckMessage, e);
    }
  }

  /**
   * Open up an error dialog, which encompasses the ability to upgrade the context configuration.
   *
   * @param healthyCheckMessage message to be shown to the user
   * @param configurationFolder path of the configuration folder to perform the upgrade
   * @param backupPolicy the {@link BackupPolicy} that should be used for the function
   * @return <code>true</code> if the upgrade has been triggered, <code>false</code> if the dialog has been aborted
   */
  private HealthCheckReport openErrorDialogWithContextUpgrade(String healthyCheckMessage, Path configurationFolder,
      BackupPolicy backupPolicy) {

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), HealthCheckDialogs.DIALOG_TITLE,
        null, healthyCheckMessage, MessageDialog.ERROR, new String[] { "Upgrade Context Configuration", "Abort" }, 1);
    dialog.setBlockOnOpen(true);
    int result = dialog.open();
    if (result == 0) {
      HealthCheckReport report = this.healthCheck.upgradeContextConfiguration(configurationFolder, backupPolicy);
      if (report.containsError(BackupFailedException.class)) {
        MessageDialog d = new MessageDialog(Display.getDefault().getActiveShell(), HealthCheckDialogs.DIALOG_TITLE,
            null, "Backup failed while trying to upgrade the context configuration. \n Continue without Backup?",
            MessageDialog.ERROR, new String[] { "Upgrade Without Backup", "Abort" }, 1);
        dialog.setBlockOnOpen(true);

        int newResult = d.open();
        if (newResult == 0) {
          HealthCheckReport newReport = this.healthCheck.upgradeContextConfiguration(configurationFolder,
              BackupPolicy.NO_BACKUP);
          return newReport;
        }
      }
      return report;
    }
    return this.report;
  }

  /**
   * Open up a success dialog with the given message, which provides the ability to perform an advanced health check in
   * addition.
   *
   * @param healthyCheckMessage message to be shown to the user
   * @param warn if the message box should be displayed as a warning
   */
  private void openSuccessDialog(String healthyCheckMessage, boolean warn) {

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), HealthCheckDialogs.DIALOG_TITLE,
        null, healthyCheckMessage, warn ? MessageDialog.WARNING : MessageDialog.INFORMATION,
        new String[] { "Advanced Health Check", "OK" }, 1);
    dialog.setBlockOnOpen(true);

    int result = dialog.open();
    if (result == 0) {
      try {
        // check configuration project existence in workspace
        this.report = performHealthCheckReport();
        AdvancedHealthCheckDialog advancedHealthCheckDialog = new AdvancedHealthCheckDialog(this.report,
            this.healthCheck);
        advancedHealthCheckDialog.setBlockOnOpen(false);
        advancedHealthCheckDialog.open();
      } catch (GeneratorProjectNotExistentException e) {
        LOG.warn("Configuration project not found!", e);
        String s = "=> Please import the configuration project into your workspace as stated in the "
            + "documentation of CobiGen or in the one of your project.";
        PlatformUIUtil.openErrorDialog(s, e);
      } catch (CoreException e) {
        LOG.error("An eclipse internal exception occurred while retrieving the configuration folder resource.", e);
        String s = "An eclipse internal exception occurred while retrieving the configuration folder resource.";
        PlatformUIUtil.openErrorDialog(s, e);
      }
    }
  }

  /**
   * Performs a HealthCheckReport on CobiGen_Templates in workspace or on latest templates jar.
   *
   * @return HealthCheckReport the {@link HealthCheckReport} created by the HealthCheck
   * @throws GeneratorProjectNotExistentException if no generator configuration project called
   * @throws CoreException if an existing generator configuration project could not be opened
   */
  private HealthCheckReport performHealthCheckReport() throws GeneratorProjectNotExistentException, CoreException {

    // check configuration project existence in workspace
    IPath ws = ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation();

    if (ws == null) {
      return this.healthCheck.perform();
    } else {
      String healthProj = ws.toPortableString();
      Path healthcheckFile = Paths.get(healthProj);
      return this.healthCheck.perform(healthcheckFile);
    }
  }
}
