package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.ConfigurationConflictException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MonolithicConfigReader extends ConfigurationReader {

  private final Map<String, TemplatesConfiguration> templatesConfigurations = new HashMap<>();

  private final Path contextFile;

  public MonolithicConfigReader(Path configRoot) {
    super(configRoot);

    // use old context.xml in templates root (CobiGen_Templates)
    Path contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

    if (!Files.exists(contextFile)) {
      // if no context.xml is found in the root folder search in src/main/templates
      configRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
      if (!Files.exists(contextFile)) {
        throw new InvalidConfigurationException(contextFile, "Could not find any context configuration file.");
      } else {
        checkForConflict(configRoot, contextFile);
      }
    } else {
      Path subConfigRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      if (Files.isDirectory(subConfigRoot)) {
        checkForConflict(subConfigRoot, contextFile);
      }
    }
    this.contextFile = contextFile;
  }

  @Override
  public ContextConfiguration readContextConfiguration() {

    return new ContextConfigurationReader(contextFile).read();
  }

  /**
   * Checks if a conflict with the old and modular configuration exists
   * <p>
   * TODO: Check if this is still needed, see: https://github.com/devonfw/cobigen/issues/1662
   *
   * @param configRoot  Path to root directory of the configuration
   * @param contextFile Path to context file of the configuration
   */
  private void checkForConflict(Path configRoot, Path contextFile) {

    if (!loadContextFilesInSubfolder(configRoot).isEmpty()) {
      throw new ConfigurationConflictException(contextFile, "You are using an old configuration of the templates in addition to new ones. Please make sure this is not the case as both at the same time are not supported. For more details visit this wiki page: "
        + WikiConstants.WIKI_UPDATE_OLD_CONFIG);
    }
  }

  /**
   * Searches for configuration Files in the sub folders of configRoot
   * <p>
   * TODO: Check if this is still needed, see: https://github.com/devonfw/cobigen/issues/1662
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  private List<Path> loadContextFilesInSubfolder(Path configRoot) {

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

    for (Path file : templateDirectories) {
      Path contextPath = file.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
      if (Files.exists(contextPath)) {
        contextPaths.add(contextPath);
      }
    }

    return contextPaths;
  }

  /**
   * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
   *
   * @param triggerOrTemplateSet to get matcher declarations from
   * @return the {@link TemplatesConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplatesConfiguration readTemplatesConfiguration(String triggerOrTemplateSet) {

    if (this.templatesConfigurations.containsKey(triggerOrTemplateSet)) {
      return templatesConfigurations.get(triggerOrTemplateSet);
    }

    TemplatesConfigurationReader reader = new TemplatesConfigurationReader(findTemplateRootPath(this.configRoot, ConfigurationConstants.TEMPLATES_CONFIG_FILENAME, triggerOrTemplateSet));
    TemplatesConfiguration templatesConfiguration = reader.read(readContextConfiguration().getTrigger(triggerOrTemplateSet));
    this.templatesConfigurations.put(triggerOrTemplateSet, templatesConfiguration);
    return templatesConfiguration;
  }
}
