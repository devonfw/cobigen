package com.devonfw.cobigen.impl.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;

/**
 * Util to extract Templates
 */
public class ExtractTemplatesUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ExtractTemplatesUtil.class);

  /**
   * Extracts template sets to the given path
   *
   * @param extractTo Path to extract the templates into
   * @param forceOverride force to overwrite the contents of the target folder
   * @throws DirectoryNotEmptyException if the given directory is not empty. Can be used to ask for overwriting
   */
  public static void extractTemplates(Path extractTo, boolean forceOverride) throws DirectoryNotEmptyException {

    // find templates will also download jars if needed as a side effect and will return the path to the
    // files.
    URI findTemplatesLocation = ConfigurationFinder.findTemplatesLocation();

    Path templatesLocationFolder = Paths.get(findTemplatesLocation);

    if (Files.exists(templatesLocationFolder)
        && templatesLocationFolder.endsWith(ConfigurationConstants.COBIGEN_TEMPLATES)) {
      LOG.info(
          "Your are using an old Templates project at {}. You can edit them in place to adapt your generation results.",
          templatesLocationFolder);
      return;
    }

    if (Files.exists(templatesLocationFolder.resolve(ConfigurationConstants.ADAPTED_FOLDER))) {
      LOG.info("Templates already found at {}. You can edit them in place to adapt your generation results.",
          extractTo);
      return;
    }

    Objects.requireNonNull(extractTo, "Target path cannot be null");
    if (!Files.isDirectory(extractTo)) {
      try {
        Files.createDirectories(extractTo);
      } catch (IOException e) {
        throw new CobiGenRuntimeException("Unable to create directory " + extractTo);
      }
    }

    try {
      if (!isEmpty(extractTo) && !forceOverride) {
        throw new DirectoryNotEmptyException(extractTo.toString());
      }

      LOG.info(
          "CobiGen is attempting to download the latest template sets jars and will extract them to cobigen home directory {}. please wait...",
          ConfigurationConstants.ADAPTED_FOLDER);
      Path templatesDirectory = extractTo;
      processJars(templatesDirectory);
      LOG.info("Successfully downloaded and extracted templates to @ {}", templatesDirectory);
    } catch (DirectoryNotEmptyException e) {
      throw e;
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Not able to extract templates to " + extractTo, e);
    }
  }

  /**
   * Unpacks the source CobiGen_Templates Jar and creates a new CobiGen_Templates folder structure at
   * $destinationPath/CobiGen_Templates location
   *
   * @param destinationPath path to be used as target directory
   * @throws IOException if no destination path could be set
   *
   * @deprecated use processJars instead
   */
  @Deprecated
  private static void processJar(Path destinationPath) throws IOException {

    if (destinationPath == null) {
      throw new IOException("Cobigen folder path not found!");
    }

    Path cobigenTemplatesPath = CobiGenPaths.getTemplatesFolderPath();

    Path sourcesJarPath = TemplatesJarUtil.getJarFile(true, cobigenTemplatesPath);
    Path classesJarPath = TemplatesJarUtil.getJarFile(false, cobigenTemplatesPath);

    LOG.debug("Processing jar file @ {}", sourcesJarPath);

    // extract sources jar to target directory
    extractArchive(sourcesJarPath, destinationPath);

    // create src/main/java directory
    Files.createDirectory(destinationPath.resolve("src/main/java"));

    // move com folder to src/main/java/com
    Files.move(destinationPath.resolve("com"), destinationPath.resolve("src/main/java/com"),
        StandardCopyOption.REPLACE_EXISTING);

    // create src/main/resources directory
    Files.createDirectory(destinationPath.resolve("src/main/resources"));

    // move META-INF folder to src/main/resources
    Files.move(destinationPath.resolve("META-INF"), destinationPath.resolve("src/main/resources/META-INF"),
        StandardCopyOption.REPLACE_EXISTING);

    // delete MANIFEST.MF
    Files.deleteIfExists(destinationPath.resolve("src/main/resources/META-INF/MANIFEST.MF"));

    URI zipFile = URI.create("jar:file:" + classesJarPath.toUri().getPath());

    // extract classes jar pom.xml
    try (FileSystem fs = FileSystemUtil.getOrCreateFileSystem(zipFile)) {
      Files.copy(fs.getPath("pom.xml"), destinationPath.resolve("pom.xml"), StandardCopyOption.REPLACE_EXISTING);
    }

  }

  /**
   * Unpacks the template set jars located in downloaded folder and creates a new folder structure for each template set
   * at $destinationPath/ location
   *
   * @param destinationPath path to be used as target directory
   * @throws IOException if no destination path could be set
   */
  private static void processJars(Path destinationPath) throws IOException {

    if (destinationPath == null) {
      throw new IOException("Cobigen folder path not found!");
    }

    Path cobigenDownloadedTemplateSetsPath = CobiGenPaths.getTemplateSetsFolderPath()
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    if (Files.exists(cobigenDownloadedTemplateSetsPath)) {
      List<Path> templateJars = TemplatesJarUtil.getJarFiles(cobigenDownloadedTemplateSetsPath);
      for (Path templateSetJar : templateJars) {
        LOG.debug("Processing jar file @ {}", templateSetJar);
        String fileName = templateSetJar.getFileName().toString().replace(".jar", "");
        Path destination = destinationPath.resolve(fileName);
        extractArchive(templateSetJar, destination);

        if (Files.exists(destination.resolve("com"))) {
          // move com folder to src/main/java/com. Needed for the utils project
          Files.createDirectories(destination.resolve("src/main/java"));
          Files.move(destination.resolve("com"), destination.resolve("src/main/java/com"),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } else {
      LOG.info("No downloaded templates found in {} to extract", cobigenDownloadedTemplateSetsPath);
    }
  }

  /**
   * Deletes a directory and its sub directories recursively
   *
   * @param pathToBeDeleted the directory which should be deleted recursively
   * @throws IOException if the file could not be deleted
   */
  public static void deleteDirectoryRecursively(Path pathToBeDeleted) throws IOException {

    Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  /**
   * Extracts an archive as is to a target directory while keeping its folder structure
   *
   * @param sourcePath Path of the archive to unpack
   * @param targetPath Path of the target directory to unpack the source archive to
   * @throws IOException if an error occurred while processing the jar or its target directory
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
        LOG.warn("An IOException occurred while reading a file on path {} with message: {}", file, exc.getMessage());
        LOG.debug("An IOException occurred while reading a file on path {} with message: {}", file,
            LOG.isDebugEnabled() ? exc : null);
        return FileVisitResult.CONTINUE;
      }
    });

  }

  /**
   * Check if directory is empty
   *
   * @param directory to be checked
   * @return <code>true</code> if is empty, <code>false</code> otherwise
   * @throws IOException in case the directory could not be read
   */
  private static boolean isEmpty(final Path directory) throws IOException {

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
      return !dirStream.iterator().hasNext();
    }
  }
}
