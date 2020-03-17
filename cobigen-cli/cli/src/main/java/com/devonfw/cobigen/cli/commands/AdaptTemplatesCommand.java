package com.devonfw.cobigen.cli.commands;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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
     * Name of configuration file
     */
    private static final String COBIGEN_CONFIG = "config.txt";

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
     * If this option is provided, we will unpack the templates jar at the given location
     */
    @Option(names = { "--custom-location", "-cl" }, arity = "0..1",
        description = MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION)
    File customTemplatesLocation = null;

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
            pathForCobigenTemplates = cobigenUtils.getCobigenCliRootPath().toString();
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

    /**
     * Creates a configuration file next to the CLI executable and stores the location of the custom templates
     * folder in it
     * @throws IOException
     *             if the configuration file could not created
     */
    public void createConfigFile() throws IOException {
        Path path = Paths.get(cobigenUtils.getCobigenCliRootPath() + File.separator + COBIGEN_CONFIG);
        Properties props = new Properties();
        props.setProperty("cobigen.custom-templates-location", customTemplatesLocation.toString());
        props.store(new FileOutputStream(path.toFile()), MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION);
    }

    /**
     * Reads the configuration file and returns all of its properties
     * @return Properties
     */
    public Properties readConfigFileProperties() {
        Properties props = new Properties();

        Path path = Paths.get(cobigenUtils.getCobigenCliRootPath() + File.separator + COBIGEN_CONFIG);
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            props.load(reader);
        } catch (IOException e) {
            logger.error("An error occured while reading the config file", e);
        }
        return props;
    }

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        logger.info("Running AdaptTemplatesCommand!, please wait...");

        if (customTemplatesLocation != null) {
            logger.info("Creating Templates folder at custom location {}", customTemplatesLocation);
            customTemplatesLocation = cobigenUtils.preprocessInputFile(customTemplatesLocation);
            createConfigFile();
        }
        Properties props = readConfigFileProperties();

        Path cobigenTemplatesDirectory = null;
        if (props != null) {
            // sets custom templates directory path from configuration file property
            cobigenTemplatesDirectory = Paths.get(props.getProperty("cobigen.custom-templates-location"));
        } else {
            // sets default templates directory path from CLI location
            cobigenTemplatesDirectory = Paths.get(cobigenUtils.getCobigenTemplatesFolderFile().toURI());
        }

        if (Files.exists(cobigenTemplatesDirectory)) {
            processJar(cobigenTemplatesDirectory);
        } else {
            logger.error("{} does not exist!", cobigenTemplatesDirectory);
            return 0;
        }

        logger.info("Successfully created custom templates folder @ {}", cobigenTemplatesDirectory);
        return 1;
    }
}
