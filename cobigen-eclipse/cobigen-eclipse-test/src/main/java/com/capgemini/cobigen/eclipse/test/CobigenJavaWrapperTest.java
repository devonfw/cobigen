package com.capgemini.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.capgemini.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.capgemini.cobigen.eclipse.test.common.EclipseCobiGenUtils;
import com.capgemini.cobigen.eclipse.test.common.EclipseUtils;
import com.capgemini.cobigen.eclipse.test.common.junit.TmpMavenProjectRule;
import com.capgemini.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CobigenJavaWrapperTest {

    /** Rule for creating temporary {@link IJavaProject}s per test. */
    @Rule
    public TmpMavenProjectRule tmpMavenProjectRule = new TmpMavenProjectRule();

    /** Rule for creating temporary files and folders */
    @Rule
    public TemporaryFolder tmpFolderRule = new TemporaryFolder();

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/CobiGenJavaWrapperTest/";

    /** {@link SWTBot} for UI controls */
    private static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    /**
     * Setup workbench appropriately for tests
     * @throws Exception
     *             test fails
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        try {
            bot.viewByTitle("Welcome").close();
        } catch (WidgetNotFoundException e) {
            // ignore
        }
        bot.resetWorkbench();
        cleanWorkspace(true);

        SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
        perspective.activate();

        // import the configuration project for this test
        EclipseUtils.importExistingGeneralProject(bot,
            new File(resourcesRootPath + "templates").getAbsolutePath());
        EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

        // this flag is set to be true and will suppress ErrorDialogs,
        // which is completely strange, so we enable them again.
        ErrorDialog.AUTOMATED_MODE = false;
    }

    /**
     * Reset workbench and open java perspective
     * @throws Exception
     *             test fails
     */
    @Before
    public void before() throws Exception {
        bot.resetWorkbench();
        cleanWorkspace(false);

        SWTBotPerspective perspective = bot.perspectiveById(JavaUI.ID_PERSPECTIVE);
        perspective.activate();
    }

    /**
     * Cleanup workspace by deleting all projects
     * @param cleanCobiGenConfiguration
     *            if <code>true</code>, the cobigen configuration project will be removed as well
     * @throws Exception
     *             test fails
     */
    private static void cleanWorkspace(boolean cleanCobiGenConfiguration) throws Exception {

        int maxRetries = 10;

        for (int i = 1; i <= maxRetries; i++) {
            IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            try {
                for (IProject project : allProjects) {
                    if (cleanCobiGenConfiguration
                        || !ResourceConstants.CONFIG_PROJECT_NAME.equals(project.getName())) {
                        project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                        project.close(new NullProgressMonitor());
                        project.delete(true, true, new NullProgressMonitor());
                    }
                }
                break;
            } catch (Exception e) {
                Thread.sleep(500);
                if (i == maxRetries) {
                    throw e;
                }
            }
        }
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
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
        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in))
                .isEqualTo("@javax.ws.rs.Path(value=/PATH) @javax.ws.rs.Path(value=/PATH)");
        }
    }

    /**
     * Test for external projects (not in workspace) taken as input for generation
     * @throws Exception
     *             test fails
     */
    @Test
    public void testWorkspaceExternalProjectAsInput() throws Exception {

        // copy sample project to external location and import it into the workspace
        File tmpFolder = tmpFolderRule.newFolder();
        String testProjName = "ExtTestProj";
        FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
        EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem =
            view.bot().tree().expandNode(testProjName, "src", "main", "SimpleInput.java");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment2");
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
        IFile generationResult = proj.getFile("TestOutput2.txt");
        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualTo("This is a test");
        }
    }

    /**
     * Test the cobigen-eclipse internal path invariants. As there are so many different relative paths in
     * place, this test will just do a generation smoke test with a nested file.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testPathInvariants() throws Exception {

        // copy sample project to external location and import it into the workspace
        File tmpFolder = tmpFolderRule.newFolder();
        String testProjName = "ExtTestProj";
        FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
        EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), false);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem =
            view.bot().tree().expandNode(testProjName, "src", "main", "SimpleInput.java");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "increment3");
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
        IFile generationResult = proj.getFile("subfolder/TestOutput3.txt");
        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualTo("Content does not matter!");
        }
    }
}
