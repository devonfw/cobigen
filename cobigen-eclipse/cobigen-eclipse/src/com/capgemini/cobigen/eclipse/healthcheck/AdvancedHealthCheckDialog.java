package com.capgemini.cobigen.eclipse.healthcheck;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.capgemini.cobigen.config.upgrade.TemplateConfigurationUpgrader;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.exceptions.BackupFailedException;

/**
 * Dialog to show the advanced health check results as well as performing the templates configuration
 * upgrades.
 * @author mbrunnli (Jun 24, 2015)
 */
public class AdvancedHealthCheckDialog extends Dialog {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AdvancedHealthCheckDialog.class);

    /** Availability of templates configuration in the found folders */
    private Set<String> hasConfiguration;

    /** Accessibility of templates configuration for changes */
    private Map<String, Boolean> isAccessible;

    /** Templates configurations, which can be upgraded */
    private Map<String, Path> upgradeableConfigurations;

    /** Templates configurations, which are already up to date */
    private Set<String> upToDateConfigurations;

    /**
     * Creates a new {@link AdvancedHealthCheckDialog} with the given parameters.
     * @param hasConfiguration
     *            Availability of templates configuration in the found folders
     * @param isAccessible
     *            Accessibility of templates configuration for changes
     * @param upgradeableConfigurations
     *            Templates configurations, which can be upgraded
     * @param upToDateConfigurations
     *            Templates configurations, which are already up to date
     * @author mbrunnli (Jun 24, 2015)
     */
    AdvancedHealthCheckDialog(Set<String> hasConfiguration, Map<String, Boolean> isAccessible,
        Map<String, Path> upgradeableConfigurations, Set<String> upToDateConfigurations) {
        super(Display.getDefault().getActiveShell());
        this.hasConfiguration = hasConfiguration;
        this.isAccessible = isAccessible;
        this.upgradeableConfigurations = upgradeableConfigurations;
        this.upToDateConfigurations = upToDateConfigurations;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 24, 2015)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());

        getShell().setText(AdvancedHealthCheck.COMMON_DIALOG_TITLE);
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
        introduction
            .setText("The following template configurations have been found in the current configuration folder:");
        introduction.setLayoutData(gridData);

        GridData leftGridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        GridData rightGridData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
        for (final String key : hasConfiguration) {
            Label label = new Label(contentParent, SWT.NONE);
            label.setText(key);
            label.setLayoutData(leftGridData);

            if (upToDateConfigurations.contains(key)) {
                Label infoLabel = new Label(contentParent, SWT.NONE);
                infoLabel.setText("Up-to-date");
                infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
                infoLabel.setLayoutData(rightGridData);
            } else if (isAccessible.get(key)) {
                if (upgradeableConfigurations.get(key) != null) {
                    Button upgrade = new Button(contentParent, SWT.PUSH);
                    upgrade.setText("Upgrade");
                    upgrade.setLayoutData(rightGridData);
                    upgrade.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            upgradeTemplatesConfiguration(upgradeableConfigurations.get(key));
                        }
                    });
                } else {
                    Label infoLabel = new Label(contentParent, SWT.NONE);
                    infoLabel.setText("Invalid!");
                    infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
                    infoLabel.setLayoutData(rightGridData);
                }
            } else {
                Label infoLabel = new Label(contentParent, SWT.NONE);
                infoLabel.setText("Not accessible!");
                infoLabel.setLayoutData(rightGridData);
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return contentParent;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 24, 2015)
     */
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
     * Upgrades the templates configuration within the folder with the given {@link Path}
     * @param templatesConfigurationFolder
     *            folder {@link Path} of the templates folder
     * @author mbrunnli (Jun 24, 2015)
     */
    private void upgradeTemplatesConfiguration(Path templatesConfigurationFolder) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());
        LOG.info("Upgrade of the templates configuration in '" + templatesConfigurationFolder
            + "' triggered.");

        Activator.getDefault().stopSelectionServiceListener();
        Activator.getDefault().stopConfigurationListener();

        TemplateConfigurationUpgrader templateConfigurationUpgrader = new TemplateConfigurationUpgrader();
        boolean successful = true;
        try {
            try {
                templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(
                    templatesConfigurationFolder, false);
                LOG.info("Upgrade finished successfully.");
            } catch (BackupFailedException e) {
                boolean continueUpgrade =
                    MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
                        AdvancedHealthCheck.COMMON_DIALOG_TITLE, "Backup failed while upgrading. "
                            + "An upgrade deletes all comments in the configuration file, "
                            + "which will be gone without a backup. Continue anyhow?");
                if (continueUpgrade) {
                    templateConfigurationUpgrader.upgradeConfigurationToLatestVersion(
                        templatesConfigurationFolder, true);
                    LOG.info("Upgrade finished successfully.");
                } else {
                    LOG.info("Upgrade aborted.");
                    successful = false;
                }
            }
        } catch (Throwable e) {
            PlatformUIUtil.openErrorDialog("Upgrade failed",
                "An unexpected error occurred while upgrading the templates configuration", e);
            LOG.error("Upgrade failed!", e);
            successful = false;
        }

        // refresh by reload
        if (successful) {
            close();
            new AdvancedHealthCheck().execute();
        }

        ResourcesPluginUtil.refreshConfigurationProject();

        Activator.getDefault().startSelectionServiceListener();
        Activator.getDefault().startConfigurationProjectListener();
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }
}
