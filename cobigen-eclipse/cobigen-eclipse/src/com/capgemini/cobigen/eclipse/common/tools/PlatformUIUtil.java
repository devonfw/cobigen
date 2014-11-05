package com.capgemini.cobigen.eclipse.common.tools;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This class provides some helper functions in order to minimize code overhead
 * @author Malte Brunnlieb (06.12.2012)
 */
public class PlatformUIUtil {

    /**
     * Waits for the {@link IWorkbench} to appear and returns it
     * @return {@link IWorkbench} of the IDE
     * @author Malte Brunnlieb (06.12.2012)
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
     * @author Malte Brunnlieb (06.12.2012)
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
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static IWorkbenchPage getActiveWorkbenchPage() {
        IWorkbenchWindow workbenchWindow = getActiveWorkbenchWindow();
        IWorkbenchPage page;
        while ((page = workbenchWindow.getActivePage()) == null) {
        }
        return page;
    }

}
