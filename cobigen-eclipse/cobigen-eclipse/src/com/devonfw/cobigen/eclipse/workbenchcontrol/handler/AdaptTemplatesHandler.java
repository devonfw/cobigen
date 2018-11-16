package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
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
                    processJar(fileName);
                    importProjectIntoWorkspace();
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
     */
    private void importProjectIntoWorkspace() {
        ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

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
            // LOG.warn("Some Exception occurred while importing CobiGen_Templates into
            // workspace", e);
        }
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder
     * structure at /main/CobiGen_Templates location
     * @param fileName
     *            Name of source jar file downloaded
     */
    private void processJar(String fileName) {
        String pathForCobigenTemplates = "";
        try {
            pathForCobigenTemplates =
                ws.toPortableString() + (((ResourcesPluginUtil.getGeneratorConfigurationProject() != null)
                    && (ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() != null))
                        ? ResourcesPluginUtil.getGeneratorConfigurationProject().getLocation() : StringUtils.EMPTY);
        } catch (GeneratorProjectNotExistentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jarPath = ws.toPortableString() + ResourceConstants.DOWNLOADED_JAR_FOLDER + "/" + fileName;
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenFolderPath = null;
        if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
            cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
        }
        /*
         * Path configFolder = fileSystem.getPath(ws.toPortableString()); ContextConfiguration
         * contextConfiguration = new ContextConfiguration(configFolder); List<String> templateNames = new
         * ArrayList<>(); for (com.devonfw.cobigen.impl.config.entity.Trigger t :
         * contextConfiguration.getTriggers()) { templateNames.add(t.getTemplateFolder()); }
         */
        List<String> templateNames = new ArrayList<>();
        try (ZipFile file = new ZipFile(jarPath)) {
            // deleteDirectoryStream(configFolder);
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
                        + "CobiGen_Templates"
                        + File.separator /*
                                          * + "src" + File.separator + "main" + File.separator + "templates"
                                          */ + File.separator + entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath = fileSystem
                        .getPath(cobigenFolderPath + File.separator + "CobiGen_Templates" + File.separator + "src"
                            + File.separator + "main" + File.separator + "java" + File.separator + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(saveForFileCreationPath);
                } else {
                    Files.deleteIfExists(saveForFileCreationPath);
                    try (InputStream is = file.getInputStream(entry);
                        BufferedInputStream bis = new BufferedInputStream(is);) {
                        Files.createFile(saveForFileCreationPath);
                        FileOutputStream fileOutput = new FileOutputStream(saveForFileCreationPath.toString());
                        while (bis.available() > 0) {
                            fileOutput.write(bis.read());
                        }
                        fileOutput.close();
                    }
                }
            }
        } catch (IOException e) {

            LOG.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
            // PlatformUIUtil.openErrorDialog("An exception occurred while processing Jar
            // file", e);
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
        // Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}
