package com.devonfw.cobigen.eclipse.test.common.utils;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
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
   * Selects the increment with the given name and generates it.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGen(SWTWorkbenchBot bot, SWTBotTreeItem input, String... increments) throws Exception {

    processCobiGen(bot, input, DEFAULT_TIMEOUT, increments);
  }

  /**
   * Selects the increment with the given name and generates it, even if monolithic templates found
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGenAndPostponeUpgrade(SWTWorkbenchBot bot, SWTBotTreeItem input, String... increments)
      throws Exception {

    processCobiGenAndPostponeUpgrade(bot, input, DEFAULT_TIMEOUT, increments);
  }

  /**
   * Selects the increment with the given name, upgrades the monolithic templates and generates,
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGenAndUpgrade(SWTWorkbenchBot bot, SWTBotTreeItem input, String... increments)
      throws Exception {

    processCobiGenAndUpgrade(bot, input, DEFAULT_TIMEOUT, increments);
  }

  /**
   * Tries a Generate process with an expected error title.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param expectedErrorTitle String of expected error title
   * @throws Exception test fails
   */
  public static void processCobiGenWithExpectedError(SWTWorkbenchBot bot, SWTBotTreeItem input,
      String expectedErrorTitle) throws Exception {

    processCobiGenWithExpectedError(bot, input, DEFAULT_TIMEOUT, expectedErrorTitle);
  }

  /**
   * Tries a Generate process from a selected text as input
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGenWithTextInput(SWTWorkbenchBot bot, SWTBotEclipseEditor input, String... increments)
      throws Exception {

    processCobiGenWithTextInput(bot, input, DEFAULT_TIMEOUT, increments);
  }

  /**
   * Expands multi layer nodes of following format: node1>node2>finalNode
   *
   * @param bot SWTWorkbenchBot to use
   * @param increment String of nodes to expand
   * @return SWTBotTreeItem node which was selected as last element
   */
  private static SWTBotTreeItem expandNodes(SWTWorkbenchBot bot, String increment) {

    SWTBotTreeItem treeItem = null;
    // split nodes
    String[] items = increment.split(">");
    // expand multi layer node
    for (int i = 0; i < items.length - 1; i++) {
      treeItem = bot.tree().expandNode(items[i]);
    }
    if (treeItem != null) {
      // select last node to generate from
      treeItem = treeItem.getNode(items[items.length - 1]);
      bot.waitUntil(widgetIsEnabled(treeItem));
      treeItem.check();
    }
    return treeItem;
  }

  /**
   * Selects the increment with the given name and generates it.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param defaultTimeout timeout to be overwritten
   * @param increments increments to be generated. (Supports expanding of multi layer nodes in artifact selection)
   * @throws Exception test fails
   */
  public static void processCobiGen(SWTWorkbenchBot bot, SWTBotTreeItem input, int defaultTimeout, String... increments)
      throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), defaultTimeout); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    generateWithSelectedIncrements(bot, defaultTimeout, increments);
  }

  /**
   * Tries a Generate process from a selected text as input
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input {@link SWTBotEclipseEditor} input of CobiGen to be selected
   * @param defaultTimeout timeout to be overwritten
   * @param increments increments to be generated.
   * @throws Exception test fails
   */
  public static void processCobiGenWithTextInput(SWTWorkbenchBot bot, SWTBotEclipseEditor input, int defaultTimeout,
      String... increments) throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), defaultTimeout); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    postponeUpgradeAndContinue(bot);
    generateWithSelectedIncrements(bot, defaultTimeout, increments);
  }

  /**
   * Selects provided increments and clicks on finish button afterwards
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param defaultTimeout timeout to be overwritten
   * @param increments increments to be generated.
   */
  private static void generateWithSelectedIncrements(SWTWorkbenchBot bot, int defaultTimeout, String... increments) {

    bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE,
        CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE_BATCH), defaultTimeout);

    // select increment and generate
    for (String increment : increments) {
      // check for multi layer nodes
      if (increment.contains(">")) {
        SWTBotTreeItem treeItem = expandNodes(bot, increment);
        bot.waitUntil(widgetIsEnabled(treeItem));
        treeItem.check();
      } else {
        // select single increment
        SWTBotTreeItem treeItem = bot.tree().getTreeItem(increment);
        bot.waitUntil(widgetIsEnabled(treeItem));
        treeItem.check();
      }
    }
    SWTBotButton finishButton = bot.button(IDialogConstants.FINISH_LABEL);
    bot.waitUntil(widgetIsEnabled(bot.button()));
    finishButton.click();
  }

  /**
   * Selects the increment with the given name and generates it, even if monolithic templates found
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws CoreException
   * @throws Exception test fails
   */
  private static void processCobiGenAndPostponeUpgrade(SWTWorkbenchBot bot, SWTBotTreeItem input, int defaultTimeout,
      String[] increments) throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), defaultTimeout); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    postponeUpgradeAndContinue(bot);
    generateWithSelectedIncrements(bot, defaultTimeout, increments);
  }

  /**
   * Selects the increment with the given name and generates it, upgrade if monolithic templates found
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param increments increments to be generated.
   * @throws CoreException
   * @throws Exception test fails
   */
  private static void processCobiGenAndUpgrade(SWTWorkbenchBot bot, SWTBotTreeItem input, int defaultTimeout,
      String[] increments) throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), defaultTimeout); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    UpgradeAndContinue(bot);
    generateWithSelectedIncrements(bot, defaultTimeout, increments);
  }

  /**
   * Tries a Generate process with an expected error title.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param input input of CobiGen to be selected
   * @param defaultTimeout timeout to be overwritten
   * @param expectedErrorTitle String of expected error title
   * @throws Exception test fails
   */
  public static void processCobiGenWithExpectedError(SWTWorkbenchBot bot, SWTBotTreeItem input, int defaultTimeout,
      String expectedErrorTitle) throws Exception {

    // Open generation wizard with new file as Input
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), defaultTimeout); // build might take some time
    input.contextMenu("CobiGen").menu("Generate...").click();
    postponeUpgradeAndContinue(bot);
    bot.waitUntil(new AnyShellIsActive(expectedErrorTitle), defaultTimeout);

    takeScreenshot(bot, "InvalidConfigurationDialog");

    SWTBotButton finishButton = bot.button(IDialogConstants.OK_LABEL);
    bot.waitUntil(widgetIsEnabled(bot.button()));
    finishButton.click();
  }

  /**
   * Skips the Upgrade Warning message with the "Postpone" button
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   *
   */
  private static void postponeUpgradeAndContinue(SWTWorkbenchBot bot) {

    takeScreenshot(bot, "Warning!");
    SWTBotShell finishDialog = bot.shell("Warning!");
    finishDialog.bot().button("Postpone").click();
  }

  /**
   * Starts the upgrade process with the "Upgrade" button
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   *
   */
  private static void UpgradeAndContinue(SWTWorkbenchBot bot) {

    takeScreenshot(bot, "Warning!");
    SWTBotShell finishDialog = bot.shell("Warning!");
    finishDialog.bot().button("Upgrade").click();
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
    captureAdvancedHealthCheck(bot);
  }

  /**
   * Checks the CobiGen HealthCheck and takes screenshots of it, even if monolithic templates found.
   *
   * @param bot to process the health check
   * @throws Exception test fails
   */
  public static void runAndCaptureHealthCheckWithMonolithicConfiguration(SWTWorkbenchBot bot) throws Exception {

    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time

    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    view.bot().tree().expandNode("CobiGen_Templates").select().contextMenu("CobiGen").menu("Health Check...").click();
    postponeUpgradeAndContinue(bot);
    captureAdvancedHealthCheck(bot);
  }

  /**
   * Checks the CobiGen HealthCheck and takes screenshots of it.
   *
   * @param bot to process the health check
   */
  private static void captureAdvancedHealthCheck(SWTWorkbenchBot bot) {

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
   * Checks the CobiGen Update Templates and takes screenshots of it.
   *
   * @param bot to process the Update Templates command
   * @throws Exception test fails
   */
  public static void runAndCaptureUpdateTemplates(SWTWorkbenchBot bot) throws Exception {

    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time

    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    view.bot().tree().contextMenu("CobiGen").menu("Update Templates...").click();
    bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.UpdateTemplateDialogs.DIALOG_TITLE), DEFAULT_TIMEOUT);

    takeScreenshot(bot, "Update Templates");
    SWTBotShell updateTemplatesDialog = bot.shell(CobiGenDialogConstants.UpdateTemplateDialogs.DIALOG_TITLE);
    updateTemplatesDialog.bot().button("Download").click();
    SWTBotShell informationDialog = bot.shell("Information");
    bot.waitUntil(new AnyShellIsActive("Information"), DEFAULT_TIMEOUT);
    takeScreenshot(bot, "Update Templates Information");
    informationDialog.bot().button("Ok").click();
  }

  /**
   * Checks the CobiGen Adapt Templates and takes screenshots of it.
   *
   * @param bot to process the Adapt Templates command
   * @throws Exception test fails
   */
  public static void runAndCaptureAdaptTemplates(SWTWorkbenchBot bot) throws Exception {

    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time

    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    view.bot().tree().contextMenu("CobiGen").menu("Adapt Templates...").click();
    bot.waitUntil(new AnyShellIsActive("Warning!"), DEFAULT_TIMEOUT);

    takeScreenshot(bot, "Adapt Templates Warning!");
    SWTBotShell warningDialog = bot.shell("Warning!");
    warningDialog.bot().button("Ok").click();

    takeScreenshot(bot, "Create new POM!");
    SWTBotShell finishDialog = bot.shell("Create new POM");
    finishDialog.bot().button("Finish").click();

    SWTBotShell informationDialog = bot.shell("Information");
    bot.waitUntil(new AnyShellIsActive("Information"), DEFAULT_TIMEOUT);
    takeScreenshot(bot, "Adapt Templates Information");
    informationDialog.bot().button("Ok").click();
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
