package com.devonfw.cobigen.cli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;

/**
 * Utilities class for CobiGen related operations. Handles everything related to custom templates folder
 * destination and configuration file management
 */
public class ConfigurationUtils {

    /**
     * Name of templates folder
     */
    public static final String COBIGEN_TEMPLATES = "CobiGen_Templates";

    /**
     * Name of configuration file
     */
    private static final String COBIGEN_CONFIG = "config.txt";

    /**
     * Folder to store template utility classes in
     */
    public static final String COBIGEN_UTILITY_CLASSES_FOLDER = "target/classes";

    /**
     * Name of configuration key for custom templates file path
     */
    private static final String COBIGEN_CONFIG_LOCATION_KEY = "cobigen.custom-templates-location";

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Checks if the configuration file exists and returns the path of the custom templates location key
     * @return Path of custom templates location or null
     */
    public static Path getCustomTemplatesLocation() {
        if (Files.exists(getCobigenCliRootPath().resolve(COBIGEN_CONFIG))) {
            Properties props = readConfigFileProperties();
            return Paths.get(props.getProperty(COBIGEN_CONFIG_LOCATION_KEY));
        } else {
            return null;
        }
    }

    /**
     * @return Path of Cobigen CLI root
     */
    public static Path getCobigenCliRootPath() {
        Path rootCLIPath = null;
        try {
            File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            rootCLIPath = locationCLI.getParentFile().toPath();
        } catch (URISyntaxException e) {
            LOG.error("An URISyntaxException occured while building the URI of the CLI location", e);
        }
        return rootCLIPath;
    }

    /**
     * Checks if custom templates location path exists and either returns the path of the default location
     * next to the CLI or the custom templates location defined in the configuration
     * @return Path of Cobigen templates folder, null if the folder does not exist
     */
    public static Path getCobigenTemplatesFolderPath() {
        Path pathForCobigenTemplates;

        Path customTemplatesLocation = getCustomTemplatesLocation();
        if (customTemplatesLocation != null) {
            pathForCobigenTemplates = getCustomTemplatesLocation();
        } else {
            pathForCobigenTemplates = getCobigenCliRootPath();
        }

        Path cobigenTemplatesFolder = null;
        if (pathForCobigenTemplates != null) {
            cobigenTemplatesFolder = pathForCobigenTemplates.resolve(COBIGEN_TEMPLATES);
        }

        if (Files.exists(cobigenTemplatesFolder)) {
            return cobigenTemplatesFolder;
        } else {
            return null;
        }
    }

    /**
     * @return boolean true if the folder specified in configuration file exists, false if not
     */
    public static boolean customTemplatesLocationExists() {

        Path pathForCobigenTemplates = getCustomTemplatesLocation();

        Path cobigenTemplatesFolder = null;
        if (pathForCobigenTemplates != null) {
            cobigenTemplatesFolder = pathForCobigenTemplates.resolve(COBIGEN_TEMPLATES);
        }

        if (pathForCobigenTemplates != null && !Files.exists(cobigenTemplatesFolder)) {
            LOG.info("Please check your configuration file as no templates folder was found at the provided path!");
            return false;
        }
        return true;
    }

    /**
     * Creates a configuration file next to the CLI executable and stores the location of the custom templates
     * folder in it
     * @param customTemplatesLocation
     *            File location to store in configuration file
     * @throws IOException
     *             if the configuration file could not created
     */
    public static void createConfigFile(Path customTemplatesLocation) throws IOException {
        Path path = getCobigenCliRootPath().resolve(COBIGEN_CONFIG);
        Properties props = new Properties();
        props.setProperty(COBIGEN_CONFIG_LOCATION_KEY, customTemplatesLocation.toString());
        props.store(new FileOutputStream(path.toFile()), MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION);
    }

    /**
     * Reads the configuration file and returns all of its properties
     * @return Properties
     */
    public static Properties readConfigFileProperties() {
        Properties props = new Properties();

        Path path = getCobigenCliRootPath().resolve(COBIGEN_CONFIG);
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            props.load(reader);
        } catch (IOException e) {
            LOG.error("An error occured while reading the config file", e);
        }
        return props;
    }

    /**
     * Processes the input file's path. Strips the quotes from the file path if they are given.
     * @param inputFile
     *            the input file
     * @return input file with processed path
     */
    public static File preprocessInputFile(File inputFile) {
        String path = inputFile.getPath();
        String pattern = "[\\\"|\\'](.+)[\\\"|\\']";
        boolean matches = path.matches(pattern);
        if (matches) {
            path = path.replace("\"", "");
            path = path.replace("\'", "");
            return new File(path);
        }

        return inputFile;
    }
}
