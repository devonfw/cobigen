package com.devonfw.cobigen.impl.config;

import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import javassist.ClassPath;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * The {@link TemplatesConfiguration} is a configuration data wrapper for all information of a context about templates
 * and the target destination for the generated data.
 */
public class TemplatesConfiguration {

  private final Path configRoot;

  /**
   * All available templates
   */
  private final Map<String, Template> templates;

  /**
   * All available increments
   */
  private final Map<String, Increment> increments;

  /**
   * {@link Trigger}, all templates of this configuration depend on
   */
  private final Trigger trigger;

  /**
   * {@link TextTemplateEngine} to be used for the template set covered by this configuration.
   */
  private final String templateEngine;

  private Path[] utilClasses;

  private URL[] classPathUrls;

  public TemplatesConfiguration(Trigger trigger, Map<String, Increment> increments, Map<String, Template> templates, String templateEngine, Path configRoot) {
    this.trigger = trigger;
    this.increments = increments;
    this.templates = templates;
    this.templateEngine = templateEngine;
    this.configRoot = configRoot.resolve(trigger.getTemplateFolder());
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
   * Returns the list of all available increments
   *
   * @return all available increments
   */
  public List<Increment> getAllGenerationPackages() {

    return new LinkedList<>(this.increments.values());
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

  public Path getConfigRoot() {
    return this.configRoot;
  }

}
