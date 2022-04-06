package com.devonfw.cobigen.impl.config.reader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.google.common.collect.Maps;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader extends AbstractContextConfigurationReader {

  /**
   * The constructor.
   *
   * @param configRoot the config root directory
   * @throws InvalidConfigurationException if the configuration is not valid
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
        this.contextFiles.add(contextFile);
      }
    } else {
      this.contextFiles.add(contextFile);
    }

    this.contextRoot = configRoot;

    readConfiguration();
  }

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
