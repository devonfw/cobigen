package com.devonfw.cobigen.eclipse.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test suite for exception handling issues.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExceptionHandlingTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/ExceptionHandlingTest/";

  /** Rule for creating temporary {@link IJavaProject}s per test. */
  @Rule
  public TmpMavenProjectRule tmpMavenProjectRule2 = new TmpMavenProjectRule();

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
   * Tests for a conflict of old and new templates configuration and proper error message dialog
   *
   * @throws Exception Test fails
   */
  @Test
  public void testConflictWithTemplateTypesDisplaysErrorDialog() throws Exception {

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
    javaClassItem.select();

    // execute CobiGen
    EclipseCobiGenUtils.processCobiGenWithExpectedError(bot, javaClassItem, "Invalid context configuration!");
  }

}
