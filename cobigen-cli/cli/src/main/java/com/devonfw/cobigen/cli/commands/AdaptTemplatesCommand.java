package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.concurrent.Callable;

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
 * This class handles the user defined template directory e.g. determining and obtaining the latest templates
 * jar, unpacking the sources and compiled classes and storing the custom location path in a configuration
 * file
 */
@Command(description = MessagesConstants.ADAPT_TEMPLATES_DESCRIPTION, name = "adapt-templates", aliases = { "a" },
    mixinStandardHelpOptions = true)
public class AdaptTemplatesCommand implements Callable<Integer> {

    /**
     * Utils class for CobiGen related operations
     */
    private static CobiGenUtils cobigenUtils = new CobiGenUtils();

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    private boolean verbose;

    /**
     * If this option is provided, we will unpack the templates jar at the given location
     */
    @Option(names = { "--custom-location", "-cl" }, arity = "0..1",
        description = MessagesConstants.CUSTOM_LOCATION_OPTION_DESCRIPTION)
    private Path customTemplatesLocation = null;

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Unpacks the source CobiGen_Templates Jar and creates a new CobiGen_Templates folder structure at
     * $destinationPath/CobiGen_Templates location
     *
     * @param destinationPath
     *            path to be used as target directory
     * @throws IOException
     *             if no destination path could be set
     */
    public static void processJar(Path destinationPath) throws IOException {

        Path sourcesJarPath = cobigenUtils.getTemplatesJar(true);
        Path classesJarPath = cobigenUtils.getTemplatesJar(false);

        if (destinationPath == null) {
            throw new IOException("Cobigen folder path not found!");
        }

        Path templatesFolderPath = destinationPath.resolve(ConfigurationUtils.COBIGEN_TEMPLATES);

        if (templatesFolderPath == null) {
            throw new IOException("Cobigen templates folder path not found!");
        }

        LOG.debug("Processing jar file @ {}", sourcesJarPath);

        extractArchive(sourcesJarPath, templatesFolderPath);
        deleteDirectoryRecursively(templatesFolderPath.resolve("META-INF"));

        extractArchive(classesJarPath, templatesFolderPath.resolve(ConfigurationUtils.COBIGEN_UTILITY_CLASSES_FOLDER));
        Files.deleteIfExists(templatesFolderPath.resolve("pom.xml"));
        // If we are unzipping a sources jar, we need to get the pom.xml from the normal jar
        Files.copy(templatesFolderPath.resolve("target/classes/pom.xml"), templatesFolderPath.resolve("pom.xml"));
        Files.deleteIfExists(templatesFolderPath.resolve("target/classes/pom.xml"));
        deleteDirectoryRecursively(templatesFolderPath.resolve("target/classes/src"));
        deleteDirectoryRecursively(templatesFolderPath.resolve("target/classes/META-INF"));

    }

    /**
     * Deletes a directory and its sub directories recursively
     *
     * @param pathToBeDeleted
     *            the directory which should be deleted recursively
     * @throws IOException
     *             if the file could not be deleted
     */
    public static void deleteDirectoryRecursively(Path pathToBeDeleted) throws IOException {
        Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    /**
     * Extracts an archive as is to a target directory while keeping its folder structure
     *
     * @param sourcePath
     *            Path of the archive to unpack
     * @param targetPath
     *            Path of the target directory to unpack the source archive to
     * @throws IOException
     *             if an error occurred while processing the jar or its target directory
     */
    private static void extractArchive(Path sourcePath, Path targetPath) throws IOException {

        // TODO: janv_capgemini check if sourcePath is an archive and throw exception if not
        FileSystem fs = FileSystems.newFileSystem(sourcePath, null);

        Path path = fs.getPath("/");
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = path.relativize(file);
                Path targetPathResolved = targetPath.resolve(relativePath.toString());
                Files.deleteIfExists(targetPathResolved);
                Files.createDirectories(targetPathResolved.getParent());
                Files.copy(file, targetPathResolved);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // Log errors but do not throw an exception
                LOG.warn("An IOException occurred while reading a file on path {} with message: {}", file,
                    exc.getMessage());
                LOG.debug("An IOException occurred while reading a file on path {} with message: {}", file,
                    LOG.isDebugEnabled() ? exc : null);
                return FileVisitResult.CONTINUE;
            }
        });

    }

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        Path cobigenTemplatesDirectory = null;
        if (customTemplatesLocation != null) {

            LOG.info(
                "CobiGen is attempting to download the latest CobiGen_Templates.jar and will extract it to a target directory of your choice. please wait...");
            ConfigurationUtils.createConfigFile(customTemplatesLocation);
            // sets custom templates directory path from configuration file property
            cobigenTemplatesDirectory = ConfigurationUtils.getCustomTemplatesLocation();
            LOG.info("Creating custom templates folder at custom location @ {}", cobigenTemplatesDirectory);
        } else {
            LOG.info(
                "CobiGen is attempting to download the latest CobiGen_Templates.jar and will extract it to a default target directory. please wait...");
            // sets default templates directory path from CLI location
            cobigenTemplatesDirectory = ConfigurationUtils.getCobigenCliRootPath();
            ConfigurationUtils.createConfigFile(cobigenTemplatesDirectory);
            LOG.info("Creating custom templates folder next to the CLI @ {}", cobigenTemplatesDirectory);
        }

        if (Files.exists(cobigenTemplatesDirectory)) {
            processJar(cobigenTemplatesDirectory);
        } else {
            LOG.error("Could not find target directory to extract templates @ {}", cobigenTemplatesDirectory);
            return 1;
        }

        LOG.info("Successfully created custom templates folder @ {}",
            cobigenTemplatesDirectory.resolve(ConfigurationUtils.COBIGEN_TEMPLATES));
        return 0;
    }
}
