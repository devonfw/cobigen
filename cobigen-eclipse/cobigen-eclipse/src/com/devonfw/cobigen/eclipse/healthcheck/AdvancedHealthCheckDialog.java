package com.devonfw.cobigen.eclipse.healthcheck;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.to.HealthCheckReport;
import com.devonfw.cobigen.eclipse.Activator;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.HealthCheckDialogs;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.tools.MessageUtil;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;

/**
 * Dialog to show the health check results as well as performing the templates configuration upgrades.
 */
public class AdvancedHealthCheckDialog extends Dialog {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(AdvancedHealthCheckDialog.class);

  /** The HealthCheck to be executed by this dialog */
  private HealthCheck healthCheck;

  /** The HealthCheckReport that is created by the HealthCheck */
  private HealthCheckReport report;

  /** Availability of templates configuration in the found folders */
  private Set<String> hasConfiguration;

  /** Accessibility of templates configuration for changes */
  private Set<String> isAccessible;

  /** Templates configurations, which can be upgraded */
  private Map<String, Path> upgradeableConfigurations;

  /** Templates configurations, which are already up to date */
  private Set<String> upToDateConfigurations;

  /** Expected templates configurations liked by the context configuration */
  private List<String> expectedTemplatesConfigurations;

  /**
   * Creates a new {@link AdvancedHealthCheckDialog} with the given parameters.
   *
   * @param report the {@link HealthCheckReport} that is used by this {@link AdvancedHealthCheckDialog}
   * @param healthCheck the initial {@link HealthCheck} that was created by the {@link HealthCheckDialog}
   * @throws GeneratorProjectNotExistentException if the generator project does not exist
   */
  AdvancedHealthCheckDialog(HealthCheckReport report, HealthCheck healthCheck)
      throws GeneratorProjectNotExistentException {

    super(Display.getDefault().getActiveShell());
    if (report != null && report.getHasConfiguration() != null) {
      this.hasConfiguration = report.getHasConfiguration();
    }
    if (report != null && report.getIsAccessible() != null) {
      this.isAccessible = report.getIsAccessible();
    }
    if (report != null && report.getUpgradeableConfigurations() != null) {
      this.upgradeableConfigurations = report.getUpgradeableConfigurations();
    }
    if (report != null && report.getUpToDateConfigurations() != null) {
      this.upToDateConfigurations = report.getUpToDateConfigurations();
    }
    if (report != null && report.getExpectedTemplatesConfigurations() != null) {
      this.expectedTemplatesConfigurations = report.getExpectedTemplatesConfigurations();
    }
    this.healthCheck = healthCheck;
  }

  @Override
  protected Control createDialogArea(Composite parent) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    getShell().setText(HealthCheckDialogs.ADVANCED_DIALOG_TITLE);
    Composite contentParent = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
    contentParent.setLayoutData(gridData);

    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 15;
    layout.marginHeight = 15;
    contentParent.setLayout(layout);

    Label introduction = new Label(contentParent, SWT.LEFT | SWT.WRAP);
    gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
    gridData.widthHint = 400;
    gridData.horizontalSpan = 2;
    introduction.setText("The following template folders are referenced by the context configuration. "
        + "These are the results of scanning each templates configuration.");
    introduction.setLayoutData(gridData);

    GridData leftGridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
    leftGridData.widthHint = 320;
    GridData rightGridData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
    rightGridData.widthHint = 80;
    if (this.expectedTemplatesConfigurations != null) {
      for (final String key : this.expectedTemplatesConfigurations) {
        Label label = new Label(contentParent, SWT.NONE);
        label.setText(key);
        label.setLayoutData(leftGridData);

        if (this.upToDateConfigurations.contains(key)) {
          Label infoLabel = new Label(contentParent, SWT.NONE);
          infoLabel.setText("Up-to-date");
          infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
          infoLabel.setLayoutData(rightGridData);
        } else if (this.upgradeableConfigurations.containsKey(key)) {
          Button upgrade = new Button(contentParent, SWT.PUSH);
          upgrade.setText("Upgrade");
          upgrade.setLayoutData(rightGridData);
          upgrade.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

              MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
              AdvancedHealthCheckDialog.this.healthCheck.upgradeTemplatesConfiguration(
                  AdvancedHealthCheckDialog.this.upgradeableConfigurations.get(key), BackupPolicy.BACKUP_IF_POSSIBLE);
              refreshUI();
              MDC.remove(InfrastructureConstants.CORRELATION_ID);
            }
          });
        } else if (!this.hasConfiguration.contains(key)) {
          Label infoLabel = new Label(contentParent, SWT.NONE);
          infoLabel.setText("Not found!");
          infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
          infoLabel.setLayoutData(rightGridData);
        } else if (!this.isAccessible.contains(key)) {
          Label infoLabel = new Label(contentParent, SWT.NONE);
          infoLabel.setText("Not writable!");
          infoLabel.setLayoutData(rightGridData);
        } else {
          Label infoLabel = new Label(contentParent, SWT.NONE);
          infoLabel.setText("Invalid!");
          infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
          infoLabel.setLayoutData(rightGridData);
        }
      }
    }

    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return contentParent;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {

    GridLayout layout = new GridLayout();
    layout.marginWidth = 15;
    layout.marginHeight = 15;
    parent.setLayout(layout);
    GridData data = new GridData(GridData.END, GridData.END, true, true);
    parent.setLayoutData(data);
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  /**
   * Refreshes the UI to show the updated configuration.
   */
  private void refreshUI() {

    try {
      if (ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() != null) {
        this.report = this.healthCheck
            .perform(ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation().toFile().toPath());
      }
      AdvancedHealthCheckDialog advancedHealthCheckDialog = new AdvancedHealthCheckDialog(this.report,
          this.healthCheck);
      advancedHealthCheckDialog.setBlockOnOpen(false);
      close();
      advancedHealthCheckDialog.open();
    } catch (GeneratorProjectNotExistentException e) {
      this.report.addError(e);
      LOG.warn("Configuration project not found!", e);
      String s = "=> Please import the configuration project into your workspace as stated in the "
          + "documentation of CobiGen or in the one of your project.";
      s = MessageUtil.enrichMsgIfMultiError(s, this.report);
      PlatformUIUtil.openErrorDialog(s, e);
    } catch (CoreException e) {
      String s = "An eclipse internal exception occurred while retrieving the configuration folder resource.";
      s = MessageUtil.enrichMsgIfMultiError(s, this.report);
      LOG.error("An eclipse internal exception occurred while retrieving the configuration folder resource.", e);
      PlatformUIUtil.openErrorDialog(s, e);
    }
    ResourcesPluginUtil.refreshConfigurationProject();
    Activator.getDefault().startConfigurationProjectListener();
  }
}
