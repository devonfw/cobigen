package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * Managing configurations, everything comes together here (I get more specific, once the ticket is done)
 *
 */
public class TemplateSetConfigurationManager {

  // List with the paths of the configuration locations for the template-set.xml files */
  private List<Path> configLocations = new ArrayList<>();

  /**
   * Map with XML Nodes 'template-set' of the template-set.xml files
   */
  protected Map<Path, TemplateSetConfiguration> templateSetConfigurations;

  // TODO: Implement
  public TemplateSetConfigurationManager(List<String> groupIds, boolean allowSnapshots, List<String> hideTemplates) {

  }

  // TODO: This method needs to be implemented
  protected TemplateSetConfiguration getConfiguration() {

    return null;

  }

  /**
   * Adds the path of a template-set.xml file to the list of all config files. Also adds the path of the
   * template-set.xml file and its root directory to the configRoots map
   *
   * @param templateSetFilePath the {@link Path} to the template-set.xml file
   * @param configRootPath the {@link Path} containing the config root directory for a template-set.xml
   * @param templateSetPaths a list containing all paths to template-set.xml files
   */
  private void addConfigRoot(Path templateSetFilePath, Path configRootPath, List<Path> templateSetPaths) {

    if (Files.exists(templateSetFilePath)) {
      templateSetPaths.add(templateSetFilePath);
      this.configLocations.add(templateSetFilePath);
    }
  }

  // TODO: Save adapted and downloaded template sets in different lists, so we can treat them individually
  // (Because of the path problems with jars)

  /**
   * Search for configuration files in the sub folder for adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   */
  private List<Path> loadTemplateSetFilesAdapted(Path configRoot) {

    List<Path> templateSetPaths = new ArrayList<>();

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

      addConfigRoot(templateSetFilePath, templateDirectory, templateSetPaths);
    }

    return templateSetPaths;
  }

  /**
   * search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   */
  private List<Path> loadTemplateSetFilesDownloaded(Path configRoot) {

    List<Path> templateSetPaths = new ArrayList<>();

    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());

        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .getParent().resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        addConfigRoot(templateSetFilePath, jarPath, templateSetPaths);
      }
    }

    return templateSetPaths;
  }

}
