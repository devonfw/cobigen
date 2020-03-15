package com.devonfw.cobigen.cli.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.logger.CLILogger;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;

import ch.qos.logback.classic.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *
 */
@Command(description = MessagesConstants.ADAPT_TEMPLATES_DESCRIPTION, name = "adapt-templates", aliases = { "a" },
    mixinStandardHelpOptions = true)
public class AdaptTemplatesCommand implements Callable<Integer> {

    /**
     * Name of templates folder
     */
    private static final String COBIGEN_TEMPLATES = "CobiGen_Templates";

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Utils class for CobiGen related operations
     */
    private static CobiGenUtils cobigenUtils = new CobiGenUtils();

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    boolean verbose;

    /**
     * Constructor needed for Picocli
     */
    public AdaptTemplatesCommand() {
        super();
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder
     * structure at /main/CobiGen_Templates location
     * @param fileName
     *            Name of source jar file downloaded
     * @throws IOException
     *             {@link IOException} occurred
     * @throws MalformedURLException
     *             {@link MalformedURLException} occurred
     */
    public static void processJar(String fileName) throws MalformedURLException, IOException {
        String pathForCobigenTemplates = "";

        try {
            File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path rootCLIPath = locationCLI.getParentFile().toPath();
            pathForCobigenTemplates = rootCLIPath.toString();
            logger.info("rootCLIPath {}", rootCLIPath.toString());
            logger.info("locationCLI {}", locationCLI.toString());
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String templatesPath = cobigenUtils.getTemplatesJar(false).getPath();

        logger.info("templatesPath {}", templatesPath.toString());

        String jarPath = templatesPath.toString();
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenFolderPath = null;
        if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
            cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
            logger.info("cobigenFolderPath {}", cobigenFolderPath.toString());
        }

        logger.info("Processing jar file @ {}", jarPath);

        try (ZipFile file = new ZipFile(jarPath)) {
            logger.info("file {}", file.getName());
        } catch (IOException e) {
            logger.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
        }

        // If we are unzipping a sources jar, we need to get the pom.xml from the normal jar
        if (fileName.contains("sources")) {

            String classJarPath = templatesPath;

            try (ZipFile file = new ZipFile(classJarPath)) {
                Enumeration<? extends ZipEntry> entries = file.entries();
                Path cobigenTemplatesFolderPath = null;
                if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
                    cobigenTemplatesFolderPath =
                        fileSystem.getPath(pathForCobigenTemplates + File.separator + COBIGEN_TEMPLATES);
                }

                if (cobigenTemplatesFolderPath == null) {
                    throw new IOException(
                        "An exception occurred while processing Jar files to create CobiGen_Templates folder");
                }

                if (Files.notExists(cobigenTemplatesFolderPath)) {
                    Files.createDirectory(cobigenTemplatesFolderPath);
                }
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("pom.xml")) {
                        Path saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                            + COBIGEN_TEMPLATES + File.separator + File.separator + entry.getName());

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
                logger.debug("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
            }
        }

        List<String> templateNames = new ArrayList<>();
        try (ZipFile file = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            if (Files.notExists(cobigenFolderPath)) {
                Files.createDirectory(cobigenFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator + COBIGEN_TEMPLATES
                    + File.separator + File.separator + entry.getName());
                if (templateNames.parallelStream().anyMatch(entry.getName()::contains)
                    || entry.getName().contains("context.xml")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator + COBIGEN_TEMPLATES
                        + File.separator + File.separator + entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath = fileSystem
                        .getPath(cobigenFolderPath + File.separator + COBIGEN_TEMPLATES + File.separator + "src"
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
            logger.debug("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
        }
    }

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        logger.info("starting AdaptTemplatesCommand!");
        processJar("test");
        return 1;
    }
}
