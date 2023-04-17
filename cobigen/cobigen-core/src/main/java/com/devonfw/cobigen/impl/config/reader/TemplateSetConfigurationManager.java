package com.devonfw.cobigen.impl.config.reader;

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
 * This class takes care of finding either adapted or downloaded template-set files.
 *
 * TODO: Move into TemplateSetConfigurationReader, see: https://github.com/devonfw/cobigen/issues/1668
 */
public class TemplateSetConfigurationManager {

  /** List with the paths of the configuration locations for the template-set.xml files */
  private Map<Path, Path> configLocations;

  /**
   * The constructor.
   */
  public TemplateSetConfigurationManager() {

    this.configLocations = new HashMap<>();

  }

  /**
   * @return configLocations
   */
  public Map<Path, Path> getConfigLocations() {

    return this.configLocations;
  }

  /**
   * Search for configuration files in the sub folders of adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   * @return List of Paths to the adapted templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesAdapted(Path configRoot) {

    List<Path> templateSetDirectories = retrieveTemplateSetDirectories(configRoot);

    List<Path> adaptedTemplateSets = new ArrayList<>();
    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      // makes sure that only valid template set folders get added
      if (Files.exists(templateSetFilePath)) {
        adaptedTemplateSets.add(templateSetFilePath);

        this.configLocations.put(templateSetFilePath, templateDirectory);
      }
    }

    return adaptedTemplateSets;
  }

  /**
   * Retrieves a list of template set directories
   *
   * @param configRoot List of template set directories
   */
  private List<Path> retrieveTemplateSetDirectories(Path configRoot) {

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

    return templateSetDirectories;
  }

  /**
   * Search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   * @return List of Paths to the downloaded templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesDownloaded(Path configRoot) {

    // TODO: add check for valid templatesetjar util
    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    List<Path> downloadedTemplateSets = new ArrayList<>();
    if (!templateJars.isEmpty()) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        // makes sure that only valid template set jars get added
        if (Files.exists(templateSetFilePath)) {
          downloadedTemplateSets.add(templateSetFilePath);
          this.configLocations.put(templateSetFilePath, jarPath);
        }

      }
    }

    return downloadedTemplateSets;
  }

}
