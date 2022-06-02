package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test suite for class path loader issues.
 */
public class MultipleMatchingTriggerTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/MultipleMatchingTriggerTest/";

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
   * Tests correct generation in case of a destination paths matches multiple templates of different matching trigger.
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testMutlipleTemplates_differentTrigger_samePath() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/PlainInput.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/PlainInput.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main",
        "PlainInput.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1", "increment2");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("TestOutput.txt");
    try (InputStream in = generationResult.getContents()) {
      // order of merge is not of interest
      String fileContents = IOUtils.toString(in);
      assertThat(fileContents).isIn("Increment1TemplateIncrement2Template", "Increment2TemplateIncrement1Template");
    }
  }

}
