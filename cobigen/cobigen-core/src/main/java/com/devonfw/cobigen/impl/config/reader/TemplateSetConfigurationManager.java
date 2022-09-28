package com.devonfw.cobigen.impl.config.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * Managing configurations, everything comes together here (I get more specific, once the ticket is done)
 *
 */

public class TemplateSetConfigurationManager {

  /** List with the paths of the configuration locations for the template-set.xml files */
  private Map<Path, Path> configLocations = new HashMap<>();

  List<Path> templateSetPaths = new ArrayList<>();

  /**
   * The constructor.
   */
  public TemplateSetConfigurationManager() {

  }

  /**
   * Adds the path of a template-set.xml file to the list of all config files. Also adds the path of the
   * template-set.xml file and its root directory to the configRoots map
   *
   * @param templateSetFilePath the {@link Path} to the template-set.xml file
   * @param configRootPath the {@link Path} containing the config root directory for a template-set.xml
   * @param templateSetPaths a list containing all paths to template-set.xml files
   * @throws FileNotFoundException
   */
  public void addConfigRoot(Path templateSetFilePath, Path configRootPath, List<Path> templateSetPaths) {

    if (Files.exists(templateSetFilePath)) {
      templateSetPaths.add(templateSetFilePath);
      this.configLocations.put(templateSetFilePath, configRootPath);
    }
  }

  // TODO: Save adapted and downloaded template sets in different lists, so we can treat them individually
  // (Because of the path problems with jars)

  /**
   * Search for configuration files in the sub folder for adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   * @return List of Paths to the adapted templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesAdapted(Path configRoot) {

    // We need to empty this list to prevent duplicates from being added
    this.templateSetPaths.clear();
    List<Path> templateSetDirectories = new ArrayList<>();

    try (Stream<Path> files = Files.list(configRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateSetDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configRoot, "Could not read configuration root directory.", e);
    }

    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      addConfigRoot(templateSetFilePath, templateDirectory, this.templateSetPaths);
    }

    return this.templateSetPaths;
  }

  /**
   * search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   * @return List of Paths to the downloaded templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesDownloaded(Path configRoot) {

    // We need to empty this list to prevent duplicates from being added
    this.templateSetPaths.clear();
    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        addConfigRoot(templateSetFilePath, jarPath, this.templateSetPaths);
      }
    }

    return this.templateSetPaths;
  }

}
