package com.capgemini.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;

import com.capgemini.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.capgemini.cobigen.eclipse.test.common.SystemTest;
import com.capgemini.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.capgemini.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.capgemini.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Test suite for class path loader issues.
 */
public class ClassPathLoadingTest extends SystemTest {

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/ClassPathLoadingTest/";

    /**
     * Setup workbench appropriately for tests
     * @throws Exception
     *             test fails
     */
    @BeforeClass
    public static void setupClass() throws Exception {

        // import the configuration project for this test
        EclipseUtils.importExistingGeneralProject(bot,
            new File(resourcesRootPath + "templates").getAbsolutePath());
        EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);
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

}
