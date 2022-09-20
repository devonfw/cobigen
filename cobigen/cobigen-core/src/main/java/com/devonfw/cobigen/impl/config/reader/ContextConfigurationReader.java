package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.ConfigurationConflictException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.google.common.collect.Maps;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader extends AbstractContextConfigurationReader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ContextConfigurationReader.class);

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  public ContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    super(configRoot);

    this.contextFiles = new ArrayList<>();

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
        this.contextFiles.add(contextFile);
      }
    } else {
      Path subConfigRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      if (Files.isDirectory(subConfigRoot)) {
        checkForConflict(subConfigRoot, contextFile);
      }
      this.contextFiles.add(contextFile);
    }

    this.contextRoot = configRoot;

    readConfiguration();
  }

  /**
   * Checks if a conflict with the old and modular configuration exists
   *
   * @param configRoot Path to root directory of the configuration
   * @param contextFile Path to context file of the configuration
   */
  private void checkForConflict(Path configRoot, Path contextFile) {

    if (!loadContextFilesInSubfolder(configRoot).isEmpty()) {
      String message = "You are using an old configuration of the templates in addition to new ones. Please make sure this is not the case as both at the same time are not supported. For more details visit this wiki page: "
          + WikiConstants.WIKI_UPDATE_OLD_CONFIG;
      ConfigurationConflictException exception = new ConfigurationConflictException(contextFile, message);
      LOG.error("A conflict with the old and modular configuration exists", exception);
      throw exception;
    }

  }

  /**
   * search for configuration Files in the subfolders of configRoot
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
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  @Override
  public Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = Maps.newHashMap();
    for (Path contextFile : this.contextConfigurations.keySet()) {
      ContextConfiguration contextConfiguration = this.contextConfigurations.get(contextFile);
      for (com.devonfw.cobigen.impl.config.entity.io.Trigger t : contextConfiguration.getTrigger()) {
        // templateFolder property is optional in schema version 2.2. If not set take the path of the context.xml file
        String templateFolder = t.getTemplateFolder();
        if (templateFolder.isEmpty() || templateFolder.equals("/")) {
          templateFolder = contextFile.getParent().getFileName().toString();
        }
        triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), templateFolder,
            Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
      }
    }
    return triggers;
  }
}
