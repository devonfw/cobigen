package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;

/**
 * Handler for the Package-Explorer Event
 */
public class GenerateHandler extends AbstractHandler {

  /**
   * Assigning logger to GenerateHandler
   */
  private static final Logger LOG = LoggerFactory.getLogger(GenerateHandler.class);

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
    LOG.info("Generate command triggered.");

    ISelection sel = HandlerUtil.getCurrentSelection(event);

    ProgressMonitorDialog dialog = new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
    GenerateJob generateJob = new GenerateJob(event, sel, MDC.get(InfrastructureConstants.CORRELATION_ID));
    try {
      dialog.run(true, false, generateJob);
    } catch (InvocationTargetException e) {
      LOG.error("Could not execute generate command.", e);
      PlatformUIUtil.openErrorDialog("Could not execute generate command.", e);
    } catch (InterruptedException e) {
      LOG.info("Generate Job aborted", LOG.isDebugEnabled() ? e : null);
    }

    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return null;
  }

}
