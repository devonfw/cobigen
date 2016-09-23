package com.capgemini.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;
import com.capgemini.cobigen.eclipse.test.common.EclipseCobiGenUtils;
import com.capgemini.cobigen.eclipse.test.common.EclipseUtils;
import com.capgemini.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.capgemini.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CobigenJavaWrapperTest {

    /**
     * Rule for creating temporary {@link IJavaProject}s per test.
     */
    @Rule
    public TmpMavenProjectRule tmpMavenProjectRule = new TmpMavenProjectRule();

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/CobiGenJavaWrapperTest/";

    /** {@link SWTBot} for UI controls */
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();

    /**
     * Activate Java Perspective before each Test
     */
    @Before
    public void setup() {
        SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
        perspective.activate();
        bot.viewByTitle("Welcome").close();
        // this flag is set to be true and will suppress ErrorDialogs,
        // which is completely strange, so we enable them again.
        ErrorDialog.AUTOMATED_MODE = false;
    }

    /**
     * Tests the fix for Bug #258.
     * @throws Exception
     *             Test fails
     */
    @Test
    public void testPreventClassPathShading() throws Exception {

        // create a new temporary java project and copy java class used as an input for CobiGen
        String testProjectName = "TestInputProj";
        IJavaProject project = tmpMavenProjectRule.createProject(testProjectName);
        tmpMavenProjectRule.createPom(
            // @formatter:off
            "<dependencies>" + "<dependency>" + "<groupId>javax.ws.rs</groupId>"
                + "<artifactId>javax.ws.rs-api</artifactId>" + "<version>2.0</version>" + "</dependency>"
                + "</dependencies>");
        // @formatter:on
        FileUtils.copyFile(new File(resourcesRootPath + "input/JavaClass.java"), project
            .getUnderlyingResource().getLocation().append("src/main/java/main/JavaClass.java").toFile());
        project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        tmpMavenProjectRule.updateProject();

        // import the configuration project for this test
        EclipseUtils.importExistingGeneralProject(bot,
            new File(resourcesRootPath + "templates").getAbsolutePath());
        EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem =
            view.bot().tree().expandNode(testProjectName, "src/main/java", "main", "JavaClass.java");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment1");
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IFile generationResult = project.getProject().getFile("TestOutput.txt");
        assertThat(IOUtils.toString(generationResult.getContents()))
            .isEqualTo("@javax.ws.rs.Path(\"/PATH\") @javax.ws.rs.Path(\"/PATH\")");
    }

}
