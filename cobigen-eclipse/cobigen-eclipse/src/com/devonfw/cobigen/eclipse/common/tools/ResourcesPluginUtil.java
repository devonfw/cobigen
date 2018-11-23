package com.devonfw.cobigen.eclipse.common.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
     * This variable is only used on the case the user doesn't have templates and he does not want either to
     * download them. Strange case but could happen.
     */
    static boolean userWantsToDownloadTemplates = true;

    /**
     * Filters the files on a directory so that we can check whether the templates are already downloaded
     */
    static FilenameFilter fileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            String regex = ResourceConstants.JAR_FILE_REGEX_NAME;

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(lowercaseName);
            if (m.find()) {
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

        File templatesDirectory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString()
            + ResourceConstants.DOWNLOADED_JAR_FOLDER);

        generatorProj = ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);

        if (!generatorProj.exists()) {
            if (!isUpdateDialogShown) {
                if (templatesDirectory.exists()) {

                    // If we don't find at least one jar, then we do need to download new templates
                    if (templatesDirectory.listFiles(fileNameFilter).length <= 0) {
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
     * @return the result of this decision, 0 if he wants to update the templates, 1 if he does not
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

        String fileName = "";

        File templatesDirectory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString()
            + ResourceConstants.DOWNLOADED_JAR_FOLDER);
        if (!templatesDirectory.exists()) {
            templatesDirectory.mkdir();
        }

        File[] jarFiles = templatesDirectory.listFiles(fileNameFilter);

        if (jarFiles.length <= 0) {
            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            progressMonitor.open();
            progressMonitor.getProgressMonitor().beginTask("downloading templates...", 0);

            URL url = new URL(mavenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            fileName = conn.getURL().getFile().substring(conn.getURL().getFile().lastIndexOf("/") + 1);
            File file = new File(templatesDirectory.getPath() + File.separator + fileName);
            Path targetPath = file.toPath();
            if (!file.exists()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            conn.disconnect();
            progressMonitor.close();
        } else {
            fileName = jarFiles[0].getPath().substring(jarFiles[0].getPath().lastIndexOf(File.separator) + 1);
        }
        return fileName;
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder
     * structure at /main/CobiGen_Templates location
     * @param fileName
     *            Name of source jar file downloaded
     */
    public static void processJar(String fileName) {
        String pathForCobigenTemplates = "";
        IPath ws = ResourcesPluginUtil.getWorkspaceLocation();

        try {
            pathForCobigenTemplates =
                ws.toPortableString() + (((ResourcesPluginUtil.getGeneratorConfigurationProject() != null)
                    && (ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() != null))
                        ? ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() : StringUtils.EMPTY);
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
        String jarPath = ws.toPortableString() + ResourceConstants.DOWNLOADED_JAR_FOLDER + "/" + fileName;
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenFolderPath = null;
        if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
            cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
        }

        List<String> templateNames = new ArrayList<>();
        try (ZipFile file = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            if (Files.notExists(cobigenFolderPath)) {
                Files.createDirectory(cobigenFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                    + "CobiGen_Templates" + File.separator + File.separator + entry.getName());
                if (templateNames.parallelStream().anyMatch(entry.getName()::contains)
                    || entry.getName().contains("context.xml")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                        + "CobiGen_Templates" + File.separator + File.separator + entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath = fileSystem
                        .getPath(cobigenFolderPath + File.separator + "CobiGen_Templates" + File.separator + "src"
                            + File.separator + "main" + File.separator + "java" + File.separator + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(saveForFileCreationPath);
                } else {
                    Files.deleteIfExists(saveForFileCreationPath);
                    Files.createFile(saveForFileCreationPath);
                    try (InputStream is = file.getInputStream(entry);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        FileOutputStream fileOutput = new FileOutputStream(saveForFileCreationPath.toString());) {

                        while (bis.available() > 0) {
                            fileOutput.write(bis.read());
                        }
                    }
                }
            }
        } catch (IOException e) {

            LOG.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
            PlatformUIUtil.openErrorDialog(
                "An exception occurred while processing Jar file to create CobiGen_Templates folder", e);
        }
    }

    /**
     * @return workspace location
     */
    public static IPath getWorkspaceLocation() {
        IPath ws = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        return ws;
    }
}
