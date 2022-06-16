package com.devonfw.cobigen.eclipse.test.common.utils;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withTitle;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForWidget;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.swtbot.SwtBotProjectActions;

/**
 * Eclipse specific operations to make test setup easier.
 */
public class EclipseUtils {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(EclipseUtils.class);

  /**
   * Imports the an existing general (non-java/non-maven) project to the workspace.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param projectPath absolute path of the project on file system
   * @throws CoreException if anything went wrong during build
   */
  public static void importExistingGeneralProject(SWTWorkbenchBot bot, String projectPath) throws CoreException {

    importExistingGeneralProject(bot, projectPath, true);
  }

  /**
   * Imports the an existing general (non-java/non-maven) project to the workspace.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param projectPath absolute path of the project on file system
   * @param copyIntoWorkspace state if the project sources should be copied into workspace while importing
   * @throws CoreException if anything went wrong during build
   */
  public static void importExistingGeneralProject(SWTWorkbenchBot bot, String projectPath, boolean copyIntoWorkspace)
      throws CoreException {

    bot.menu("File").menu("Import...").click();

    SWTBotShell popup = bot.activeShell();
    bot.tree().expandNode("General").select("Existing Projects into Workspace");
    bot.waitUntil(waitForWidget(withMnemonic(IDialogConstants.NEXT_LABEL)));
    bot.button(IDialogConstants.NEXT_LABEL).click();
    String selectRootDirLabel = "Select root directory:";
    bot.waitUntil(waitForWidget(withMnemonic(selectRootDirLabel)));
    bot.radio(selectRootDirLabel).click();
    SWTBotCombo comboBox = bot.comboBox();
    comboBox.setText(projectPath);

    String cbCopyProjectsLabel = "Copy projects into workspace";
    bot.waitUntil(waitForWidget(withMnemonic(cbCopyProjectsLabel)));
    SWTBotCheckBox cbCopyProjects = bot.checkBox(cbCopyProjectsLabel);
    SWTBotButton selectAll = bot.button("Select All");
    selectAll.setFocus();
    bot.waitUntil(widgetIsEnabled(selectAll));
    selectAll.click(); // just to trigger project scanning, correct logic will be set below

    if (copyIntoWorkspace && !cbCopyProjects.isChecked() || !copyIntoWorkspace && cbCopyProjects.isChecked()) {
      cbCopyProjects.click();
    }

    SWTBotButton finishButton = bot.button("Finish");
    bot.waitUntil(widgetIsEnabled(finishButton));
    finishButton.click();
    bot.waitUntil(shellCloses(popup));
    ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    bot.waitUntil(new AllJobsAreFinished());
  }

  /**
   * Updates a maven project.
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param projectName name of the project
   * @throws Exception if anything happens during build
   */
  public static void updateMavenProject(SWTWorkbenchBot bot, String projectName) throws Exception {

    Awaitility.await().atMost(EclipseCobiGenUtils.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> bot.viewById(JavaUI.ID_PACKAGES));
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem configurationProject = view.bot().tree().expandNode(projectName);
    configurationProject.contextMenu().menu("Maven", false, 0).menu("Update Project...", false, 0).click();
    bot.waitUntil(shellIsActive("Update Maven Project"));
    bot.checkBox("Force Update of Snapshots/Releases").click();
    bot.button(IDialogConstants.OK_LABEL).click();
    Retry.runWithRetry(bot, () -> ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
        .build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor()), CoreException.class, 2);
  }

  /**
   * Create a new file in the given project relative path with the given contents.
   *
   * @param project to add the file to
   * @param relativePath the project relative path
   * @param contents the contents to be written to the file
   * @return the {@link IFile file}
   * @throws CoreException if anything fails
   */
  public static IFile createFile(IJavaProject project, String relativePath, String contents) throws CoreException {

    IContainer lastContainer = project.getProject();
    String[] pathArr = relativePath.split("/");
    // create folders
    for (int i = 0; i < pathArr.length - 1; i++) {
      IFolder folder = lastContainer.getFolder(new Path(pathArr[i]));
      if (!folder.exists()) {
        folder.create(true, true, new NullProgressMonitor());
      }
      lastContainer = folder;
    }

    IFile file = lastContainer.getFile(new Path(pathArr[pathArr.length - 1]));
    if (!file.exists()) {
      file.create(new ByteArrayInputStream(contents.getBytes()), true, new NullProgressMonitor());
    }
    return file;
  }

  /**
   * Cleanup workspace by deleting all projects
   *
   * @param bot the {@link SWTWorkbenchBot} of the test
   * @param cleanCobiGenConfiguration if <code>true</code>, the cobigen configuration project will be removed as well
   * @throws Exception test fails
   */
  public static void cleanWorkspace(SWTWorkbenchBot bot, boolean cleanCobiGenConfiguration) throws Exception {

    LOG.debug("Clean workspace {}", cleanCobiGenConfiguration ? "incl. CobiGen_Templates" : "");

    int maxRetries = 10;
    String projectName = "";

    for (int i = 1; i <= maxRetries; i++) {
      IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      try {
        LOG.debug("Found projects to be cleaned: {}", Arrays.toString(allProjects));
        for (IProject project : allProjects) {
          projectName = project.getName();
          if (cleanCobiGenConfiguration || !ResourceConstants.CONFIG_PROJECT_NAME.equals(projectName)) {

            project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            project.close(new NullProgressMonitor());

            LOG.debug("Deleting project: {}", projectName);
            SwtBotProjectActions.deleteProject(bot, projectName, true);
          }
        }
        break;
      } catch (Exception e) {
        LOG.debug("An error occured while trying to delete project: {}", projectName, e);
        Thread.sleep(500);
        if (i == maxRetries) {
          LOG.debug("Not able to cleanup the workspace after: {} retries", maxRetries);
          throw e;
        }
      }
    }
    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

  }

  /**
   * Defensively try to expand errors in problem view for screenshot analysis. Failing here, will not fail the test.
   *
   * @param bot the current workbench bot
   */
  public static void openErrorsTreeInProblemsView(SWTWorkbenchBot bot) {

    try {
      SWTBotView viewByTitle = bot.view(withTitle(containsString("Problems")));
      SWTBotTree tree = viewByTitle.bot().tree().select(0);
      Arrays.asList(tree.getAllItems()).stream().forEach(e -> e.expand());

      LOG.debug("Log Problems view entries:");
      LOG.debug(">>>>>");
      for (SWTBotTreeItem category : tree.getAllItems()) {
        LOG.debug("> {}", category.getText());
        for (SWTBotTreeItem issue : category.getItems()) {
          LOG.debug(">> {}", issue.getText());
        }
      }
      LOG.debug("<<<<<");
    } catch (WidgetNotFoundException e) {
      LOG.warn("Could not find Widget during expansion of problem view. This does not harm the test... continue.", e);
    }
  }
}
