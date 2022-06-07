package com.devonfw.cobigen.impl.config;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Maps;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFinder.class);

  /** Cached templates configurations. Trigger ID -> Configuration File URI -> configuration instance */
  private Map<String, Map<Path, TemplatesConfiguration>> templatesConfigurations = Maps.newHashMap();

  /** Cached context configuration */
  private ContextConfiguration contextConfiguration;

  /** Root path of the configuration */
  private Path configurationPath;

  /** The OS filesystem path of the configuration */
  private URI configurationLocation;

  /**
   * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
   *
   * @param configurationLocation the OS Filesystem path of the configuration location.
   */
  public ConfigurationHolder(URI configurationLocation) {

    this.configurationPath = FileSystemUtil.createFileSystemDependentPath(configurationLocation);
    this.configurationLocation = configurationLocation;
    // updates the root template path and informs all of its observers
    PluginRegistry.notifyPlugins(this.configurationPath);
  }

  /**
   * @return <code>true</code> if the configuration is based in a JAR file
   */
  public boolean isJarConfig() {

    return FileSystemUtil.isZipFile(this.configurationLocation);
  }

  /**
   * @return the path of the configuration based on the OS filesystem. It could be a .jar file or a maven project root
   *         folder
   */
  public URI getConfigurationLocation() {

    return this.configurationLocation;
  }

  /**
   * @return the path within the configuration. Might be a different file system than OS in case of a .jar configuration
   */
  public Path getConfigurationPath() {

    return this.configurationPath;
  }

  /**
   * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
   *
   * @param trigger to get matcher declarations from
   * @return the {@link TemplatesConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplatesConfiguration readTemplatesConfiguration(Trigger trigger) {

    Path configRoot = readContextConfiguration().getConfigLocationforTrigger(trigger.getId(), true);
    Path templateFolder = Paths.get(trigger.getTemplateFolder());
    if (!this.templatesConfigurations.containsKey(trigger.getId())) {
      TemplatesConfiguration config = new TemplatesConfiguration(configRoot, trigger, this);
      this.templatesConfigurations.put(trigger.getId(), Maps.<Path, TemplatesConfiguration> newHashMap());

      this.templatesConfigurations.get(trigger.getId()).put(templateFolder, config);
    }

    return this.templatesConfigurations.get(trigger.getId()).get(templateFolder);
  }

  /**
   * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public ContextConfiguration readContextConfiguration() {

    if (this.contextConfiguration == null) {
      this.contextConfiguration = new ContextConfiguration(this.configurationPath);
    }
    return this.contextConfiguration;
  }

  /**
   * @return return if the template folder structure consists of template sets or if the old structure is used
   */
  public boolean isTemplateSetConfiguration() {

    if (this.configurationPath.toUri().getScheme().equals("jar")
        || !this.configurationPath.getFileName().toString().equals(ConfigurationConstants.TEMPLATE_SETS_FOLDER)) {
      return false;
    }
    return true;
  }

  /**
   * Search for the location of the Java utils
   *
   * @return the {@link Path} of the location of the util classes or null if no location was found
   * @throws IOException
   */
  public List<Path> getUtilsLocation() {

    List<Path> utilsLocationPaths = new ArrayList<>();
    if (isTemplateSetConfiguration()) {
      List<Trigger> triggers = readContextConfiguration().getTriggers();

      for (Trigger trigger : triggers) {
        Path configLocation = readContextConfiguration().getConfigLocationforTrigger(trigger.getId(), false);
        utilsLocationPaths.add(configLocation);
      }
    } else {
      utilsLocationPaths.add(Paths.get(this.configurationLocation));
    }

    return utilsLocationPaths;
  }
}
