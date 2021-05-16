package com.devonfw.cobigen.impl.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.ConfigurationUtil;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;

/**
 * Util to extract Templates
 */
public class ExtractTemplatesUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ExtractTemplatesUtil.class);

    /**
     * Extracts templates project to the given path
     * @param extractTo
     *            Path to extract the templates into
     * @param forceOverride
     *            force to overwrite the contents of the target folder
     * @throws DirectoryNotEmptyException
     *             if the given directory is not empty. Can be used to ask for overwriting
     */
    public static void extractTemplates(Path extractTo, boolean forceOverride) throws DirectoryNotEmptyException {
        Objects.requireNonNull(extractTo, "Target path cannot be null");
        if (!Files.isDirectory(extractTo)) {
            throw new CobiGenRuntimeException(extractTo + " is not a directory");
        }
        try {
            if (!isEmpty(extractTo) && !forceOverride) {
                throw new DirectoryNotEmptyException(extractTo.toString());
            }

            LOG.info(
                "CobiGen is attempting to download the latest CobiGen_Templates.jar and will extract it to cobigen home directory {}. please wait...",
                ConfigurationConstants.DEFAULT_HOME);
            File templatesDirectory = ConfigurationUtil.getTemplatesFolderPath().toFile();
            TemplatesJarUtil.downloadLatestDevon4jTemplates(true, templatesDirectory);
            TemplatesJarUtil.downloadLatestDevon4jTemplates(false, templatesDirectory);
            processJar(templatesDirectory.toPath());
            LOG.info("Successfully downloaded and extracted templates to @ {}",
                templatesDirectory.toPath().resolve(ConfigurationConstants.COBIGEN_TEMPLATES));
        } catch (DirectoryNotEmptyException e) {
            throw e;
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Not able to extract templates to ", e);
        }
    }

    /**
     * Unpacks the source CobiGen_Templates Jar and creates a new CobiGen_Templates folder structure at
     * $destinationPath/CobiGen_Templates location
     *
     * @param destinationPath
     *            path to be used as target directory
     * @throws IOException
     *             if no destination path could be set
     */
    private static void processJar(Path destinationPath) throws IOException {
        if (destinationPath == null) {
            throw new IOException("Cobigen folder path not found!");
        }

        File destinationFile = destinationPath.toFile();
        Path sourcesJarPath = TemplatesJarUtil.getJarFile(true, destinationFile).toPath();
        Path classesJarPath = TemplatesJarUtil.getJarFile(false, destinationFile).toPath();

        LOG.debug("Processing jar file @ {}", sourcesJarPath);

        extractArchive(sourcesJarPath, destinationPath);
        deleteDirectoryRecursively(destinationPath.resolve("META-INF"));

        extractArchive(classesJarPath, destinationPath.resolve("target/classes"));
        Files.deleteIfExists(destinationPath.resolve("pom.xml"));
        // If we are unzipping a sources jar, we need to get the pom.xml from the normal jar
        Files.copy(destinationPath.resolve("target/classes/pom.xml"), destinationPath.resolve("pom.xml"));
        Files.deleteIfExists(destinationPath.resolve("target/classes/pom.xml"));
        deleteDirectoryRecursively(destinationPath.resolve("target/classes/src"));
        deleteDirectoryRecursively(destinationPath.resolve("target/classes/META-INF"));
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

    /**
     * Check if directory is empty
     *
     * @param directory
     *            to be checked
     * @return <code>true</code> if is empty, <code>false</code> otherwise
     * @throws IOException
     *             in case the directory could not be read
     */
    private static boolean isEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}
