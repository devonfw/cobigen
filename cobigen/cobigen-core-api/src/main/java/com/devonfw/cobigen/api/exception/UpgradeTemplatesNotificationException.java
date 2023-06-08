package com.devonfw.cobigen.api.exception;

/**
 * Exception that indicates that an old monolithic template structure has been adapted. For asking if the template
 * structure should be upgraded.
 */
public class UpgradeTemplatesNotificationException extends Exception {

  /** Generated serial version UID */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new {@link UpgradeTemplatesNotificationException} with a proper notification message
   *
   */
  public UpgradeTemplatesNotificationException() {

    super(
        "You are using an old, monolithic template project. Do you want to upgrade your template project to the new template structure with independent template sets?");
  }

}
