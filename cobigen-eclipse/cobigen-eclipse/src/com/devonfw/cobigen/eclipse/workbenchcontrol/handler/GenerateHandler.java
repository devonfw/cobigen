package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.generator.GeneratorWrapperFactory;
import com.devonfw.cobigen.eclipse.healthcheck.HealthCheckDialog;
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

        } catch (InvalidConfigurationException e) {
            LOG.warn("Invalid configuration.", e);
            openInvalidConfigurationErrorDialog(e);
        } catch (CobiGenRuntimeException e) {
            LOG.error("CobiGen Error: {}", e.getMessage(), e);
            PlatformUIUtil.openErrorDialog(e.getMessage(), e);
        } catch (GeneratorProjectNotExistentException e) {
            LOG.error(
                "The project '{}' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.",
                ResourceConstants.CONFIG_PROJECT_NAME, e);
            MessageDialog.openError(HandlerUtil.getActiveShell(event), "Generator configuration project not found!",
                "The project '" + ResourceConstants.CONFIG_PROJECT_NAME
                    + "' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.");
        } catch (GeneratorCreationException e) {
            LOG.error("Could not create an instance of the generator.", e);
            PlatformUIUtil.openErrorDialog("Could not initialize CobiGen for the given selection: " + e.getMessage(),
                e);
        } catch (InvalidInputException e) {
            LOG.info("Invalid input selected for generation: {}", e.getMessage());
            MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Invalid selection", e.getMessage());
        } catch (Throwable e) {
            LOG.error("An unexpected exception occurred!", e);
            PlatformUIUtil.openErrorDialog("An unexpected exception occurred!", e);
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }

    /**
     * Opens up a message dialog for displaying further guidance on context configuration issues.
     * @param e
     *            {@link InvalidConfigurationException} occurred
     */
    private void openInvalidConfigurationErrorDialog(InvalidConfigurationException e) {
        MessageDialog dialog =
            new MessageDialog(Display.getDefault().getActiveShell(), "Invalid context configuration!", null,
                "Any context/templates configuration has been changed into an invalid state "
                    + "OR is simply outdated, if you recently updated CobiGen. "
                    + "For further investigation and automatic upgrade options start CobiGen's Health Check."
                    + "\n\nOriginal error message: " + e.getMessage(),
                MessageDialog.ERROR, new String[] { "Health Check", "OK" }, 1);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();
        if (result == 0) {
            new HealthCheckDialog().execute();
        }
    }
}
