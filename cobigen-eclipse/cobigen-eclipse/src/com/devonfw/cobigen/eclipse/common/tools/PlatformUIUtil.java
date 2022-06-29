package com.devonfw.cobigen.eclipse.common.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
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

import com.devonfw.cobigen.eclipse.Activator;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.devonfw.cobigen.eclipse.common.widget.LinkErrorDialog;
import com.google.common.collect.Lists;

/**
 * This class provides some helper functions in order to minimize code overhead
 *
 * @author Malte Brunnlieb (06.12.2012)
 */
public class PlatformUIUtil {

  /**
   * The active {@link IWorkbenchWindow}
   */
  private static IWorkbenchWindow workbenchWindow;

  /**
   * Returns the active {@link IWorkbenchWindow}
   *
   * @return {@link IWorkbenchWindow}
   */
  private static IWorkbenchWindow getWorkbenchWindow() {

    return workbenchWindow;
  }

  /**
   * Sets the active {@link IWorkbenchWindow}
   *
   * @param workbenchWindow new value of {@link #getworkbenchWindow}.
   */
  private static void setWorkbenchWindow(IWorkbenchWindow workbenchWindow) {

    PlatformUIUtil.workbenchWindow = workbenchWindow;
  }

  /**
   * Waits for the {@link IWorkbench} to appear and returns it
   *
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
   *
   * @return the active {@link IWorkbenchWindow} of the UI
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow() {

    getWorkbench().getDisplay().syncExec(() -> {
      setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    });

    while (getWorkbenchWindow() == null) {
    }
    return getWorkbenchWindow();
  }

  /**
   * Waits for the active {@link IWorkbenchPage} and returns it
   *
   * @return the active {@link IWorkbenchPage} of the UI
   */
  public static IWorkbenchPage getActiveWorkbenchPage() {

    IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
    IWorkbenchPage page;
    while ((page = activeWorkbenchWindow.getActivePage()) == null) {
    }
    return page;
  }

  /**
   * Open up an error dialog, which shows the stack trace of the cause if not null.
   *
   * @param message message to be shown to the user
   * @param cause of the error or <code>null</code> if the error was not caused by any {@link Throwable}
   */
  public static void openErrorDialog(final String message, final Throwable cause) {

    getWorkbench().getDisplay().syncExec(() -> {

      if (cause == null) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), CobiGenDialogConstants.DIALOG_TITLE_ERROR,
            message);
      } else {
        LinkErrorDialog.openError(Display.getDefault().getActiveShell(), CobiGenDialogConstants.DIALOG_TITLE_ERROR,
            message, createMultiStatus(cause));
      }
    });

  }

  /**
   * Creates a {@link MultiStatus} for the stack trace of the given exception.
   *
   * @param t exception to format
   * @return the {@link MultiStatus} containing an error {@link Status} for each stack trace entry.
   */
  public static MultiStatus createMultiStatus(Throwable t) {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);

    final String trace = sw.toString();

    List<Status> childStatus = Lists.newArrayList();
    for (String line : trace.split(System.getProperty("line.separator"))) {
      childStatus.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line));
    }

    MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatus.toArray(new Status[0]),
        t.getMessage(), t);
    return ms;
  }
}
