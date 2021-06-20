package com.devonfw.cobigen.eclipse.test.common;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Abstract test implementation providing the commonly used setup and tear down methods as well as JUnit rules
 * and resets the SWTBot accordingly.
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

    /** {@link SWTBot} for UI controls */
    protected static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    /**
     * Setup workbench appropriately for tests
     * @throws Exception
     *             test fails
     */
    @BeforeClass
    public static void setupTest() throws Exception {
        try {
            bot.viewByTitle("Welcome").close();

            bot.resetWorkbench();
            bot.waitUntil(new AllJobsAreFinished(), EclipseCobiGenUtils.DEFAULT_TIMEOUT);
            EclipseUtils.cleanWorkspace(true);

            SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
            perspective.activate();

            // this flag is set to be true and will suppress ErrorDialogs,
            // which is completely strange, so we enable them again.
            ErrorDialog.AUTOMATED_MODE = false;
        } catch (WidgetNotFoundException e) {
            // ignore
        } catch (Exception e) {
            LOG.debug("Exception occured during test setup", e);
        }
    }

    /**
     * Reset workbench and open java perspective
     * @throws Exception
     *             test fails
     */
    @Before
    public void beforeTest() throws Exception {
        try {
            bot.resetWorkbench();
            EclipseUtils.cleanWorkspace(false);

            SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
            perspective.activate();
        } catch (Exception e) {
            LOG.debug("Exception occured during test setup", e);
        }
    }
}
