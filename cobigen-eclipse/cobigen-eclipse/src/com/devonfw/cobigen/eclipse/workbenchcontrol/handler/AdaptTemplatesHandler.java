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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.bindings.Trigger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.impl.config.ContextConfiguration;

/**
 * Handler for the Package-Explorer Event
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
                "Cobigen_Templates folder is already imported, click on Update templates button to update with latest. ",
                MessageDialog.INFORMATION, new String[] { "Ok" }, 1);
            dialog.setBlockOnOpen(true);
            dialog.open();
        } else {
            MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Warning!", null,
                "Clicking on ok button will override exisitng Cobigen_templates in workspace.", MessageDialog.WARNING,
                new String[] { "Ok", "Cancel" }, 1);
            dialog.setBlockOnOpen(true);
            int result = dialog.open();

            if (result == 0) {
                try {
                    String fileName = ResourcesPluginUtil.downloadJar(true);
                    processJar(fileName);
                    importProjectIntoWorkspace();
                    dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Information", null,
                        "Cobigen_Templates folder is imported sucessfully", MessageDialog.INFORMATION,
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
     * Cobigen_Templates folder created at main folder using source jar will be imported into workspace
     */
    private void importProjectIntoWorkspace() {

        try {
            IProject project =
                ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);
            IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
            description.setLocation(
                new org.eclipse.core.runtime.Path(ws.toPortableString() + ResourceConstants.COBIGEN_TEMPLATES_FOLDER));
            project.create(description, null);
            project.open(null);
        } catch (CoreException e) {
            MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                "Some Exception occurred while importing Cobigen_Templates into workspace");
            LOG.warn("Some Exception occurred while importing Cobigen_Templates into workspace", e);
        }
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new Cobigen_templates folder
     * structure at /main/CobiGen_Templates location
     * @param fileName
     *            Name of source jar file downloaded
     */
    private void processJar(String fileName) {

        String jarPath = ws.toPortableString() + ResourceConstants.DOWNLOADED_JAR_FOLDER + "/" + fileName;
        String pathForCobigenTemplates = ws.toPortableString() + ResourceConstants.COBIGEN_TEMPLATES_FOLDER;
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
        Path configFolder = fileSystem.getPath(ws.toPortableString() + "/main/CobiGen_Templates/src/main/templates");
        ContextConfiguration contextConfiguration = new ContextConfiguration(configFolder);
        List<String> templateNames = new ArrayList<>();
        for (Trigger t : contextConfiguration.getTriggers()) {
            templateNames.add(t.getTemplateFolder());
        }

        try (ZipFile file = new ZipFile(jarPath)) {
            deleteDirectoryStream(configFolder);
            Enumeration<? extends ZipEntry> entries = file.entries();
            if (Files.notExists(cobigenFolderPath)) {
                Files.createDirectory(cobigenFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator + entry.getName());
                if (templateNames.parallelStream().anyMatch(entry.getName()::contains)
                    || entry.getName().contains("context.xml")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator + "src"
                        + File.separator + "main" + File.separator + "templates" + File.separator + entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator + "src"
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
            LOG.error("An exception occurred  while processing Jar files to create Cobigen_Templates folder", e);
            PlatformUIUtil.openErrorDialog("An exception occurred  while processing Jar file", e);
        }
    }

    /**
     * This method is responsible for deleting existing templates
     *
     * @param path
     *            path of Cobigen_Templates to be overridden
     * @throws IOException
     *             exception will be thrown in case path doesn't exist
     */
    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}
