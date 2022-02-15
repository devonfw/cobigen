package com.devonfw.cobigen.eclipse.test.common;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Abstract test implementation providing the commonly used setup and tear down methods as well as JUnit rules and
 * resets the SWTBot accordingly.
 */
public abstract class SystemTest {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(SystemTest.class);

  /** Rule for creating temporary {@link IJavaProject}s per test. */
  @Rule
  public TmpMavenProjectRule tmpMavenProjectRule = new TmpMavenProjectRule();

  /** Rule for creating temporary files and folders */
  @Rule
  public TemporaryFolder tmpFolderRule = new TemporaryFolder();

  /**
   * Logging test name
   */
  @Rule
  public TestRule watcher = new TestWatcher() {
    @Override
    protected void starting(Description description) {

      LOG.info(">>>>>>>> Starting test '{}'", description.getMethodName());
    }

    @Override
    protected void finished(Description description) {

      LOG.info(">>>>>>>> Finishing test '{}'", description.getMethodName());
    }
  };

  /** {@link SWTBot} for UI controls */
  protected static SWTWorkbenchBot bot = new SWTWorkbenchBot();

  static {
    SWTBotPreferences.TIMEOUT = EclipseCobiGenUtils.DEFAULT_TIMEOUT;
  }

  /**
   * Setup workbench appropriately for tests
   *
   * @throws Exception setup failed
   */
  @Before
  public void setupTest() throws Exception {

    bot.resetWorkbench();
    bot.waitUntil(new AllJobsAreFinished());
    EclipseUtils.cleanWorkspace(bot, false);

    // this flag is set to be true and will suppress ErrorDialogs,
    // which is completely strange, so we enable them again.
    ErrorDialog.AUTOMATED_MODE = false;
    SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
    perspective.activate();

    try {
      bot.viewByTitle("Welcome").close();
    } catch (WidgetNotFoundException e) {
      // ignore as Welcome screen will just occur once
    } catch (Exception e) {
      LOG.debug("Exception occured during test setup", e);
      throw e;
    }
  }
}
