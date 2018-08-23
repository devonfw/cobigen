package com.devonfw.cobigen.eclipse.common.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;

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
    static boolean isUpdateDialogShown = true;

    /**
     * Refreshes the configuration project from the file system.
     */
    public static void refreshConfigurationProject() {
        try {
            isUpdateDialogShown = true;
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
     * Returns the generator configuration project if it exists. If the project is closed, the project will be
     * opened automatically
     * @return the generator configuration {@link IProject}
     * @throws GeneratorProjectNotExistentException
     *             if no generator configuration project called {@link ResourceConstants#CONFIG_PROJECT_NAME}
     *             exists
     * @throws CoreException
     *             if an existing generator configuration project could not be opened
     */
    public static IProject getGeneratorConfigurationProject()
        throws GeneratorProjectNotExistentException, CoreException {

        generatorProj = ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);

        if (!generatorProj.exists()) {
            int result = 0;
            if (!isUpdateDialogShown) {
                MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
                    "Generator configuration project not found!", null,
                    "Cobigen_templates folder is not imported. Do you want to download latest templates and use it", 0,
                    new String[] { "Update", "Cancel" }, 1);
                dialog.setBlockOnOpen(true);
                result = dialog.open();
            }
            if (result == 0) {
                isUpdateDialogShown = false;
                return null;

            } else {
                MessageDialog.openError(Display.getDefault().getActiveShell(),
                    "Generator configuration project not found!",
                    "The project '" + ResourceConstants.CONFIG_PROJECT_NAME
                        + "' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.");

                throw new GeneratorProjectNotExistentException();
            }
        }
        if (!generatorProj.isOpen()) {
            generatorProj.open(new NullProgressMonitor());
        }

        return generatorProj;
    }

    /**
     * @param isDownloadSource
     *            true if downloading source jar file
     * @return fileName Name of the file downloaded
     * @throws MalformedURLException
     *             {@link MalformedURLException} occurred
     * @throws IOException
     *             {@link IOException} occurred
     */
    public static String downloadJar(boolean isDownloadSource) throws MalformedURLException, IOException {
        String mavenUrl =
            "https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.devonfw.cobigen&a=templates-oasp4j&v=LATEST";
        if (isDownloadSource) {
            mavenUrl = mavenUrl + "&c=sources";
        }
        File directory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString()
            + ResourceConstants.DOWNLOADED_JAR_FOLDER);
        if (!directory.exists()) {
            directory.mkdir();
        }
        URL url = new URL(mavenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        String fileName = conn.getURL().getFile().substring(conn.getURL().getFile().lastIndexOf("/") + 1);
        File file = new File(directory.getPath() + File.separator + fileName);
        Path targetPath = file.toPath();
        if (!file.exists()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        conn.disconnect();
        return fileName;
    }

    /**
     * @return workspace location
     */
    public static IPath getWorkspaceLocation() {
        IPath ws = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        return ws;
    }
}
