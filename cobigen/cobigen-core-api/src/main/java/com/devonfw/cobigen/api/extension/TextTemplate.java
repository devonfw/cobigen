package com.devonfw.cobigen.api.extension;

import java.nio.file.Path;

/**
 * A text template managed by any {@link TextTemplateEngine}
 */
public interface TextTemplate {

  /**
   * Returns the relative path of a template within the configuration folder
   *
   * @return the relative path to the template file
   */
  public String getRelativeTemplatePath();

  /**
   * Returns the absolute file path to the template file
   *
   * @return the absolute file path to the template file
   */
  public Path getAbsoluteTemplatePath();

}
