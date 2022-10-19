package com.devonfw.cobigen.eclipse.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;

import java.io.File;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test the upgrader in core and generates from the new template-set
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UpgradeMonolithicTemplatesTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/UpgradeMonolithicTemplatesTest/";

  /** Current home directory */
  protected Path currentHome;

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception test fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
    // import the configuration project for this test
    EclipseUtils.importExistingGeneralProject(bot,
        new File(resourcesRootPath + "templates/CobiGen_Templates").getAbsolutePath());
    EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

  }

  /**
   * Tests if the Upgrader will work properly and generate after that
   *
   * Setup home path for cobigen to isolate the test (withEnvironmentVariable)
   *
   * @throws Exception Test fails
   */
  @Test
  public void testUpgradeAndGenerateFromTemplateSet() throws Exception {

    this.currentHome = this.tmpFolderRule.newFolder("cobigen-test-home").toPath();
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, this.currentHome.toString()).execute(() -> {

      // create a new temporary java project and copy java class used as an input for CobiGen
      String testProjectName = "TestInputProj";
      IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
      FileUtils.copyFile(new File(resourcesRootPath + "input/test.yml"),
          project.getUnderlyingResource().getLocation().append("src/main/java/main/test.yml").toFile());
      project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
      this.tmpMavenProjectRule.updateProject();

      // expand the new file in the package explorer
      SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
      SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main", "test.yml");
      javaClassItem.select();

      // execute CobiGen
      EclipseCobiGenUtils.processCobiGenAndUpgrade(bot, javaClassItem, "All");

      EclipseCobiGenUtils.confirmSuccessfullGeneration(bot, 40000);

      // check assertions
      bot.waitUntil(new AllJobsAreFinished(), 10000);
    });
  }
}
