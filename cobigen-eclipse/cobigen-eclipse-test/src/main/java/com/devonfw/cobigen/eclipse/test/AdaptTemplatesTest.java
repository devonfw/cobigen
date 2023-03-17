package com.devonfw.cobigen.eclipse.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * Class for testing Adapt Templates with a following Generate process
 *
 */
public class AdaptTemplatesTest extends SystemTest {

    /** Temporary files rule to create temporary folders or files */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

    /** Line separator, e.g. for windows '\r\n' */
    public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

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
     * Test generation with OpenAPI input after adapting templates
     *
     * @throws Exception test fails
     */
    public void testBasicOpenAPIGenerationWithAdaptTemplates() throws Exception {

        // copy sample project to external location and import it into the workspace
        String testProjName = "ExtTestProj";
        IJavaProject project = this.tmpMavenProjectRule.createProject(testProjName);
        FileUtils.copyFile(new File(resourcesRootPath + "input/adapt-templates.yml"),
                project.getUnderlyingResource().getLocation().append("adapt-templates.yml").toFile());
        project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        this.tmpMavenProjectRule.updateProject();

        EclipseCobiGenUtils.runAndCaptureUpdateTemplates(bot);
        EclipseCobiGenUtils.runAndCaptureAdaptTemplates(bot);
        EclipseUtils.updateMavenProject(bot, ResourceConstants.CONFIG_PROJECT_NAME);

        EclipseUtils.openErrorsTreeInProblemsView(bot);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "adapt-templates.yml");
        javaClassItem.select();

        IProject adaptedTemplatesProj = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(ConfigurationConstants.COBIGEN_TEMPLATES);

        assertThat(adaptedTemplatesProj.exists()).isTrue();
    }

    /**
     * Test of testBasicOpenAPIGenerationWithAdaptTemplates with custom COBIGEN_HOME
     * environment variable
     *
     * @throws Exception test fails
     */
    @Test
    public void testAdaptTemplates() throws Exception {

        File tmpProject = this.tempFolder.newFolder("playground", "project", "templates");
        withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.getParentFile().getAbsolutePath())
                .execute(() -> testBasicOpenAPIGenerationWithAdaptTemplates());
    }
}
