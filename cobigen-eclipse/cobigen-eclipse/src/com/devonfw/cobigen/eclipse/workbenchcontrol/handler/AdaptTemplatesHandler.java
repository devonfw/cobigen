package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;

/**
 * Handler for the Package-Explorer EventfimportProjectIntoWorkspace
 */
public class AdaptTemplatesHandler extends AbstractHandler {

    /**
     * Assigning logger to UpdateTemplatesHandler
     */
    private static final Logger LOG = LoggerFactory.getLogger(AdaptTemplatesHandler.class);

    /**
     * Location of workspace root
     */
    IPath ws = ResourcesPluginUtil.getWorkspaceLocation();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        IProject generatorProj =
            ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);

        if (generatorProj.exists()) {
            MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Info!", null,
                "CobiGen_Templates folder is already imported, click on Update templates button to update with latest. ",
                MessageDialog.INFORMATION, new String[] { "Ok" }, 1);
            dialog.setBlockOnOpen(true);
            dialog.open();
        } else {
            MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Warning!", null,
                "Clicking on ok button will override existing CobiGen_Templates in workspace.", MessageDialog.WARNING,
                new String[] { "Ok", "Cancel" }, 1);
            dialog.setBlockOnOpen(true);
            int result = dialog.open();

            if (result == 0) {
                try {
                    String fileName = ResourcesPluginUtil.downloadJar(true);
                    ResourcesPluginUtil.processJar(fileName);
                    try {
                        importProjectIntoWorkspace();
                    } catch (NotDefinedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NotEnabledException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NotHandledException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Information", null,
                        "CobiGen_Templates folder is imported sucessfully", MessageDialog.INFORMATION,
                        new String[] { "Ok" }, 1);
                    dialog.setBlockOnOpen(true);
                    dialog.open();
                } catch (MalformedURLException e) {
                    LOG.error("An exception with download url of maven central", e);
                    PlatformUIUtil.openErrorDialog("An exception with download url of maven central", e);
                } catch (IOException e) {
                    LOG.error("An exception occurred while writing Jar files to .metadata folder", e);
                    PlatformUIUtil.openErrorDialog("An exception occurred while writing Jar files to .metadata folder",
                        e);
                }
            }
            MDC.remove(InfrastructureConstants.CORRELATION_ID);
        }
        return null;

    }

    /**
     * CobiGen_Templates folder created at main folder using source jar will be imported into workspace
     * @throws NotHandledException
     * @throws NotEnabledException
     * @throws NotDefinedException
     * @throws ExecutionException
     */
    private void importProjectIntoWorkspace()
        throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
        ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

        ICommandService commandService =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);

        Command command = commandService.getCommand("org.eclipse.m2e.enableNatureAction");
        command.executeWithChecks(new ExecutionEvent());

        progressMonitor.open();
        progressMonitor.getProgressMonitor().beginTask("Importing templates...", 0);
        try {
            IProject project =
                ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);
            IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
            description.setLocation(new org.eclipse.core.runtime.Path(ws.toPortableString() + "/CobiGen_Templates"));
            project.create(description, null);
            project.open(null);
            progressMonitor.close();

        } catch (CoreException e) {
            progressMonitor.close();
            e.printStackTrace();
            MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                "Some Exception occurred while importing CobiGen_Templates into workspace");
        }
    }

    /**
     * This method is responsible for deleting existing templates
     *
     * @param path
     *            path of CobiGen_Templates to be overridden
     * @throws IOException
     *             exception will be thrown in case path doesn't exist
     */
    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}
