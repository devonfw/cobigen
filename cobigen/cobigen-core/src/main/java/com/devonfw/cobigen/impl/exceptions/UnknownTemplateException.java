package com.devonfw.cobigen.impl.exceptions;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/**
 * The {@link UnknownTemplateException} occurs if a template is requested which is not registered in the template
 * configuration
 */
public class UnknownTemplateException extends InvalidConfigurationException {

  /**
   * Generated serial version UID
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new {@link UnknownTemplateException} with the given message
   *
   * @param templateId Template ID
   */
  public UnknownTemplateException(String templateId) {

    super("Unknown template with id=" + templateId + ". Template could not be found in the configuration.");
  }
}
