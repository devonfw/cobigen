package com.devonfw.cobigen.impl.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.TemplatesConfigurationReader;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;

/**
 * The {@link TemplatesConfiguration} is a configuration data wrapper for all information of a context about templates
 * and the target destination for the generated data.
 */
public class TemplatesConfiguration {

  /** Folder name of the context definition (root folder for all templates) */
  private String templatesFolderName;

  /** All available templates */
  private Map<String, Template> templates;

  /** All available increments */
  private Map<String, Increment> increments;

  /** {@link Trigger}, all templates of this configuration depend on */
  private Trigger trigger;

  /** {@link TextTemplateEngine} to be used for the template set covered by this configuration. */
  private String templateEngine;

  /**
   * {@link TemplatesConfigurationReader} to be used for reading external increments
   */
  private TemplatesConfigurationReader externalReader;

  /**
   * Creates a new {@link TemplatesConfiguration} for the given template folder with the given settings reference. We
   * use the configurationHolder to store there all the external TemplatesConfiguration
   *
   * @param configRoot configuration root path
   * @param trigger {@link Trigger} of this {@link TemplatesConfiguration}
   * @param configurationHolder The {@link ConfigurationHolder} used for reading templates folder
   * @throws UnknownContextVariableException if the destination path contains an undefined context variable
   * @throws UnknownExpressionException if there is an unknown variable modifier
   * @throws InvalidConfigurationException if the given templates.xml is not valid
   */
  public TemplatesConfiguration(Path configRoot, Trigger trigger, ConfigurationHolder configurationHolder)
      throws InvalidConfigurationException {

    if (configurationHolder.isTemplateSetConfiguration()) {
      TemplateSetConfiguration templateSetConfiguration = new TemplateSetConfiguration(configRoot, configurationHolder);
      this.templatesFolderName = trigger.getTemplateFolder();

      this.templates = templateSetConfiguration.getTemplateSetConfigurationReader().getTemplatesConfigurationReader()
          .loadTemplates(trigger);
      this.increments = templateSetConfiguration.getTemplateSetConfigurationReader().getTemplatesConfigurationReader()
          .loadIncrements(this.templates, trigger);
      this.templateEngine = templateSetConfiguration.getTemplateSetConfigurationReader()
          .getTemplatesConfigurationReader().getTemplateEngine();
      this.trigger = trigger;
    } else {
      TemplatesConfigurationReader reader = new TemplatesConfigurationReader(configRoot, trigger.getTemplateFolder(),
          configurationHolder);

      this.templatesFolderName = trigger.getTemplateFolder();

      this.templates = reader.loadTemplates(trigger);
      this.increments = reader.loadIncrements(this.templates, trigger);
      this.templateEngine = reader.getTemplateEngine();
      this.trigger = trigger;
    }

  }

  /**
   * Creates a new {@link TemplatesConfiguration} for the given template folder with the given settings reference
   *
   * @param configRoot configuration root path
   * @param trigger {@link Trigger} of this {@link TemplatesConfiguration}
   * @param configurationHolder The {@link ConfigurationHolder} used for reading templates folder
   * @param incrementToSearch String name of the increment we should retrieve and store
   * @throws UnknownContextVariableException if the destination path contains an undefined context variable
   * @throws UnknownExpressionException if there is an unknown variable modifier
   * @throws InvalidConfigurationException if the given templates.xml is not valid
   */
  public TemplatesConfiguration(Path configRoot, Trigger trigger, ConfigurationHolder configurationHolder,
      String incrementToSearch) throws InvalidConfigurationException {

    this.externalReader = new TemplatesConfigurationReader(configRoot, trigger.getTemplateFolder(),
        configurationHolder);
    this.templatesFolderName = trigger.getTemplateFolder();
    this.templates = this.externalReader.loadTemplates(trigger);
    Map<String, Increment> externalIncrements = this.externalReader.loadSpecificIncrement(this.templates, trigger,
        incrementToSearch);
    this.increments = new HashMap<>();
    this.increments.putAll(externalIncrements);
    this.templateEngine = this.externalReader.getTemplateEngine();
    this.trigger = trigger;
  }

  /**
   * Returns the {@link Template} with the given id
   *
   * @param id of the {@link Template} to be searched for
   * @return the {@link Template} with the given id or <code>null</code> if there is no
   */
  public Template getTemplate(String id) {

    return this.templates.get(id);
  }

  /**
   * Returns the set of all available templates
   *
   * @return the set of all available templates
   */
  public Set<Template> getAllTemplates() {

    return new HashSet<>(this.templates.values());
  }

  /**
   * Returns the {@link Trigger}, this {@link TemplatesConfiguration} is related to
   *
   * @return the {@link Trigger}, this {@link TemplatesConfiguration} is related to
   */
  public Trigger getTrigger() {

    return this.trigger;
  }

  /**
   * Returns the set of all available increments
   *
   * @return the set of all available increments
   */
  public List<Increment> getAllGenerationPackages() {

    return new LinkedList<>(this.increments.values());
  }

  /**
   * Returns the folder name of this context definition (root folder for all templates)
   *
   * @return the folder name of this context definition (root folder for all templates)
   */
  public String getTemplatesFolderName() {

    return this.templatesFolderName;
  }

  /**
   * Returns the configured template engine
   *
   * @return the template engine name to be used
   */
  public String getTemplateEngine() {

    return this.templateEngine;
  }

  /**
   * Returns a map containing all the increments
   *
   * @return Map containing increments
   */
  public Map<String, Increment> getIncrements() {

    return this.increments;
  }

  /**
   * Loads an specific increment and stores it inside our increments map
   *
   * @param incrementToSearch Name of the increment to search
   */
  public void loadSpecificIncrement(String incrementToSearch) {

    Map<String, Increment> externalIncrements = this.externalReader.loadSpecificIncrement(this.templates, this.trigger,
        incrementToSearch);
    this.increments.putAll(externalIncrements);
  }

}
