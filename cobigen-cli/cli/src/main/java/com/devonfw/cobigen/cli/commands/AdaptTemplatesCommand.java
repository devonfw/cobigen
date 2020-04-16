package com.devonfw.cobigen.cli.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.mmm.util.io.api.IoMode;
import net.sf.mmm.util.io.api.RuntimeIoException;

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
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Constructor needed for Picocli
     */
    public AdaptTemplatesCommand() {
        super();
    }

    /**
     * Process Jar method is responsible for unzip the source Jar and create new CobiGen_Templates folder
     * structure at /main/CobiGen_Templates location
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

        // If we are unzipping a sources jar, we need to get the pom.xml from the normal jar
        if (sourcesJarPath.resolve("sources") != null) {
            try (ZipFile file = new ZipFile(classesJarPath.toFile().toString())) {
                Enumeration<? extends ZipEntry> entries = file.entries();
                if (Files.notExists(templatesFolderPath)) {
                    Files.createDirectory(templatesFolderPath);
                }
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("pom.xml")) {
                        Path saveForFileCreationPath = templatesFolderPath.resolve(entry.getName());
                        createFile(file, entry, saveForFileCreationPath);
                    }
                }
            } catch (IOException e) {
                throw new IOException("An exception occurred while unpacking pom.xml from Jar file to templates folder",
                    e);
            }
        }

        // unpack sources
        try (ZipFile file = new ZipFile(sourcesJarPath.toFile().toString())) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            if (Files.notExists(templatesFolderPath)) {
                Files.createDirectory(templatesFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path saveForFileCreationPath = templatesFolderPath.resolve(entry.getName());
                if (entry.getName().contains("context.xml")) {
                    saveForFileCreationPath = templatesFolderPath.resolve(entry.getName());
                } else if (entry.getName().contains("com/")) {
                    saveForFileCreationPath =
                        templatesFolderPath.resolve("src").resolve("main").resolve("java").resolve(entry.getName());
                }
                createFile(file, entry, saveForFileCreationPath);
            }
        } catch (IOException e) {
            throw new IOException("An exception occurred while unpacking sources from Jar file to templates folder", e);
        }

        // unpack classes to target directory
        try (ZipFile file = new ZipFile(classesJarPath.toFile().toString())) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            Path sourcesClassPath = templatesFolderPath.resolve(ConfigurationUtils.COBIGEN_UTILITY_CLASSES_FOLDER);
            if (Files.notExists(templatesFolderPath)) {
                Files.createDirectory(templatesFolderPath);
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().contains("com/")) {
                    Path saveForFileCreationPath =
                        sourcesClassPath.resolve("src").resolve("main").resolve("java").resolve(entry.getName());
                    createFile(file, entry, saveForFileCreationPath);
                }
            }
        } catch (IOException e) {
            throw new IOException("An exception occurred while unpacking classes from Jar files to templates folder",
                e);
        }
    }

    /**
     * Creates a file or directory and cleans up current file if it exists already
     * @param file
     *            ZipFile to access
     * @param entry
     *            ZipEntry to get input stream from
     * @param saveForFileCreationPath
     *            Path to save file at
     * @throws IOException
     *             if file could not be created
     */
    private static void createFile(ZipFile file, ZipEntry entry, Path saveForFileCreationPath) throws IOException {
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
            } catch (IOException e) {
                throw new RuntimeIoException(e, IoMode.WRITE);
            }
        }
    }

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        LOG.info("Running AdaptTemplatesCommand!, please wait...");

        Path cobigenTemplatesDirectory = null;
        if (customTemplatesLocation != null) {
            customTemplatesLocation = ConfigurationUtils.preprocessInputFile(customTemplatesLocation);
            ConfigurationUtils.createConfigFile(customTemplatesLocation);
            // sets custom templates directory path from configuration file property
            cobigenTemplatesDirectory = ConfigurationUtils.getCustomTemplatesLocation();
            LOG.info("Creating custom templates folder at custom location @ {}", cobigenTemplatesDirectory);
        } else {
            // sets default templates directory path from CLI location
            cobigenTemplatesDirectory = ConfigurationUtils.getCobigenCliRootPath();
            ConfigurationUtils.createConfigFile(cobigenTemplatesDirectory.toFile());
            LOG.info("Creating custom templates folder next to the CLI @ {}", cobigenTemplatesDirectory);
        }

        if (Files.exists(cobigenTemplatesDirectory)) {
            processJar(cobigenTemplatesDirectory);
        } else {
            LOG.error("Could not find target directory to extract templates @", cobigenTemplatesDirectory);
            return 0;
        }

        LOG.info("Successfully created custom templates folder @ {}",
            cobigenTemplatesDirectory + File.separator + ConfigurationUtils.COBIGEN_TEMPLATES);
        return 1;
    }
}
