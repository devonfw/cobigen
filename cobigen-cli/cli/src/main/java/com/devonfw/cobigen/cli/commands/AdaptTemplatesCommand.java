package com.devonfw.cobigen.cli.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.devonfw.cobigen.cli.utils.ConfigurationUtils;

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
     * Utils class for CobiGen related operations
     */
    private static CobiGenUtils cobigenUtils = new CobiGenUtils();

    /**
     * Utils class for configuration related operations
     */
    private static ConfigurationUtils configurationUtils = new ConfigurationUtils();

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    boolean verbose;

    /**
     * If this option is provided, we will unpack the templates jar at the given location
     */
    @Option(names = { "--custom-location", "-cl" }, arity = "0..1",
        description = MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION)
    File customTemplatesLocation = null;

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Constructor needed for Picocli
     */
    public AdaptTemplatesCommand() {
        super();
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder
     * structure at /main/CobiGen_Templates location
     * @param customPath
     *            Custom path to be used as target directory
     * @throws IOException
     *             if no destination path could be set
     */
    public static void processJar(Path customPath) throws IOException {
        String pathForCobigenTemplates = "";

        if (customPath != null) {
            pathForCobigenTemplates = customPath.toString();
            logger.info("Target directory for custom templates {}", pathForCobigenTemplates);
        } else {
            pathForCobigenTemplates = configurationUtils.getCobigenCliRootPath().toString();
        }

        String jarPath = cobigenUtils.getTemplatesJar(false).getPath().toString();
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenFolderPath = null;
        if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
            cobigenFolderPath = fileSystem.getPath(pathForCobigenTemplates);
        }

        if (cobigenFolderPath == null) {
            throw new IOException(
                "An exception occurred while processing Jar files to create CobiGen_Templates folder");
        }

        logger.info("Processing jar file @ {}", jarPath);

        List<String> templateNames = new ArrayList<>();
        try (ZipFile file = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            if (Files.notExists(cobigenFolderPath)) {
                Files.createDirectory(cobigenFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                    + ConfigurationUtils.COBIGEN_TEMPLATES + File.separator + File.separator + entry.getName());
                if (templateNames.parallelStream().anyMatch(entry.getName()::contains)
                    || entry.getName().contains("context.xml")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                        + ConfigurationUtils.COBIGEN_TEMPLATES + File.separator + File.separator + entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath = fileSystem.getPath(cobigenFolderPath + File.separator
                        + ConfigurationUtils.COBIGEN_TEMPLATES + File.separator + "src" + File.separator + "main"
                        + File.separator + "java" + File.separator + entry.getName());
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

        logger.info("Running AdaptTemplatesCommand!, please wait...");

        Path cobigenTemplatesDirectory = null;
        if (customTemplatesLocation != null) {
            logger.info("Creating Templates folder at custom location {}", customTemplatesLocation);
            customTemplatesLocation = configurationUtils.preprocessInputFile(customTemplatesLocation);
            configurationUtils.createConfigFile(customTemplatesLocation);
            // sets custom templates directory path from configuration file property
            cobigenTemplatesDirectory = configurationUtils.getCustomTemplatesLocation();
        } else {
            // sets default templates directory path from CLI location
            cobigenTemplatesDirectory = Paths.get(configurationUtils.getCobigenCliRootPath().toUri());
        }

        if (Files.exists(cobigenTemplatesDirectory)) {
            processJar(cobigenTemplatesDirectory);
        } else {
            logger.error("Could not find target directory to extract templates @", cobigenTemplatesDirectory);
            return 0;
        }

        logger.info("Successfully created custom templates folder @ {}", cobigenTemplatesDirectory);
        return 1;
    }
}
