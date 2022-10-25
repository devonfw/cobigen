package com.devonfw.cobigen.impl.config;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Maps;

/**
 * The {@link ConfigurationFactory} creates one of the configuration readers fitting to the configuration xml
 */
public class ConfigurationFactory {

  private Path configRoot;

  /**
   * The constructor.
   *
   * @param configRoot root of the cobigen
   */
  public ConfigurationFactory(URI configRoot) {

    this.configRoot = FileSystemUtil.createFileSystemDependentPath(configRoot);

  }

  /**
   * @param templatesConfigurations Cached templates configurations. Trigger ID -> Configuration File URI ->
   *        configuration instance
   * @param templateFolder path to the templates folder
   * @param trigger to get matcher declarations from
   * @param holder holds the templatesConfigurations in the given list
   * @return the {@link TemplatesConfiguration} instance saved in the given map
   */
  public TemplatesConfiguration retrieveTemplatesConfiguration(
      Map<String, Map<Path, TemplatesConfiguration>> templatesConfigurations, Path templateFolder, Trigger trigger,
      ConfigurationHolder holder) {

    if (!templatesConfigurations.containsKey(trigger.getId())) {
      TemplatesConfiguration config = new TemplatesConfiguration(this.configRoot, trigger, holder);
      templatesConfigurations.put(trigger.getId(), Maps.<Path, TemplatesConfiguration> newHashMap());

      templatesConfigurations.get(trigger.getId()).put(templateFolder, config);
    }

    return templatesConfigurations.get(trigger.getId()).get(templateFolder);
  }

  /**
   * @return {@link ContextConfigurationDecorator} instance
   */
  public ContextConfiguration getContextConfiguration() {

    return new ContextConfiguration(this.configRoot);
  }

  /**
   * @param groupIds property
   * @param allowSnapshots property
   * @param hideTemplates property
   * @return {@link TemplateSetConfiguration} instance
   */
  public TemplateSetConfiguration retrieveTemplateSetConfiguration(ConfigurationProperties properties) {

    TemplateSetConfiguration templateSetConfiguration = new TemplateSetConfiguration(properties, this.configRoot);
    return templateSetConfiguration;
  }

}
