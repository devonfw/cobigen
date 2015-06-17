package com.capgemini.cobigen.eclipse.workbenchcontrol.handler;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.capgemini.cobigen.config.reader.ContextConfigurationReader;
import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.google.common.collect.Lists;

/**
 * This handler implements the Health Check to provide more information about the current status of CobiGen
 * and potentially why it cannot be used with the current selection.
 * @author mbrunnli (Jun 16, 2015)
 */
public class HealthCheck extends AbstractHandler {

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 16, 2015)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());

        String firstStep =
            "1. CobiGen configuration project '" + ResourceConstants.CONFIG_PROJECT_NAME + "'... ";
        String secondStep =
            "\n2. CobiGen context configuration '" + ContextConfigurationReader.CONFIG_FILENAME + "'... ";

        SelectionServiceListener selectionServiceListener = null;
        String healthyCheckMessage = "";
        try {
            selectionServiceListener = new SelectionServiceListener();
        } catch (GeneratorProjectNotExistentException e) {
            healthyCheckMessage =
                firstStep + "NOT FOUND IN WORKSPACE!\n"
                    + "=> Please import the configuration project as stated in the documentation of CobiGen"
                    + " or in the one of your project.";
        } catch (InvalidConfigurationException e) {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "INVALID!\n=> " + e.getLocalizedMessage();
        } catch (Throwable e) {
            healthyCheckMessage = "An unexpected error occurred while loading CobiGen! ";
            MultiStatus status = createMultiStatus(e);
            ErrorDialog.openError(Display.getDefault().getActiveShell(), "Health Check", healthyCheckMessage,
                status);
            return null;
        }

        if (selectionServiceListener == null) {
            MessageDialog.openError(Display.getDefault().getActiveShell(), "Health Check",
                healthyCheckMessage);
        } else {
            healthyCheckMessage = firstStep + "OK.";
            healthyCheckMessage += secondStep + "OK.";
            healthyCheckMessage += "\n3. Check validity of current selection... ";
            ISelection sel = HandlerUtil.getCurrentSelection(event);
            if (sel instanceof IStructuredSelection) {
                try {
                    if (selectionServiceListener.isValidInput((IStructuredSelection) sel)) {
                        healthyCheckMessage += "OK.";
                        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Health Check",
                            healthyCheckMessage);
                    } else {
                        healthyCheckMessage += "NO MATCHING TRIGGER.";
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Health Check",
                            healthyCheckMessage);
                    }
                } catch (InvalidInputException e) {
                    healthyCheckMessage += "invalid!\n=> CAUSE: " + e.getLocalizedMessage();
                    if (e.hasRootCause()) {
                        MultiStatus status = createMultiStatus(e);
                        ErrorDialog.openError(Display.getDefault().getActiveShell(), "Health Check",
                            healthyCheckMessage, status);
                    } else {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Health Check",
                            healthyCheckMessage);
                    }
                } catch (Throwable e) {
                    healthyCheckMessage += "\n=> An unexpected error occurred while loading CobiGen! ";
                    MultiStatus status = createMultiStatus(e);
                    ErrorDialog.openError(Display.getDefault().getActiveShell(), "Health Check",
                        healthyCheckMessage, status);
                }
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return null;
    }

    /**
     * Creates a {@link MultiStatus} for the stack trace of the given exception.
     * @param t
     *            exception to format
     * @return the {@link MultiStatus} containing an error {@link Status} for each stack trace entry.
     * @author mbrunnli (Jun 17, 2015)
     */
    private static MultiStatus createMultiStatus(Throwable t) {

        List<Status> childStatus = Lists.newArrayList();
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTrace : stackTraces) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, stackTrace.toString());
            childStatus.add(status);
        }

        MultiStatus ms =
            new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatus.toArray(new Status[0]),
                t.toString(), t);
        return ms;
    }
}
