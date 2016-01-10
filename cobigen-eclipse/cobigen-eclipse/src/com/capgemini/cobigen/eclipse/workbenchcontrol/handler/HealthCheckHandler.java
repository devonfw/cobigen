package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.healthcheck.HealthCheck;

/**
 * This handler triggers the {@link HealthCheck} to provide more information about the current status of
 * CobiGen and potentially why it cannot be used with the current selection.
 * @author mbrunnli (Jun 16, 2015)
 */
public class HealthCheckHandler extends AbstractHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckHandler.class);

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 16, 2015)
     */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        try {
            new HealthCheck().execute(HandlerUtil.getCurrentSelection(event));
        } catch (Throwable e) {
            LOG.error("An unexpected error occurred while processing the health check. This is a bug.", e);
            PlatformUIUtil.openErrorDialog("Error",
                "An unexpected error occurred while processing the health check. This might be a bug.", e);
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }
}
