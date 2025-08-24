package com.devonfw.cobigen.eclipse.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Class for testing Manage Template Sets with a click on the Home Button
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenGUITest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception test fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
  }

  /**
   * Testing to open the Template Set Management GUI and clicking Home Button
   *
   * @throws Exception test fails
   */
  @Test
  public void testOpenTemplateSetManagementGUI() throws Exception {

    // copy sample project to external location and import it into the workspace
    String testProjName = "ExtTestProj";
    IJavaProject project = this.tmpMavenProjectRule.createProject(testProjName);
    FileUtils.copyFile(new File(resourcesRootPath + "input/adapt-templates.yml"),
        project.getUnderlyingResource().getLocation().append("adapt-templates.yml").toFile());
    project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    this.tmpMavenProjectRule.updateProject();

    // open GUI
    EclipseCobiGenUtils.runAndCaptureManageTemplateSets(bot);
  }

}
