package com.devonfw.cobigen.eclipse.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class HealthCheckTest extends SystemTest {

  /** Root path of the Test Resources */
  private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

  /**
   * Clean workspace before test suite
   *
   * @throws Exception if anything fails
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    EclipseUtils.cleanWorkspace(bot, true);
  }

  /**
   * Testing HealthCheck
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore
  public void testHealthCheckIfTemplateProjecNotCopiedIntoWS() throws Exception {

    File tmpFolder = this.tmpFolderRule.newFolder();
    FileUtils.copyDirectory(new File(resourcesRootPath + "templates"), tmpFolder);
    EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);
    EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

    EclipseCobiGenUtils.runAndCaptureHealthCheck(bot);
  }
}
