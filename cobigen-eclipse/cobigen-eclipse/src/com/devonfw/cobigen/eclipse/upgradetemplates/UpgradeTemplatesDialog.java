package com.devonfw.cobigen.eclipse.upgradetemplates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import org.slf4j.MDC;

import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.UpgradeTemplatesDialogs;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.impl.CobiGenFactory;

/**
 * Dialog to upgrade old monolithic templates
 */
public class UpgradeTemplatesDialog extends Dialog {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UpgradeTemplatesDialog.class);

  /**
   * Creates a new {@link UpgradeTemplatesDialog}
   */
  public UpgradeTemplatesDialog() {

    super(Display.getDefault().getActiveShell());

  }

  @Override
  protected Control createDialogArea(Composite parent) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    getShell().setText(UpgradeTemplatesDialogs.DIALOG_TITLE);
    Composite contentParent = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
    contentParent.setLayoutData(gridData);

    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 15;
    layout.marginHeight = 15;
    contentParent.setLayout(layout);

    Label introduction = new Label(contentParent, SWT.LEFT);
    gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
    gridData.widthHint = 450;
    gridData.horizontalSpan = 2;
    introduction.setText("You are using an Old templates:");
    introduction.setLayoutData(gridData);

    GridData leftGridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
    leftGridData.widthHint = 320;
    GridData rightGridData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
    rightGridData.widthHint = 80;
    Label label = new Label(contentParent, SWT.NONE);
    label.setText("templates-devon4j");
    label.setLayoutData(leftGridData);
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return contentParent;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {

    Button button = createButton(parent, IDialogConstants.OK_ID, "Upgrade", false);
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {

      }
    });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * Executes the upgrade Process
   */
  public void execute() {

    IProject generatorConfProj = null;

    // refresh and check context configuration
    ResourcesPluginUtil.refreshConfigurationProject();

    // check configuration project existence in workspace
    try {
      generatorConfProj = ResourcesPluginUtil.getGeneratorConfigurationProject();
    } catch (GeneratorProjectNotExistentException e) {
      e.printStackTrace();
    } catch (CoreException e) {
      e.printStackTrace();
    }

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

    openSuccessDialog();

  }

  /**
   *
   */
  private void openSuccessDialog() {

  }

  /**
   *
   */
  public void postponeAndGenerate() {

    // TODO Auto-generated method stub

  }

}
