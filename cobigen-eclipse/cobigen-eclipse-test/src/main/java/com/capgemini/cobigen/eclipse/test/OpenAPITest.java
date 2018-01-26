package com.capgemini.cobigen.eclipse.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.capgemini.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.capgemini.cobigen.eclipse.test.common.SystemTest;
import com.capgemini.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.capgemini.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.capgemini.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenAPITest extends SystemTest {

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

    /**
     * Setup workbench appropriately for tests
     * @throws Exception
     *             test fails
     */
    @BeforeClass
    public static void setupClass() throws Exception {

        try {
            // import the configuration project for this test
            EclipseUtils.importExistingGeneralProject(bot, new File(resourcesRootPath + "templates").getAbsolutePath());
            EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test for external projects (not in workspace) taken as input for generation
     * @throws Exception
     *             test fails
     */
    @Test
    public void testBasicOpenAPIGeneration() throws Exception {

        // copy sample project to external location and import it into the workspace
        File tmpFolder = tmpFolderRule.newFolder();
        String testProjName = "ExtTestProj";
        FileUtils.copyDirectory(new File(resourcesRootPath + "input/" + testProjName), tmpFolder);
        EclipseUtils.importExistingGeneralProject(bot, tmpFolder.getAbsolutePath(), true);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "devonfw.yml");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "CRUD REST services");
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
        IFile generationResult = proj.getFile("src/main/java/TestOutput2.txt");
        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualTo("This is a test");
        }
    }

}
