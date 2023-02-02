package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.core.ui.internal.actions.EnableNatureAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.tools.ExceptionHandler;
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
    IProject generatorProj = ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);
    IProject generatorProjOfTempltesSets = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(ResourceConstants.TEMPLATE_SETS_CONFIG_PROJECT_NAME);
    Path templateSet = ResourcesPluginUtil.getTemplateSetDirectory();

    if (generatorProjOfTempltesSets.exists()) {
      // 1. the project is imported so, do not adapt(no need to extract the jar files) For now nothing to do.

    } else if (Files.exists(templateSet)) {

      // 2. downloaded exists? first adapt the jar file (files), then import project to eclipse

      Path downloadedPath = templateSet.resolve(ResourceConstants.TEMPLATE_SETS_DOWNLOADED);
      Path adaptedPath = templateSet.resolve(ResourceConstants.TEMPLATE_SETS_ADAPTED);

      // A. adapt the jar files if not already adapted
      if (Files.exists(downloadedPath) && !Files.exists(adaptedPath))
        ResourcesPluginUtil.adaptTemplateSet(templateSet);

      // B. Import the project
      importProjectIntoWorkspace(ResourceConstants.TEMPLATE_SETS_CONFIG_PROJECT_NAME, templateSet);
      /*
       * 3. TODO downloaded does not exists? update command must be executed. then go to 2. // (step 3 can be ignored
       * for now until the new template-sets are deployed online.)
       */
    } else {
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
            String fileName = ResourcesPluginUtil.getJarPath(true);
            if (fileName.equals("")) {
              result = createUpdateTemplatesDialog();
              if (result == 1) {
                MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                    "Templates have not been found, please download them!");
                throw new NullPointerException("Templates have not been found!");
              } else {
                fileName = ResourcesPluginUtil.downloadJar(true);
              }

            }
            ResourcesPluginUtil.processJar(fileName);

            importProjectIntoWorkspace(ResourceConstants.CONFIG_PROJECT_NAME, null);
            dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Information", null,
                "CobiGen_Templates folder is imported sucessfully", MessageDialog.INFORMATION, new String[] { "Ok" },
                1);
            dialog.setBlockOnOpen(true);
            dialog.open();
          } catch (MalformedURLException e) {
            LOG.error("An exception with download url of maven central", e);
            PlatformUIUtil.openErrorDialog("An exception with download url of maven central", e);
          } catch (IOException e) {
            LOG.error("An exception occurred while writing Jar files to .metadata folder", e);
            PlatformUIUtil.openErrorDialog("An exception occurred while writing Jar files to .metadata folder", e);
          } catch (Throwable e) {
            ExceptionHandler.handle(e, HandlerUtil.getActiveShell(event));
          }
        }
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
      }
    }
    return null;

  }

  /**
   * CobiGen_Templates folder created at main folder using source jar will be imported into workspace
   */
  private void importProjectIntoWorkspace(String projectName, Path projectPath) {

    ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

    // Class that contains the logic of the command 'configure -> to Maven Project'
    EnableNatureAction mavenConverter = new EnableNatureAction();

    progressMonitor.open();
    progressMonitor.getProgressMonitor().beginTask("Importing templates...", 0);
    try {
      IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
      if (projectName == ResourceConstants.TEMPLATE_SETS_CONFIG_PROJECT_NAME) {
        description.setLocationURI(projectPath.toUri());
      } else {
        description.setLocation(new org.eclipse.core.runtime.Path(this.ws.toPortableString() + "/" + projectName));
      }
      project.create(description, null);

      // We set the current project to be converted to a Maven project
      ISelection sel = new StructuredSelection(project);
      mavenConverter.selectionChanged(null, sel);

      project.open(null);
      // Converts the current project to a Maven project
      if (projectName == ResourceConstants.CONFIG_PROJECT_NAME)
        mavenConverter.run(null);
      progressMonitor.close();

    } catch (CoreException e) {
      progressMonitor.close();
      e.printStackTrace();
      MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
          "Some Exception occurred while importing " + projectName + " into workspace");
    }
  }

  /**
   * This method is responsible for deleting existing templates
   *
   * @param path path of CobiGen_Templates to be overridden
   * @throws IOException exception will be thrown in case path doesn't exist
   */
  void deleteDirectoryStream(Path path) throws IOException {

    Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  /**
   * Creates a new dialog so that the user can choose between updating the templates or not
   *
   * @return the result of this decision, 0 if he wants to update the templates, 1 if he does not
   */
  private static int createUpdateTemplatesDialog() {

    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
        "Generator configuration project not found!", null,
        "CobiGen_templates folder is not imported. Do you want to download latest templates and use it", 0,
        new String[] { "Update", "Cancel" }, 1);
    dialog.setBlockOnOpen(true);
    return dialog.open();

  }
}
