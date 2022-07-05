package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
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
 * Test suite for issues with generation from text selection
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class GenerateFromTextInputTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/GenerateFromTextInputTest/";

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
   * Tests if a generate from text selection works properly
   *
   * Reported in: https://github.com/devonfw/cobigen/issues/1526
   *
   * @throws Exception Test fails
   */
  @Ignore
  @Test
  public void testGenerateFromTextDoesNotLoop() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/PlainInput.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/PlainInput.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "PlainInput.java");

    // double click on the file to open the Java editor
    javaClassItem.select().doubleClick();

    SWTBotEclipseEditor textEditor = bot.editorByTitle("PlainInput.java").toTextEditor();

    // select 1st line of code as input
    textEditor.selectLine(0);

    EclipseCobiGenUtils.processCobiGenWithTextInput(bot, textEditor, "increment1");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("TestOutput.txt");
    assertThat(generationResult.exists()).isTrue();

  }

}
