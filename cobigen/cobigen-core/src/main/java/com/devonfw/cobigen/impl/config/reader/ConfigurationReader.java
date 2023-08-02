package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurationReader {

  private static final Path[] templatesConfigLocation = new Path[]{Paths.get(""),
    Paths.get(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER), Paths.get(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)};
  protected final Path configRoot;
  /**
   * Location where the properties are saved
   */
  protected ConfigurationProperties configurationProperties;

  public ConfigurationReader(Path configRoot) {
    if (configRoot == null) {
      throw new IllegalArgumentException("Configuration path cannot be null.");
    }

    this.configRoot = configRoot;
  }

  /**
   * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public abstract ContextConfiguration readContextConfiguration();

  /**
   * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
   *
   * @param triggerOrTemplateSet to get matcher declarations from
   * @return the {@link TemplatesConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public abstract TemplatesConfiguration readTemplatesConfiguration(String triggerOrTemplateSet);

  public Path findTemplateRootPath(Path projectRoot, String configurationFileName, String templateFolder) {
    Path configFilePath = null;
    Path templateLocation = null;
    for (Path p : templatesConfigLocation) {
      configFilePath = projectRoot.resolve(templateFolder).resolve(p).resolve(configurationFileName);
      templateLocation = projectRoot.resolve(templateFolder).resolve(p);
      if (Files.exists(configFilePath)) {
        break;
      }
    }

    if (!Files.exists(configFilePath)) {
      throw new InvalidConfigurationException(configFilePath, "Could not find " + configurationFileName + " at any of the following locations "
        + printPathsOfAllPossibleConfigLocations(projectRoot, configurationFileName, templateFolder));
    }
    return templateLocation;
  }

  private String printPathsOfAllPossibleConfigLocations(Path projectRoot, String configurationFileName, String templateFolder) {
    StringBuilder sb = new StringBuilder("{ ");
    List<String> paths = new ArrayList<>(templatesConfigLocation.length);
    for (Path p : templatesConfigLocation) {
      paths.add(projectRoot.resolve(templateFolder).resolve(p).resolve(configurationFileName).toString());
    }
    sb.append(String.join(", ", paths));
    sb.append(" }");
    return sb.toString();
  }
}
