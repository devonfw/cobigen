package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CobigenJavaWrapperTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/CobiGenJavaWrapperTest/";

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception test fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
    // import the configuration project for this test
    EclipseUtils.importExistingGeneralProject(bot, new File(resourcesRootPath + "templates").getAbsolutePath());
    EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);
  }

  /**
   * Test for external projects (not in workspace) taken as input for generation
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void testWorkspaceExternalProjectAsInput() throws Exception {

    // copy sample project to external location and import it into the workspace
    File tmpFolder = this.tmpFolderRule.newFolder();
    String testProjName = "ExtTestProj";
    FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
    EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "src", "main", "SimpleInput.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment2");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
    IFile generationResult = proj.getFile("TestOutput2.txt");
    try (InputStream in = generationResult.getContents()) {
      assertThat(IOUtils.toString(in)).isEqualTo("This is a test");
    }
  }

  /**
   * Test the cobigen-eclipse internal path invariants. As there are so many different relative paths in place, this
   * test will just do a generation smoke test with a nested file.
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void testPathInvariants() throws Exception {

    // copy sample project to external location and import it into the workspace
    File tmpFolder = this.tmpFolderRule.newFolder();
    String testProjName = "ExtTestProj";
    FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
    EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "src", "main", "SimpleInput.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment3");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
    IFile generationResult = proj.getFile("subfolder/TestOutput3.txt");
    try (InputStream in = generationResult.getContents()) {
      assertThat(IOUtils.toString(in)).isEqualTo("Content does not matter!");
    }
  }

  /**
   * Tests successful batch generation.
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void testPackageAsInputForGeneration() throws Exception {

    // copy sample project to external location and import it into the workspace
    File tmpFolder = this.tmpFolderRule.newFolder();
    String testProjName = "ExtTestProj";
    FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
    EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "src", "main");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
    IFile generationResult = proj.getFile("TestOutput.txt");
    try (InputStream in = generationResult.getContents()) {
      // we do not make any guarantees on the ordering
      assertThat(IOUtils.toString(in)).isIn("SimpleInputSimpleInput2", "SimpleInput2SimpleInput");
    }
  }
}
