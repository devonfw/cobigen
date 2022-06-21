package com.devonfw.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
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

/** Test suite testing generation with velocity template engine */
public class VelocityGenerationTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/VelocityGenerationTest/";

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
   * Tests a simple generation using velocity template engine.
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void simpleVelocityBasedGeneration() throws Exception {

    // create a new temporary java project and copy java class used as an input for CobiGen
    String testProjectName = "TestInputProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjectName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/Input.java"),
        project.getUnderlyingResource().getLocation().append("src/main/java/main/Input.java").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

    // expand the new file in the package explorer
    SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
    SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjectName, "src/main/java", "main", "Input.java");
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "Velocity Test");
    EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

    // check assertions
    bot.waitUntil(new AllJobsAreFinished(), 10000);
    IFile generationResult = project.getProject().getFile("velocityTest.txt");
    assertThat(Paths.get(generationResult.getLocationURI())).exists().hasContent("String,int,");
  }
}
