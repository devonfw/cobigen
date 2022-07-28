package com.devonfw.cobigen.eclipse.test;

import java.io.File;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test the upgrader in core and generates from the new template-set
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UpgradeMonolithicTemplatesTest extends SystemTest {

  /** Root path of the Test Resources */ // TODO
  private static final String resourcesRootPath = "somewhere";

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
   * Tests if the Upgrader will work properly and generate after that
   *
   *
   * @throws Exception Test fails
   */
  @Test
  @Ignore
  public void testUpgradeAndGenerateFromTextDoesNotLoop() throws Exception {
    // TODO

  }
}
