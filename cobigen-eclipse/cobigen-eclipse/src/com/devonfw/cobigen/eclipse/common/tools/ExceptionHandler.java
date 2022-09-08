package com.devonfw.cobigen.eclipse.common.tools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.ConfigurationConflictException;
import com.devonfw.cobigen.api.exception.DeprecatedMonolithicConfigurationException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.devonfw.cobigen.eclipse.healthcheck.HealthCheckDialog;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;
import com.devonfw.cobigen.impl.util.PostponeUtil;

/**
 * Util class to handle exceptions
 */
public class ExceptionHandler {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * @param e the exception to be handled
   * @param activeShell the shell a potential message dialog should be bound to or null if a default should be created
   */
  public static void handle(Throwable e, Shell activeShell) {

    if (CoreException.class.isAssignableFrom(e.getClass())) {
      LOG.error("Eclipse internal Exception", e);
      PlatformUIUtil.openErrorDialog("An eclipse internal exception occurred during processing:\n" + e.getMessage()
          + "\n If this problem persists please report it to the CobiGen developers.", e);
    } else if (InvalidConfigurationException.class.isAssignableFrom(e.getClass())) {
      LOG.warn("Invalid configuration.", e);
      openInvalidConfigurationErrorDialog((InvalidConfigurationException) e);
    } else if (ConfigurationConflictException.class.isAssignableFrom(e.getClass())) {
      openInvalidConfigurationErrorDialog((ConfigurationConflictException) e);
    } else if (DeprecatedMonolithicConfigurationException.class.isAssignableFrom(e.getClass())) {
      LOG.warn("Monolithic Templates found.", e);
      openMonolithicConfigurationErrorDialog((DeprecatedMonolithicConfigurationException) e);
    } else if (GeneratorProjectNotExistentException.class.isAssignableFrom(e.getClass())) {
      LOG.error(
          "The project '{}' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.",
          ResourceConstants.CONFIG_PROJECT_NAME, e);
      MessageDialog.openError(getShell(activeShell), "Generator configuration project not found!", "The project '"
          + ResourceConstants.CONFIG_PROJECT_NAME
          + "' containing the configuration and templates is currently not existent. Please create one or check it out from SVN as stated in the user documentation.");
    } else if (GeneratorCreationException.class.isAssignableFrom(e.getClass())) {
      LOG.error("Could not create an instance of the generator.", e);
      PlatformUIUtil.openErrorDialog("Could not initialize CobiGen for the given selection: " + e.getMessage(), e);
    } else if (InvalidInputException.class.isAssignableFrom(e.getClass())) {
      LOG.info("Invalid input selected for generation: {}", e.getMessage());
      MessageDialog.openInformation(getShell(activeShell), "Invalid selection", e.getMessage());
    } else if (CobiGenRuntimeException.class.isAssignableFrom(e.getClass())) {
      LOG.error("CobiGen Exception:\n{}", e.getMessage(), e);
      PlatformUIUtil.openErrorDialog(e.getMessage(), e);
    } else {
      LOG.error("An unexpected exception occurred!", e);
      PlatformUIUtil.openErrorDialog("An unexpected exception occurred!", e);
    }

  }

  /**
   * @param activeShell active shell or null if not passed
   * @return the shell to be used
   */
  private static Shell getShell(Shell activeShell) {

    return activeShell != null ? activeShell : Display.getDefault().getActiveShell();
  }

  /**
   * Opens up a message dialog for displaying further guidance on context configuration issues.
   *
   * @param e {@link InvalidConfigurationException} occurred
   */
  private static void openInvalidConfigurationErrorDialog(InvalidConfigurationException e) {

    PlatformUIUtil.getWorkbench().getDisplay().syncExec(() -> {
      MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Invalid context configuration!",
          null,
          "Any context/templates configuration has been changed into an invalid state "
              + "OR is simply outdated, if you recently updated CobiGen. "
              + "For further investigation and automatic upgrade options start CobiGen's Health Check."
              + "\n\nOriginal error message: " + e.getMessage(),
          MessageDialog.ERROR, new String[] { "Health Check", "OK" }, 1);
      dialog.setBlockOnOpen(true);

      int result = dialog.open();
      if (result == 0) {
        new HealthCheckDialog().execute();
      }
    });
  }

  /**
   * Opens up a message dialog for displaying further guidance on upgrading old templates.
   *
   * @param e {@link DeprecatedMonolithicConfigurationException} occurred
   */
  private static void openMonolithicConfigurationErrorDialog(DeprecatedMonolithicConfigurationException e) {

    PlatformUIUtil.getWorkbench().getDisplay().syncExec(() -> {
      MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Warning!", null,
          e.getMessage() + " Further Information can be found at:"
              + WikiConstants.WIKI_UPGRADE_MONOLITHIC_CONFIGURATION,
          MessageDialog.WARNING, new String[] { "Upgrade", "Postpone", "Postpone for 30 days" }, 2);

      MessageDialog successUpgrade = new MessageDialog(Display.getDefault().getActiveShell(), "Success!", null,
          "Templates were successfully upgraded. ", MessageDialog.INFORMATION, new String[] { "Ok" }, 1);

      dialog.setBlockOnOpen(true);
      successUpgrade.setBlockOnOpen(true);
      int result = dialog.open();
      if (result == 0) {
        try {
          ResourcesPluginUtil
              .startTemplatesUpgrader(DeprecatedMonolithicConfigurationException.getMonolithicConfiguration());
          successUpgrade.open();
        } catch (Throwable a) {
          String upgradeErrorMessage = "An error has occurred while upgrading the templates!";
          LOG.error(upgradeErrorMessage, a);
          PlatformUIUtil.openErrorDialog(upgradeErrorMessage + " " + a.getMessage(), a);
        }
      }
      if (result == 1) {
        // Do nothing (Postpone and Continue)
      }
      if (result == 2) {
        PostponeUtil.addATimestampForOneMonth();

      }
    });
  }

}
