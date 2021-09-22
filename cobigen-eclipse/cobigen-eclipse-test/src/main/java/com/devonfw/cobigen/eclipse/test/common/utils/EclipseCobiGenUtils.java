package com.devonfw.cobigen.eclipse.test.common.utils;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AnyShellIsActive;

/**
 * Eclipse Utils to work with the CobiGen UI.
 */
public class EclipseCobiGenUtils {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(EclipseCobiGenUtils.class);

  /** Default timeout for waiting on generation results or build results */
  public static final int DEFAULT_TIMEOUT = 15000;

  /**
   * Selects the the increment with the given name and generates it.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGen(SWTWorkbenchBot bot, SWTBotTreeItem input, String... increments) throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE,
        CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE_BATCH), DEFAULT_TIMEOUT);

    // select increment and generate
    for (String increment : increments) {
      SWTBotTreeItem treeItem = bot.tree().getTreeItem(increment);
      bot.waitUntil(widgetIsEnabled(treeItem));
      treeItem.check();
    }
    SWTBotButton finishButton = bot.button(IDialogConstants.FINISH_LABEL);
    bot.waitUntil(widgetIsEnabled(bot.button()));
    finishButton.click();
  }

  /**
   * Checks the CobiGen HealthCheck and takes screenshots of it.
   *
   * @param bot to process the health check
   * @throws Exception test fails
   */
  public static void runAndCaptureHealthCheck(SWTWorkbenchBot bot) throws Exception {

    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time

    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    view.bot().tree().expandNode("CobiGen_Templates").select().contextMenu("CobiGen").menu("Health Check...").click();
    bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.HealthCheckDialogs.DIALOG_TITLE), DEFAULT_TIMEOUT);

    takeScreenshot(bot, "healthCheck");
    SWTBotShell healthCheckDialog = bot.shell(CobiGenDialogConstants.HealthCheckDialogs.DIALOG_TITLE);
    healthCheckDialog.bot().button(CobiGenDialogConstants.HealthCheckDialogs.ADVANCED_DIALOG_TITLE).click();
    bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.HealthCheckDialogs.ADVANCED_DIALOG_TITLE),
        DEFAULT_TIMEOUT);
    takeScreenshot(bot, "advancedHealthCheck");
    SWTBotShell advancedHealthCheckDialog = bot.shell(CobiGenDialogConstants.HealthCheckDialogs.ADVANCED_DIALOG_TITLE);
    advancedHealthCheckDialog.bot().button("OK");
    advancedHealthCheckDialog.bot().button("OK");
  }

  /**
   * Takes a screenshot (*.jpeg) of the current screen encoding test method and class and appends the given identifier
   * to the file name
   *
   * @param bot current {@link SWTBot}
   * @param identifier to appended on the generated screenshots file name
   */
  public static void takeScreenshot(SWTBot bot, String identifier) {

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    StackTraceElement stackTraceElement = null;
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      if (stackTrace[i].getMethodName().startsWith("test") && stackTrace[i].getClassName().endsWith("Test")) {
        stackTraceElement = stackTrace[i];
        break;
      }
    }

    if (stackTraceElement == null) {
      LOG.warn("No test method in stacktrace found for creating health check screenshots.");
      return;
    }

    bot.captureScreenshot("target/screenshots/" + stackTraceElement.getMethodName() + "("
        + stackTraceElement.getClassName() + ")-" + identifier + ".jpeg");
  }

  /**
   * Confirm successful generation dialog as well as organize import dialog if necessary.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   */
  public static void confirmSuccessfullGeneration(SWTWorkbenchBot bot) {

    confirmSuccessfullGeneration(bot, DEFAULT_TIMEOUT);
  }

  /**
   * Confirm successful generation dialog as well as organize import dialog if necessary.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param timeout the bot should wait while discovering dialogs
   */
  public static void confirmSuccessfullGeneration(SWTWorkbenchBot bot, int timeout) {

    try {
      bot.waitUntil(shellIsActive("Organize Imports"), timeout);
      bot.shell("Organize Imports").bot().button(IDialogConstants.OK_LABEL).click();
    } catch (TimeoutException e) {
      // dialog just optional
    }
    bot.waitUntil(shellIsActive(CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED), timeout);
    bot.shell(CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED).bot().button(IDialogConstants.OK_LABEL).click();
  }
}
