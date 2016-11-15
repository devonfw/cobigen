package com.capgemini.cobigen.eclipse.common.tools;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.capgemini.cobigen.eclipse.common.ui.LinkErrorDialog;
import com.google.common.collect.Lists;

/**
 * This class provides some helper functions in order to minimize code overhead
 * @author Malte Brunnlieb (06.12.2012)
 */
public class PlatformUIUtil {

    /**
     * Waits for the {@link IWorkbench} to appear and returns it
     * @return {@link IWorkbench} of the IDE
     */
    public static IWorkbench getWorkbench() {
        IWorkbench workbench;
        while ((workbench = PlatformUI.getWorkbench()) == null) {
        }
        return workbench;
    }

    /**
     * Waits for the active {@link IWorkbenchWindow} and returns it
     * @return the active {@link IWorkbenchWindow} of the UI
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        IWorkbench workbench = getWorkbench();
        IWorkbenchWindow workbenchWindow;
        while ((workbenchWindow = workbench.getActiveWorkbenchWindow()) == null) {
        }
        return workbenchWindow;
    }

    /**
     * Waits for the active {@link IWorkbenchPage} and returns it
     * @return the active {@link IWorkbenchPage} of the UI
     */
    public static IWorkbenchPage getActiveWorkbenchPage() {
        IWorkbenchWindow workbenchWindow = getActiveWorkbenchWindow();
        IWorkbenchPage page;
        while ((page = workbenchWindow.getActivePage()) == null) {
        }
        return page;
    }

    /**
     * Open up an error dialog, which shows the stack trace of the cause if not null.
     * @param message
     *            message to be shown to the user
     * @param cause
     *            of the error or <code>null</code> if the error was not caused by any {@link Throwable}
     */
    public static void openErrorDialog(final String message, final Throwable cause) {

        getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                if (cause == null) {
                    MessageDialog.openError(Display.getDefault().getActiveShell(),
                        CobiGenDialogConstants.DIALOG_TITLE_ERROR, message);
                } else {
                    LinkErrorDialog.openError(Display.getDefault().getActiveShell(),
                        CobiGenDialogConstants.DIALOG_TITLE_ERROR, message, createMultiStatus(cause));
                }
            }
        });

    }

    /**
     * Creates a {@link MultiStatus} for the stack trace of the given exception.
     * @param t
     *            exception to format
     * @return the {@link MultiStatus} containing an error {@link Status} for each stack trace entry.
     */
    public static MultiStatus createMultiStatus(Throwable t) {

        List<Status> childStatus = Lists.newArrayList();
        StackTraceElement[] stackTraceElements = t.getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement element = stackTraceElements[i];
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, element.toString());
            childStatus.add(status);
        }

        if (t.getCause() != null) {
            childStatus.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Caused by"));
            childStatus.add(createMultiStatus(t.getCause()));
        }

        MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
            childStatus.toArray(new Status[0]), t.toString(), t);
        return ms;
    }
}
