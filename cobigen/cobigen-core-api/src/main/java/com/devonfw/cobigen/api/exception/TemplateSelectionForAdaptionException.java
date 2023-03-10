package com.devonfw.cobigen.api.exception;

import java.util.List;

import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateStatePair;

/**
 * Exception that indicates that a new template structure is available. For asking which template sets should be
 * adapted.
 */
public class TemplateSelectionForAdaptionException extends Exception {

  /** Generated serial version UID */
  private static final long serialVersionUID = 1;

  /** List of available template sets. */
  private List<MavenCoordinateStatePair> templateSetMavenCoordinateStatePairs;

  /**
   * Creates a new {@link TemplateSelectionForAdaptionException}
   *
   * @param templateSetMavenCoordinateStatePairs A list with available template sets to adapt. Template sets are wrapped
   *        into a pair of MavenCoordinateStates.
   *
   */
  public TemplateSelectionForAdaptionException(List<MavenCoordinateStatePair> templateSetMavenCoordinateStatePairs) {

    super("Select the template sets you want to adapt.");
    this.templateSetMavenCoordinateStatePairs = templateSetMavenCoordinateStatePairs;
  }

  /**
   * @return templateSets All available template sets.
   */
  public List<MavenCoordinateStatePair> getTemplateSetMavenCoordinateStatePairs() {

    return this.templateSetMavenCoordinateStatePairs;
  }
}
