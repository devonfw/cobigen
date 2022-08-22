package com.devonfw.cobigen.impl.config.reader.interfaces;

import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.TemplatesConfigurationReader;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;

/**
 * TODO khucklen This type ...
 *
 */
public interface TemplatesInterface {

  /**
   * Returns the configured template engine to be used
   *
   * @return the configured template engine to be used
   */
  String getTemplateEngine();

  /**
   * Loads all templates of the static configuration into the local representation
   *
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @return the mapping of template names to the corresponding {@link Template}
   * @throws UnknownContextVariableException if the destination path contains an undefined context variable
   * @throws UnknownExpressionException if there is an unknown variable modifier
   * @throws InvalidConfigurationException if there are multiple templates with the same name
   */
  Map<String, Template> loadTemplates(Trigger trigger)
      throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException;

  /**
   * Loads all increments of the static configuration into the local representation.
   *
   * @return the mapping of increment names to the corresponding {@link Increment}
   * @param templates {@link Map} of all templates (see {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @throws InvalidConfigurationException if there is an invalid ref attribute
   */
  Map<String, Increment> loadIncrements(Map<String, Template> templates, Trigger trigger)
      throws InvalidConfigurationException;

  /**
   * Loads an specific increment of the static configuration into the local representation. The return object must be a
   * map because maybe this increment references other increments
   *
   * @return the mapping of increment names to the corresponding {@link Increment}
   * @param templates {@link Map} of all templates (see {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @param incrementName the increment to search
   * @throws InvalidConfigurationException if there is an invalid ref attribute
   */
  Map<String, Increment> loadSpecificIncrement(Map<String, Template> templates, Trigger trigger, String incrementName)
      throws InvalidConfigurationException;

  /**
   * Tries to find an increment on a list of increments and return it
   *
   * @param increment list of increments
   * @param ref name of the increment to get
   * @return Increment if it was found, null if no increment with that name was found
   */
  com.devonfw.cobigen.impl.config.entity.io.Increment getSpecificIncrement(
      List<com.devonfw.cobigen.impl.config.entity.io.Increment> increment, String ref);

}