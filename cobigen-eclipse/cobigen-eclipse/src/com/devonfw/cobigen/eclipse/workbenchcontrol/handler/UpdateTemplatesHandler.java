package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.updatetemplates.UpdateTemplatesDialog;

/**
 * Handler for the Package-Explorer Event
 */
public class UpdateTemplatesHandler extends AbstractHandler {

    /**
     * Assigning logger to UpdateTemplatesHandler
     */
    private static final Logger LOG = LoggerFactory.getLogger(UpdateTemplatesHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        try {
            UpdateTemplatesDialog updateTemplatesDialog = new UpdateTemplatesDialog();
            updateTemplatesDialog.open();
        } catch (Throwable e) {
            LOG.error("An unexpected error occurred while opening update dialog. This is a bug.", e);
            PlatformUIUtil
                .openErrorDialog("An unexpected error occurred while opening update dialog. This might be a bug.", e);
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }
}
