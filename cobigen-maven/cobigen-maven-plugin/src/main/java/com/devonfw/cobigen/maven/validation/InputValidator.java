package com.devonfw.cobigen.maven.validation;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;

/**
 * Input validator, which validates the increment and template declarations within the maven plugin configuration
 *
 * @author mbrunnli (09.02.2015)
 */
public class InputValidator {

  /**
   * Validates, whether all declared templates could be found in the attached configuration list
   *
   * @param templates available templates in the configuration
   * @param templateIdsToBeGenerated requested templates
   * @throws MojoExecutionException if one of the requested templates could not be found in configuration
   * @author mbrunnli (11.02.2015)
   */
  public static void validateTemplateInputs(List<TemplateTo> templates, List<String> templateIdsToBeGenerated)
      throws MojoExecutionException {

    List<String> templateIds = new LinkedList<>(templateIdsToBeGenerated);
    for (TemplateTo template : templates) {
      if (templateIds.contains(template.getId())) {
        templateIds.remove(template.getId());
      }
    }
    if (!templateIds.isEmpty()) {
      throw new MojoExecutionException("No template(s) with the given id(s) '" + templateIds + "' found.");
    }
  }

  /**
   * Validates, whether all declared increments could be found in the attached configuration list
   *
   * @param increments available increments in the configuration
   * @param templateIdsToBeGenerated requested increments
   * @throws MojoExecutionException if one of the requested increments could not be found in the configuration
   * @author mbrunnli (11.02.2015)
   */
  public static void validateIncrementInputs(List<IncrementTo> increments, List<String> templateIdsToBeGenerated)
      throws MojoExecutionException {

    List<String> incrementIds = new LinkedList<>(templateIdsToBeGenerated);
    for (IncrementTo increment : increments) {
      if (incrementIds.contains(increment.getId())) {
        incrementIds.remove(increment.getId());
      }
    }
    if (!incrementIds.isEmpty()) {
      throw new MojoExecutionException("No increment(s) with the given id(s) '" + incrementIds + "' found.");
    }
  }
}
