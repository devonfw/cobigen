package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.nio.charset.Charset;
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
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Maps;

/** The {@link ContextConfigurationSetReader} reads the context xml */
public class ContextConfigurationSetReader extends AbstractContextConfigurationReader {

  /** Map with the paths of the config roots for a context.xml file */
  private Map<Path, Path> configRoots = new HashMap<>();

  /** Map with the paths of the config roots for a trigger */
  private Map<String, Path> triggerConfigRoots = new HashMap<>();

  /**
   * The constructor.
   *
   * @param configRoot the config root directory
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public ContextConfigurationSetReader(Path configRoot) throws InvalidConfigurationException {

    super(configRoot);

    this.contextFiles = new ArrayList<>();

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find a folder in which to search for the context configuration file.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        this.contextFiles.addAll(loadContextFilesAdapted(templateSetsAdapted));
      }

      if (Files.exists(templateSetsDownloaded)) {
        this.contextFiles.addAll(loadContextFilesDownloaded(templateSetsDownloaded));
      }

      if (this.contextFiles.isEmpty()) {
        throw new InvalidConfigurationException(configRoot, "Could not find any context configuration file.");
      }
    }

    this.contextRoot = configRoot;

    readConfiguration();
  }

  /**
   * search for configuration files in the subfolder for adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   */
  private List<Path> loadContextFilesAdapted(Path configRoot) {

    List<Path> contextPaths = new ArrayList<>();

    List<Path> templateDirectories = new ArrayList<>();

    try (Stream<Path> files = Files.list(configRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configRoot, "Could not read configuration root directory.", e);
    }

    for (Path templateDirectory : templateDirectories) {
      Path contextFilePath = templateDirectory.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

      addConfigRoot(contextFilePath, templateDirectory, contextPaths);
    }

    return contextPaths;
  }

  /**
   * search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   */
  private List<Path> loadContextFilesDownloaded(Path configRoot) {

    List<Path> contextPaths = new ArrayList<>();

    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path contextFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

        addConfigRoot(contextFilePath, configurationPath, contextPaths);
      }
    }

    return contextPaths;
  }

  @Override
  public Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = Maps.newHashMap();
    for (Path contextFile : this.contextConfigurations.keySet()) {
      Path configRoot = this.configRoots.get(contextFile);
      ContextConfiguration contextConfiguration = this.contextConfigurations.get(contextFile);
      boolean isJarConfig = (configRoot.getParent() == null);

      List<com.devonfw.cobigen.impl.config.entity.io.Trigger> triggerList = contextConfiguration.getTrigger();
      if (!triggerList.isEmpty()) {
        // context configuration in template sets consists of only one trigger
        com.devonfw.cobigen.impl.config.entity.io.Trigger trigger = triggerList.get(0);

        String templateFolder;
        if (isJarConfig) {
          templateFolder = contextFile.getParent().toString();
        } else {
          templateFolder = configRoot.getFileName().resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER).toString();
          configRoot = configRoot.getParent();
        }

        if (!this.triggerConfigRoots.containsKey(trigger.getId()) || !isJarConfig) {
          // prefer the adapted templates
          this.triggerConfigRoots.put(trigger.getId(), configRoot);
          triggers.put(trigger.getId(), new Trigger(trigger.getId(), trigger.getType(), templateFolder,
              Charset.forName(trigger.getInputCharset()), loadMatchers(trigger), loadContainerMatchers(trigger)));
        }
      }
    }
    return triggers;
  }

  /**
   * Get the configuration root directory for a given trigger
   *
   * @param triggerId the trigger id to search the config root for
   * @return the {@link Path} of the config root for a trigger
   */
  public Path getConfigRootForTrigger(String triggerId) {

    return this.triggerConfigRoots.get(triggerId);
  }

  /**
   * Adds the path to a context.xml file to the list of all config files. Also adds the path of the context.xml file and
   * its root directory to the configRoots map
   *
   * @param contextFilePath the {@link Path} to the context.xml file
   * @param configRootPath the {@link Path} containing the config root directory for a context.xml
   * @param contextPaths a list containing all paths to context.xml files
   */
  private void addConfigRoot(Path contextFilePath, Path configRootPath, List<Path> contextPaths) {

    if (Files.exists(contextFilePath)) {
      contextPaths.add(contextFilePath);
      this.configRoots.put(contextFilePath, configRootPath);
    }
  }
}
