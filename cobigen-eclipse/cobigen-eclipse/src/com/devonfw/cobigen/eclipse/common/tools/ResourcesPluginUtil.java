package com.devonfw.cobigen.eclipse.common.tools;

import java.io.File;
import java.io.FilenameFilter;
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
    static boolean isUpdateDialogShown = false;

    /**
     * Filters the files on a directory so that we can check whether the templates are already downloaded
     */
    static FilenameFilter fileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            if (lowercaseName.contains(ResourceConstants.JAR_FILE_NAME)) {
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * Refreshes the configuration project from the file system.
     */

    public static void refreshConfigurationProject() {
        try {
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

        IPath ws = ResourcesPluginUtil.getWorkspaceLocation();
        File templatesDirectory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString()
            + ResourceConstants.DOWNLOADED_JAR_FOLDER);

        generatorProj = ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);

        if (!generatorProj.exists()) {
            if (!isUpdateDialogShown) {
                if (templatesDirectory.exists()) {

                    // If we find at least one jar, then we do not need to download new templates
                    if (templatesDirectory.listFiles(fileNameFilter).length > 0) {
                        return generatorProj;
                    } else {
                        int result = createUpdateTemplatesDialog();
                        isUpdateDialogShown = true;
                        if (result == 0) {

                        }
                    }

                } else {
                    int result = createUpdateTemplatesDialog();
                    isUpdateDialogShown = true;
                    if (result == 0) {

                    }
                }
            }
        }

        return generatorProj;
    }

    /**
     *
     */
    private static int createUpdateTemplatesDialog() {
        MessageDialog dialog =
            new MessageDialog(Display.getDefault().getActiveShell(), "Generator configuration project not found!", null,
                "CobiGen_templates folder is not imported. Do you want to download latest templates and use it", 0,
                new String[] { "Update", "Cancel" }, 1);
        dialog.setBlockOnOpen(true);
        return dialog.open();

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
