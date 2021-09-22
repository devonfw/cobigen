package com.devonfw.cobigen.eclipse.common.constants.external;

/**
 * Constants of CobiGen dialogs.
 */
public class CobiGenDialogConstants {

  /** Message dialog title for a succeeded generation */
  public static final String DIALOG_TITLE_GEN_SUCCEEDED = "Success";

  /** Message dialog title for a succeeded generation with warnings */
  public static final String DIALOG_TITLE_GEN_SUCCEEDED_W_WARNINGS = "Success with warnings";

  /** Message dialog title for an error dialog */
  public static final String DIALOG_TITLE_ERROR = "Error";

  /** Dialog constants of the health check */
  public class HealthCheckDialogs {

    /** Commonly used dialog title for the Basic Health Check */
    public static final String DIALOG_TITLE = "Health Check";

    /** Commonly used dialog title for the Advanced Health Check */
    public static final String ADVANCED_DIALOG_TITLE = "Advanced Health Check";
  }

  /** Dialog constants of the Generate Wizard */
  public class GenerateWizard {

    /** Dialog title of the generate wizard for one single input */
    public static final String DIALOG_TITLE = "CobiGen";

    /** Dialog title of the generate wizard for batch processing */
    public static final String DIALOG_TITLE_BATCH = "CobiGen (batch mode)";
  }

  /** Dialog constants of the Update Templates */
  public class UpdateTemplateDialogs {

    /** Dialog title of the Update Templates */
    public static final String DIALOG_TITLE = "Update CobiGen Templates";

  }
}
