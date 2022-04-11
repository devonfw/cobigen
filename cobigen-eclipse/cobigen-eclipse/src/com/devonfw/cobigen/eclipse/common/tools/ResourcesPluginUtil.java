package com.devonfw.cobigen.eclipse.common.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.updatetemplates.UpdateTemplatesDialog;
import com.devonfw.cobigen.impl.util.ExtractTemplatesUtil;

/** Util for NPE save access of {@link ResourcesPlugin} utils */
public class ResourcesPluginUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ResourcesPluginUtil.class);

  /**
   * Generator project
   */
  static IProject generatorProj;

  /**
   * If Update Dialog already shown while refreshConfigurationProject, don't show it again in call of
   * getGeneratorConfigurationProject
   */
  static boolean isUpdateDialogShown = false;

  /**
   * This variable is only used on the case the user doesn't have templates and he does not want either to download
   * them. Strange case but could happen.
   */
  static boolean userWantsToDownloadTemplates = true;

  /**
   * Refreshes the configuration project from the file system.
   */

  public static void refreshConfigurationProject() {

    try {
      isUpdateDialogShown = false;
      generatorProj = getGeneratorConfigurationProject();
      if (null != generatorProj && !generatorProj.exists()) {
        generatorProj.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
      }
    } catch (CoreException e) {
      MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
          "Could not refresh the CobiGen configuration project automatically. " + "Please try it again manually");
      LOG.warn("Configuration project refresh failed", e);
    }
  }

  /**
   * Returns the generator configuration project if it exists. If the project is closed, the project will be opened
   * automatically
   *
   * @return the generator configuration {@link IProject}
   * @throws GeneratorProjectNotExistentException if no generator configuration project called
   *         {@link ResourceConstants#CONFIG_PROJECT_NAME} exists
   * @throws CoreException if an existing generator configuration project could not be opened
   */
  public static IProject getGeneratorConfigurationProject() throws GeneratorProjectNotExistentException, CoreException {

    File templatesDirectory = getTemplatesDirectory();

    generatorProj = ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);

    if (!generatorProj.exists()) {
      if (!isUpdateDialogShown) {
        if (templatesDirectory.exists()) {
          Path jarFilePath = TemplatesJarUtil.getJarFile(false, templatesDirectory.toPath());
          // If we don't find at least one jar, then we do need to download new templates
          if (jarFilePath == null || !Files.exists(jarFilePath)) {
            int result = createUpdateTemplatesDialog();
            isUpdateDialogShown = true;
            if (result == 1) {
              // User does not want to download templates.
              userWantsToDownloadTemplates = false;
            } else {
              userWantsToDownloadTemplates = true;
            }
          }

        } else {
          int result = createUpdateTemplatesDialog();
          isUpdateDialogShown = true;
          if (result == 1) {
            // User does not want to download templates.
            userWantsToDownloadTemplates = false;
          } else {
            userWantsToDownloadTemplates = true;
          }
        }
      }
    }

    if (userWantsToDownloadTemplates) {
      return generatorProj;
    } else {
      return null;
    }
  }

  /**
   * Creates a new dialog so that the user can choose between updating the templates or not
   *
   * @return the result of this decision, 0 if he wants to update the templates, 1 if he does not
   */
  public static int createUpdateTemplatesDialog() {

    UpdateTemplatesDialog dialog = new UpdateTemplatesDialog();
    dialog.setBlockOnOpen(true);
    return dialog.open();

  }

  /**
   * @param isDownloadSource true if downloading source jar file
   * @return fileName Name of the file downloaded
   * @throws MalformedURLException {@link MalformedURLException} occurred
   * @throws IOException {@link IOException} occurred
   */
  public static String downloadJar(boolean isDownloadSource) throws MalformedURLException, IOException {

    String fileName = "";

    ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    progressMonitor.open();
    progressMonitor.getProgressMonitor().beginTask("downloading latest templates...", 0);

    File templatesDirectory = getTemplatesDirectory();
    try {
      fileName = TemplatesJarUtil.downloadLatestDevon4jTemplates(isDownloadSource, templatesDirectory);
    } finally {
      progressMonitor.close();
    }

    return fileName;
  }

  /**
   * Returns the file path of the templates jar
   *
   * @param isSource true if we want to get source jar file path
   * @return fileName Name of the jar downloaded or null if it was not found
   */
  public static String getJarPath(boolean isSource) {

    File templatesDirectory = getTemplatesDirectory();

    Path jarFilePath = TemplatesJarUtil.getJarFile(isSource, templatesDirectory.toPath());

    if (jarFilePath == null || !Files.exists(jarFilePath)) {
      return "";
    }

    String fileName = jarFilePath.toFile().getPath()
        .substring(jarFilePath.toFile().getPath().lastIndexOf(File.separator) + 1);
    return fileName;
  }

  /**
   * Gets or creates a new templates directory
   *
   * @return the templateDirectory
   */
  private static File getTemplatesDirectory() {

    File templatesDirectory = CobiGenPaths.getTemplatesFolderPath().toFile();
    return templatesDirectory;
  }

  /**
   * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder structure at
   * /main/CobiGen_Templates location
   *
   * @param fileName Name of source jar file downloaded
   * @throws IOException {@link IOException} occurred
   * @throws MalformedURLException {@link MalformedURLException} occurred
   */
  public static void processJar(String fileName) throws MalformedURLException, IOException {

    String pathForCobigenTemplates = "";
    IPath ws = ResourcesPluginUtil.getWorkspaceLocation();

    try {
      pathForCobigenTemplates = ws.toPortableString()
          + (((ResourcesPluginUtil.getGeneratorConfigurationProject() != null)
              && (ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() != null))
                  ? ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation()
                  : StringUtils.EMPTY);
    } catch (GeneratorProjectNotExistentException e1) {
      LOG.warn("Configuration project not found!", e1);
      String s = "=> Probably there was an error while downloading the templates. "
          + "Please try to update them and try again.";
      PlatformUIUtil.openErrorDialog(s, e1);
    } catch (CoreException e) {
      MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
          "Could not refresh the CobiGen configuration project automatically. " + "Please try it again manually");
      LOG.warn("Configuration project refresh failed", e);
    }

    FileSystem fileSystem = FileSystems.getDefault();
    Path cobigenFolderPath = null;
    if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
      cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
    }

    try {
      ExtractTemplatesUtil.extractTemplates(cobigenFolderPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES), false);
    } catch (Exception e) {
      LOG.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
      PlatformUIUtil
          .openErrorDialog("An exception occurred while processing Jar file to create CobiGen_Templates folder", e);
    }
  }

  /**
   * @return workspace location
   */
  public static IPath getWorkspaceLocation() {

    IPath ws = ResourcesPlugin.getWorkspace().getRoot().getLocation();
    return ws;
  }

  /**
   * Set the boolean variable to notify that the user wants to download the templates
   *
   * @param userWantsToDownloadTemplates true if users wants to download the templates
   */
  public static void setUserWantsToDownloadTemplates(boolean userWantsToDownloadTemplates) {

    ResourcesPluginUtil.userWantsToDownloadTemplates = userWantsToDownloadTemplates;
  }

}
