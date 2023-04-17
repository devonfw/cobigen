package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.tools.ExceptionHandler;
import com.devonfw.cobigen.eclipse.healthcheck.HealthCheckDialog;

/**
 * This handler triggers the {@link HealthCheckDialog} to provide more information about the current status of CobiGen
 * and potentially why it cannot be used with the current selection.
 */
public class HealthCheckHandler extends AbstractHandler {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(HealthCheckHandler.class);

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    checkMonolithicConfigurationException(event);
    try {
      new HealthCheckDialog().execute(true);
    } catch (Throwable e) {
      ExceptionHandler.handle(e, HandlerUtil.getActiveShell(event));
    }
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
    return null;
  }

  /**
   * checks if monolithic configuration exist, handles the exception and lets the user decide if the templates should be
   * upgraded.
   *
   * @param event handler event executing this job.
   *
   */
  private void checkMonolithicConfigurationException(ExecutionEvent event) {

    try {
      new HealthCheckDialog().execute();
    } catch (Throwable e) {
      ExceptionHandler.handle(e, HandlerUtil.getActiveShell(event));
    }

  }
}
