package com.devonfw.cobigen.impl.adapter;

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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.upgrade.AbstractConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * Implementation of {@link TemplateAdapter}. Provides methods for adapting template sets as well as the old monolithic
 * template structure.
 */
public class TemplateAdapterImpl implements TemplateAdapter {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateAdapterImpl.class);

  /** The parent location of the template. */
  private Path templatesLocation;

  /**
   * Creates a new {@link TemplateAdapter} instance. The location of the templates if is not specified, so the location
   * is searched by the core module itself. Or if specified, handles the adaption of templates at the given templates
   * location.
   *
   * @param templatesLocation The {@link Path} of the location to search the templates for.
   */
  public TemplateAdapterImpl(Path templatesLocation) {

    if (templatesLocation == null) {
      URI templatesLocationPath = ConfigurationFinder.findTemplatesLocation();
      if (Files.exists(Paths.get(templatesLocationPath))) {
        this.templatesLocation = Paths.get(templatesLocationPath);
        if (this.templatesLocation.getFileName().toString().contains(".jar")) {
          this.templatesLocation = this.templatesLocation.getParent();
        }

      }
    } else {
      this.templatesLocation = templatesLocation;
    }
  }

  @Override
  public void adaptTemplates()
      throws IOException, UpgradeTemplatesNotificationException, TemplateSelectionForAdaptionException {

    if (isMonolithicTemplatesConfiguration()) {
      Path destinationPath = this.templatesLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      adaptMonolithicTemplates(destinationPath, false);
      throw new UpgradeTemplatesNotificationException();
    } else {
      throw new TemplateSelectionForAdaptionException(getTemplateSetJars());
    }
  }

  @Override
  public void adaptTemplateSets(List<Path> templateSetJars, boolean forceOverride) throws IOException {

    Path destinationPath = this.templatesLocation.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    adaptTemplateSets(templateSetJars, destinationPath, forceOverride);
  }

  @Override
  public void adaptTemplateSets(List<Path> templateSetJars, Path destinationPath, boolean forceOverride)
      throws IOException {

    try {
      processTemplateSetJars(templateSetJars, destinationPath, forceOverride);
      LOG.info("Successfully extracted templates to @ {}", destinationPath);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Not able to extract templates to " + destinationPath, e);
    }
  }

  /**
   * Extracts a specified set of template jars to the specified target. The list is specified by the user in CLI or
   * Eclipse module.
   *
   * @param templateSetJarsToAdapt A {@link List} of {@link Path} with the template jars to adapt.
   * @param destinationPath The {@link Path} where the jars should be extracted to
   * @param forceOverride Indicator whether an already adapted template set should be overridden
   * @throws IOException If CobiGen is not able to extract the jar file to the destination folder
   */
  private void processTemplateSetJars(List<Path> templateSetJarsToAdapt, Path destinationPath, boolean forceOverride)
      throws IOException {

    for (Path templateSetJar : templateSetJarsToAdapt) {
      LOG.debug("Processing jar file @ {}", templateSetJar);
      String fileName = templateSetJar.getFileName().toString().replace(".jar", "");
      Path destination = destinationPath.resolve(fileName);

      boolean extract = false;
      try {
        extract = validatePaths(destination, forceOverride);
      } catch (IOException e) {
        LOG.info("Unable to extract template jar file to {}", destination);
      }

      if (extract) {
        if (Files.exists(destination) && forceOverride) {
          LOG.info("Override the existing destination folder {}", destination);
          deleteDirectoryRecursively(destination);
        }

        extractArchive(templateSetJar, destination);
        // com folder with precompiled util classes is not needed. The utils compiled at first generation into the
        // target folder
        if (Files.exists(destination.resolve("com"))) {
          FileUtils.deleteDirectory(destination.resolve("com").toFile());
        }
      }
    }
  }

  @Override
  public void adaptMonolithicTemplates(boolean forceOverride) throws IOException {

    Path destinationPath = this.templatesLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    this.adaptMonolithicTemplates(destinationPath, forceOverride);
  }

  @Override
  public void adaptMonolithicTemplates(Path destinationPath, boolean forceOverride) throws IOException {

    if (validatePaths(destinationPath, forceOverride)) {
      try {
        extractMonolithicJar(destinationPath, forceOverride);
      } catch (IOException e) {
        throw new CobiGenRuntimeException("Not able to extract monolithic templates to " + destinationPath, e);
      }
    }
  }

  /**
   * Unpacks the source CobiGen_Templates Jar and creates a new CobiGen_Templates folder structure at
   * $destinationPath/CobiGen_Templates location
   *
   * @param destinationPath path to be used as target directory
   * @throws IOException if no destination path could be set
   *
   */
  private void extractMonolithicJar(Path destinationPath, boolean forceOverride) throws IOException {

    if (!isEmpty(destinationPath) && forceOverride) {
      LOG.info("Override the existing destination folder {}", destinationPath);
      deleteDirectoryRecursively(destinationPath);
    }

    Path sourcesJarPath = TemplatesJarUtil.getJarFile(true, this.templatesLocation);
    Path classesJarPath = TemplatesJarUtil.getJarFile(false, this.templatesLocation);

    if (sourcesJarPath == null && classesJarPath == null) {
      LOG.info("No monolithic jar found in {}!", this.templatesLocation);
      return;
    }

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

    LOG.info("Successfully extracted templates to @ {}", destinationPath);
  }

  @Override
  public List<Path> getTemplateSetJars() {

    Path downloadedJarsFolder = this.templatesLocation.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    if (!Files.exists(downloadedJarsFolder)) {
      LOG.info("No template set jars found. Folder {} does not exist.", downloadedJarsFolder);
      return null;
    }
    return TemplatesJarUtil.getJarFiles(downloadedJarsFolder);
  }

  @Override
  public boolean isMonolithicTemplatesConfiguration() {

    if (this.templatesLocation != null
        && this.templatesLocation.getFileName().endsWith(ConfigurationConstants.TEMPLATE_SETS_FOLDER)) {
      return false;
    }
    return true;
  }

  /**
   * Get the location to load the templates from.
   *
   * @return templatesLocation The {@link Path} where the templates are located.
   */
  @Override
  public Path getTemplatesLocation() {

    return this.templatesLocation;
  }

  @Override
  public boolean isTemplateSetAlreadyAdapted(Path templateSetJar) {

    if (templateSetJar != null && Files.exists(templateSetJar)) {
      Path adaptedFolder = this.templatesLocation.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      if (Files.exists(adaptedFolder)
          && Files.exists(adaptedFolder.resolve(templateSetJar.getFileName().toString().replace(".jar", "")))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Extracts an archive as is to a target directory while keeping its folder structure
   *
   * @param sourcePath Path of the archive to unpack
   * @param targetPath Path of the target directory to unpack the source archive to
   * @throws IOException if an error occurred while processing the jar or its target directory
   */
  private void extractArchive(Path sourcePath, Path targetPath) throws IOException {

    if (FileSystemUtil.isZipFile(sourcePath.toUri())) {
      // Important cast for jdk 17 compatibility
      FileSystem fs = FileSystems.newFileSystem(sourcePath, (ClassLoader) null);

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
    } else {
      LOG.info("Source path is not a ZIP file {}", sourcePath);
    }
  }

  /**
   * Validates the given source and destination paths.
   *
   * @param destinationPath The {@link Path} to adapt the template into
   * @param forceOverride Indicator if already adapted templates should be overridden
   * @return Returns {@code true} the path are valid and the templates can be extracted to the given path
   * @throws IOException
   */
  private boolean validatePaths(Path destinationPath, boolean forceOverride) throws IOException {

    Objects.requireNonNull(this.templatesLocation, "Templates location cannot be null");
    Objects.requireNonNull(destinationPath, "Destination path cannot be null");

    if (!Files.exists(this.templatesLocation)) {
      LOG.info("Templates location {} does not exist.", this.templatesLocation);
      return false;
    }

    if (!Files.isDirectory(destinationPath)) {
      try {
        Files.createDirectories(destinationPath);
      } catch (IOException e) {
        throw new CobiGenRuntimeException("Unable to create directory " + destinationPath);
      }
    }

    if (!isEmpty(destinationPath) && !forceOverride) {
      throw new DirectoryNotEmptyException(destinationPath.toString());
    }

    return true;
  }

  /**
   * Deletes a directory and its sub directories recursively
   *
   * @param pathToBeDeleted the directory which should be deleted recursively
   * @throws IOException if the file could not be deleted
   */
  private void deleteDirectoryRecursively(Path pathToBeDeleted) throws IOException {

    Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  /**
   * Check if directory is empty
   *
   * @param directory to be checked
   * @return <code>true</code> if is empty, <code>false</code> otherwise
   * @throws IOException in case the directory could not be read
   */
  private boolean isEmpty(final Path directory) throws IOException {

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
      return !dirStream.iterator().hasNext();
    }
  }

  @Override
  public Path upgradeMonolithicTemplates(Path templatesProject) {

    Path templatesPath = null;
    Path CobigenTemplates;
    if (templatesProject != null) {
      CobigenTemplates = CobiGenPaths.getPomLocation(templatesProject);
      templatesPath = FileSystemUtil.createFileSystemDependentPath(CobigenTemplates.toUri());
    } else {
      templatesPath = FileSystemUtil.createFileSystemDependentPath(ConfigurationFinder.findTemplatesLocation());
      CobigenTemplates = CobiGenPaths.getPomLocation(templatesPath);
    }
    AbstractConfigurationUpgrader<ContextConfigurationVersion> contextUpgraderObject = new ContextConfigurationUpgrader();

    // Upgrade the context.xml to the new template-set with latest version
    contextUpgraderObject.upgradeConfigurationToLatestVersion(templatesPath, BackupPolicy.NO_BACKUP);

    LOG.info("context.xml upgraded successfully. {}", templatesPath);
    LOG.info("Templates successfully upgraded. \n ");

    // check the new Path to the template-set after the upgrade
    Path newTemplates;
    if (templatesProject == null) {
      // 1. check in .cobigen Home
      // No need to check renaming in Home folder since we only have two templates folders:
      // templates and templates-set
      newTemplates = Paths.get(ConfigurationFinder.findTemplatesLocation());
    } else {
      // 2. otherwise check in the given customTemplates

      // check renaming
      // 2.1 renaming from CobiGen_Templates to template-sets occurred
      if (Files.exists(CobigenTemplates.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)))
        newTemplates = CobigenTemplates.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      else {
        // 2.2 Renaming from templates to template-sets occurred
        newTemplates = CobigenTemplates.getParent().getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
            .resolve(ConfigurationConstants.ADAPTED_FOLDER);
      }
    }
    LOG.info("New templates location: {} ", newTemplates);
    return newTemplates;
  }
}
