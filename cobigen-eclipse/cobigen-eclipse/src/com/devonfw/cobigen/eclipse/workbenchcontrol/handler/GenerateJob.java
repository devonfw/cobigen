package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.tools.ExceptionHandler;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.generator.GeneratorWrapperFactory;
import com.devonfw.cobigen.eclipse.wizard.generate.GenerateBatchWizard;
import com.devonfw.cobigen.eclipse.wizard.generate.GenerateWizard;
import com.devonfw.cobigen.impl.util.PostponeUtil;

/**
 *
 */
public class GenerateJob implements IRunnableWithProgress {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(GenerateJob.class);

  /** Execution Event */
  private ExecutionEvent event;

  /** Selection with which the generation has been triggered */
  private ISelection selection;

  /** Correlation ID */
  private String correlationId;

  /**
   * Create new Job to start generation
   *
   * @param event the handler event executing this job
   * @param selection Selection with which the generation has been triggered
   * @param correlationId the correlation ID starting this event
   */
  public GenerateJob(ExecutionEvent event, ISelection selection, String correlationId) {

    this.event = event;
    this.selection = selection;
    this.correlationId = correlationId;
  }

  @Override
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

    MDC.put(InfrastructureConstants.CORRELATION_ID, this.correlationId);

    // when this handler is executed, we should be sure, that the selection is currently supported by the following
    // implementation

    try {
      LOG.info("Initiating CobiGen...");
      monitor.beginTask("Initiating CobiGen...", 1);
      // TODO: Check this, see: https://github.com/devonfw/cobigen/issues/1679
      if (PostponeUtil.isTimePassed())
        checkMonolithicConfigurationException(monitor);
      CobiGenWrapper generator = GeneratorWrapperFactory.createGenerator(this.selection, monitor, true);
      monitor.worked(1);
      if (generator == null) {
        LOG.error("Invalid selection. No CobiGen instance created. Exiting generate command.");
        PlatformUIUtil.openErrorDialog("The current selection is currently not supported as valid input.", null);
        return;
      }

      monitor.beginTask("Searching valid triggers...", 1);
      if (!generator.isValidInput(monitor)) {
        LOG.info("No matching Trigger. Exiting generate command.");
        PlatformUIUtil.getWorkbench().getDisplay().syncExec(
            () -> MessageDialog.openInformation(HandlerUtil.getActiveShell(this.event), "No matching Trigger!",
                "Your current selection is not valid as input for any generation purpose. "
                    + "Please find the specification of valid inputs in the context configuration ('"
                    + ResourceConstants.CONFIG_PROJECT_NAME + "/context.xml')."));
        return;
      }
      monitor.worked(1);

      PlatformUIUtil.getWorkbench().getDisplay().syncExec(() -> {
        if (!generator.isSingleNonContainerInput()) {
          LOG.info("Open Generate Wizard (Batchmode) ...");
          WizardDialog wiz = new WizardDialog(HandlerUtil.getActiveShell(this.event),
              new GenerateBatchWizard(generator, monitor));
          wiz.setPageSize(new Point(800, 500));
          wiz.open();
          LOG.debug("Generate Wizard (Batchmode) opened.");
        } else {
          LOG.info("Open Generate Wizard ...");
          WizardDialog wiz = new WizardDialog(HandlerUtil.getActiveShell(this.event),
              new GenerateWizard(generator, monitor));
          wiz.setPageSize(new Point(800, 500));
          wiz.open();
          LOG.debug("Generate Wizard opened.");
        }
      });
    } catch (Throwable e) {
      ExceptionHandler.handle(e, HandlerUtil.getActiveShell(this.event));
    }

    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  /**
   * Checks if monolithic configuration exists, handles the exception and lets the user decide if the templates should
   * be upgraded.
   *
   * @param monitor of run method
   */
  private void checkMonolithicConfigurationException(IProgressMonitor monitor) {

    try {
      GeneratorWrapperFactory.createGenerator(this.selection, monitor, false);
    } catch (Throwable e) {
      ExceptionHandler.handle(e, HandlerUtil.getActiveShell(this.event));
    }

  }

}
