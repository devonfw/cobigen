package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.GeneratorWrapperFactory;
import com.capgemini.cobigen.eclipse.wizard.generate.GenerateBatchWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.GenerateWizard;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;

/**
 * Handler for the Package-Explorer Event
 * @author mbrunnli (13.02.2013)
 */
public class GenerateHandler extends AbstractHandler {

    /**
     * Assigning logger to GenerateHandler
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateHandler.class);

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013), updated by sholzer (22.09.2015)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        LOG.info("Generate command triggered.");

        ISelection sel = HandlerUtil.getCurrentSelection(event);

        if (sel instanceof IStructuredSelection) {

            // when this handler is executed, we should we should be sure, that the selection is currently
            // supported by the following implementation

            try {
                LOG.info("Initiating CobiGen...");
                CobiGenWrapper generator =
                    GeneratorWrapperFactory.createGenerator((IStructuredSelection) sel);
                if (generator == null) {
                    MessageDialog.openError(HandlerUtil.getActiveShell(event), "Not yet supported!",
                        "The current selection is currently not supported as valid input.");
                    LOG.info("Invalid selection. No CobiGen instance created. Exiting generate command.");
                    return null;
                }

                if (!generator.isValidInput((IStructuredSelection) sel)) {
                    MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "No matching Trigger!",
                        "Your current selection is not valid as input for any generation purpose. "
                            + "Please find the specification of valid inputs in the context configuration ('"
                            + ResourceConstants.CONFIG_PROJECT_NAME + "/context.xml').");
                    LOG.info("No matching Trigger. Exiting generate command.");
                    return null;
                }

                if (((IStructuredSelection) sel).size() > 1 || (((IStructuredSelection) sel).size() == 1)
                    && ((IStructuredSelection) sel).getFirstElement() instanceof IPackageFragment) {
                    WizardDialog wiz =
                        new WizardDialog(HandlerUtil.getActiveShell(event),
                            new GenerateBatchWizard(generator));
                    wiz.setPageSize(new Point(800, 500));
                    wiz.open();
                    LOG.info("Generate Wizard (Batchmode) opened.");
                } else if (((IStructuredSelection) sel).size() == 1) {
                    WizardDialog wiz =
                        new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateWizard(generator));
                    wiz.setPageSize(new Point(800, 500));
                    wiz.open();
                    LOG.info("Generate Wizard opened.");
                }

            } catch (UnknownContextVariableException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Context Variable: " + e.getMessage(), e);
                LOG.error("Unknown Context Variable", e);
            } catch (UnknownTemplateException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Template: " + e.getMessage(), e);
                LOG.error("Unknown Template", e);
            } catch (UnknownExpressionException e) {
                PlatformUIUtil.openErrorDialog("Error", "Unknown Expression: " + e.getMessage(), e);
                LOG.error("Unknown Expression", e);
            } catch (InvalidConfigurationException e) {
                PlatformUIUtil.openErrorDialog("Error", "Invalid Configuration: " + e.getMessage(), e);
                LOG.error("Invalid Configuration", e);
            } catch (GeneratorProjectNotExistentException e) {
                MessageDialog
                    .openError(
                        HandlerUtil.getActiveShell(event),
                        "Generator configuration project not found!",
                        "The project '"
                            + ResourceConstants.CONFIG_PROJECT_NAME
                            + "' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.");
                LOG.error(
                    "The project '{}' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.",
                    ResourceConstants.CONFIG_PROJECT_NAME, e);
            } catch (GeneratorCreationException e) {
                PlatformUIUtil.openErrorDialog("Error", "Could not create an instance of the generator!", e);
                LOG.error("Could not create an instance of the generator.", e);
            } catch (InvalidInputException e) {
                MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Invalid selection",
                    e.getMessage());
                LOG.info("Invalid input selected for generation: " + e.getMessage());
            } catch (Throwable e) {
                PlatformUIUtil.openErrorDialog("Error", "An unexpected exception occurred!", e);
                LOG.error("An unexpected exception occurred!", e);
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }
}
