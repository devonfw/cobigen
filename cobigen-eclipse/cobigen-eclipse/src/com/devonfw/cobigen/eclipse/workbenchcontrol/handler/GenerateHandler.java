package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
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
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.generator.GeneratorWrapperFactory;
import com.devonfw.cobigen.eclipse.wizard.generate.GenerateBatchWizard;
import com.devonfw.cobigen.eclipse.wizard.generate.GenerateWizard;

/**
 * Handler for the Package-Explorer Event
 * @author mbrunnli (13.02.2013)
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

        // when this handler is executed, we should we should be sure, that the selection is currently
        // supported by the following implementation

        try {
            LOG.info("Initiating CobiGen...");
            CobiGenWrapper generator = GeneratorWrapperFactory.createGenerator(sel);
            if (generator == null) {
                LOG.info("Invalid selection. No CobiGen instance created. Exiting generate command.");
                MessageDialog.openError(HandlerUtil.getActiveShell(event), "Not yet supported!",
                    "The current selection is currently not supported as valid input.");
                return null;
            }

            if (!generator.isValidInput()) {
                LOG.info("No matching Trigger. Exiting generate command.");
                MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "No matching Trigger!",
                    "Your current selection is not valid as input for any generation purpose. "
                        + "Please find the specification of valid inputs in the context configuration ('"
                        + ResourceConstants.CONFIG_PROJECT_NAME + "/context.xml').");
                return null;
            }

            if (!generator.isSingleNonContainerInput()) {
                LOG.info("Open Generate Wizard (Batchmode) ...");
                WizardDialog wiz =
                    new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateBatchWizard(generator));
                wiz.setPageSize(new Point(800, 500));
                wiz.open();
                LOG.debug("Generate Wizard (Batchmode) opened.");
            } else {
                LOG.info("Open Generate Wizard ...");
                WizardDialog wiz = new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateWizard(generator));
                wiz.setPageSize(new Point(800, 500));
                wiz.open();
                LOG.debug("Generate Wizard opened.");
            }

        } catch (Throwable e) {
            ExceptionHandler.handle(e, HandlerUtil.getActiveShell(event));
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }

}
