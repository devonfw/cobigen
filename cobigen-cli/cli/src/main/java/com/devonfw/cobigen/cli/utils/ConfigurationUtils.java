package com.devonfw.cobigen.cli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;

/**
 *
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
     * Name of configuration key for custom templates file path
     */
    private static final String COBIGEN_CONFIG_LOCATION_KEY = "cobigen.custom-templates-location";

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * @return Path of custom templates location or null
     */
    public Path getCustomTemplatesLocation() {
        if (Files.exists(Paths.get(getCobigenCliRootPath() + File.separator + COBIGEN_CONFIG))) {
            Properties props = readConfigFileProperties();
            return Paths.get(props.getProperty(COBIGEN_CONFIG_LOCATION_KEY));
        } else {
            return null;
        }
    }

    /**
     * @return Path of Cobigen CLI root
     */
    public Path getCobigenCliRootPath() {
        Path rootCLIPath = null;
        try {
            File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            rootCLIPath = locationCLI.getParentFile().toPath();
        } catch (URISyntaxException e) {
            logger.error("An error occured while building the URI of the CLI location {}", e);
        }
        return rootCLIPath;
    }

    /**
     * @return File of Cobigen templates folder
     */
    public File getCobigenTemplatesFolderFile() {
        String pathForCobigenTemplates = "";

        Path customTemplatesLocation = getCustomTemplatesLocation();
        if (customTemplatesLocation != null) {
            pathForCobigenTemplates = getCustomTemplatesLocation().toString();
        } else {
            pathForCobigenTemplates = getCobigenCliRootPath().toString();
        }

        // initializes filesystem and sets cobigenTemplatesFolderPath
        FileSystem fileSystem = FileSystems.getDefault();
        Path cobigenTemplatesFolderPath = null;
        if (fileSystem != null && fileSystem.getPath(pathForCobigenTemplates) != null) {
            cobigenTemplatesFolderPath =
                fileSystem.getPath(pathForCobigenTemplates + File.separator + COBIGEN_TEMPLATES);
        }
        return cobigenTemplatesFolderPath.toFile();
    }

    /**
     *
     * @param classLoader
     *            Classloader to load resources from
     * @return URL of context configuration
     * @throws IOException
     *             if no context.xml was found
     */
    public URL getContextConfiguration(ClassLoader classLoader) throws IOException {
        URL contextConfigurationLocation = null;
        String[] possibleLocations = new String[] { "context.xml", "src/main/templates/context.xml" };

        for (String possibleLocation : possibleLocations) {
            URL configLocation = classLoader.getResource(possibleLocation);
            if (configLocation != null) {
                contextConfigurationLocation = configLocation;
                logger.debug("Found context.xml @ " + contextConfigurationLocation.toString());
                break;
            }
        }

        if (contextConfigurationLocation == null) {
            throw new IOException("No context.xml could be found in the classloader!");
        } else {
            // Make sure to create file system
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            URI uri = URI.create(contextConfigurationLocation.toString());
            FileSystem fs;
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                fs = FileSystems.newFileSystem(uri, env);
            }
            Paths.get(uri);
        }
        return contextConfigurationLocation;
    }

    /**
     * Creates a configuration file next to the CLI executable and stores the location of the custom templates
     * folder in it
     * @param customTemplatesLocation
     * @throws IOException
     *             if the configuration file could not created
     */
    public void createConfigFile(File customTemplatesLocation) throws IOException {
        Path path = Paths.get(getCobigenCliRootPath() + File.separator + COBIGEN_CONFIG);
        Properties props = new Properties();
        props.setProperty(COBIGEN_CONFIG_LOCATION_KEY, customTemplatesLocation.toString());
        props.store(new FileOutputStream(path.toFile()), MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION);
    }

    /**
     * Reads the configuration file and returns all of its properties
     * @return Properties
     */
    public Properties readConfigFileProperties() {
        Properties props = new Properties();

        Path path = Paths.get(getCobigenCliRootPath() + File.separator + COBIGEN_CONFIG);
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            props.load(reader);
        } catch (IOException e) {
            logger.error("An error occured while reading the config file", e);
        }
        return props;
    }

    /**
     * Processes the input file's path. Strips the quotes from the file path if they are given.
     * @param inputFile
     *            the input file
     * @return input file with processed path
     */
    public File preprocessInputFile(File inputFile) {
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

    /**
     * Tries to load a class over it's file path. If the path is /a/b/c/Some.class this method tries to load
     * the following classes in this order: <list>
     * <li>Some</li>
     * <li>c.Some</li>
     * <li>b.c.Some</li>
     * <li>a.b.c.Some</> </list>
     * @param classPath
     *            the {@link Path} of the Class file
     * @param cl
     *            the used ClassLoader
     * @return Class<?> of the class file
     * @throws ClassNotFoundException
     *             if no class could be found all the way up to the path root
     */
    public Class<?> loadClassByPath(Path classPath, ClassLoader cl) throws ClassNotFoundException {
        // Get a list with all path segments, starting with the class name
        Queue<String> pathSegments = new LinkedList<>();
        // Split the path by the systems file separator and without the .class suffix
        String[] pathSegmentsArray = classPath.toString().substring(0, classPath.toString().length() - 6)
            .split("\\".equals(File.separator) ? "\\\\" : File.separator);
        for (int i = pathSegmentsArray.length - 1; i > -1; i--) {
            pathSegments.add(pathSegmentsArray[i]);
        }

        if (!pathSegments.isEmpty()) {
            String className = "";
            while (!pathSegments.isEmpty()) {
                if (className == "") {
                    className = pathSegments.poll();
                } else {
                    className = pathSegments.poll() + "." + className;
                }
                try {
                    logger.debug("Try to load " + className);
                    return cl.loadClass(className);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    continue;
                }
            }
        }
        throw new ClassNotFoundException("Could not find class on path " + classPath.toString());

    }
}
