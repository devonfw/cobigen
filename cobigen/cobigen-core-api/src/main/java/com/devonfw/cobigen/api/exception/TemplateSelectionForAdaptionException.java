package com.devonfw.cobigen.api.exception;

import java.nio.file.Path;
import java.util.List;

/**
 * Exception that indicates that a new template structure is available. For asking which template sets should be
 * adapted.
 */
public class TemplateSelectionForAdaptionException extends Exception {
  /** Generated serial version UID */
  private static final long serialVersionUID = 1;

  /** List of available template sets. */
  private List<Path> templateSets;

  /**
   * Creates a new {@link TemplateSelectionForAdaptionException}
   *
   * @param templateSets A list with available template sets to adapt.
   *
   */
  public TemplateSelectionForAdaptionException(List<Path> templateSets) {

    super("Select the template sets you want to adapt.");
    this.templateSets = templateSets;
  }

  /**
   * @return templateSets All available template sets.
   */
  public List<Path> getTemplateSets() {

    return this.templateSets;
  }
}