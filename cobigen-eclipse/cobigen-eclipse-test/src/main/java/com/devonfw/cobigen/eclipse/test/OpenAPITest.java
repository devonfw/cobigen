package com.devonfw.cobigen.eclipse.test;

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

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.test.common.SystemTest;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseUtils;

/**
 * General Eclipse Plug-in Tests.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenAPITest extends SystemTest {

    /** Root path of the Test Resources */
    private static final String resourcesRootPath = "src/main/resources/OpenAPITest/";

    /** Line separator, e.g. for windows '\r\n' */
    public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

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
        EclipseUtils.updateMavenProject(bot, testProjName);

        // expand the new file in the package explorer
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem javaClassItem = view.bot().tree().expandNode(testProjName, "devonfw.yml");
        javaClassItem.select();

        // execute CobiGen
        EclipseCobiGenUtils.processCobiGen(bot, javaClassItem, "CRUD REST services");
        // increase timeout as the openAPI parser is slow on initialization
        EclipseCobiGenUtils.confirmSuccessfullGeneration(bot, 40000);

        // check assertions
        bot.waitUntil(new AllJobsAreFinished(), 10000);
        IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProjName);
        IFile generationResult = proj
            .getFile("src/com/devonfw/test/sampledatamanagement/service/api/rest/SampledatamanagementRestService.java");

        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualToIgnoringWhitespace(
                "package com.devonfw.test.sampledatamanagement.service.api.rest;" + LINE_SEPARATOR + LINE_SEPARATOR + //
                    "import java.awt.PageAttributes.MediaType;" + LINE_SEPARATOR + LINE_SEPARATOR + //
                    "public interface SampledatamanagementRestService {" + LINE_SEPARATOR + //
                    LINE_SEPARATOR + //
                    "    @GET" + LINE_SEPARATOR + //
                    "    @Path(\"/sampledata/custom/{id}/\")" + LINE_SEPARATOR + //
                    "    @Produces(MediaType.APPLICATION_JSON_VALUE)" + LINE_SEPARATOR + //
                    "    public SampleData customMethod(@PathParam(\"id\") @Max(100) @Min(0) long id);" + LINE_SEPARATOR
                    + //
                    LINE_SEPARATOR + //
                    "    @DELETE" + LINE_SEPARATOR + //
                    "    @Path(\"/sampledata/custom/{id}/\")" + LINE_SEPARATOR + //
                    "    @Produces(MediaType.APPLICATION_JSON_VALUE)" + LINE_SEPARATOR + //
                    "    public Boolean deleteCustomSampleData(@PathParam(\"id\") @Max(100) @Min(0) long id);"
                    + LINE_SEPARATOR + //
                    LINE_SEPARATOR + //
                    "    @POST" + LINE_SEPARATOR + //
                    "    @Path(\"/sampledata/customSave/\")" + LINE_SEPARATOR + //
                    "    @Consumes(MediaType.APPLICATION_JSON_VALUE)" + LINE_SEPARATOR + //
                    "    @Produces(MediaType.APPLICATION_JSON_VALUE)" + LINE_SEPARATOR + //
                    "    public PaginatedListTo<SampleDataEto> saveCustomSampleData(SampleDataEto sampleData);"
                    + LINE_SEPARATOR + //
                    LINE_SEPARATOR + //
                    "    @POST" + LINE_SEPARATOR + //
                    "    @Path(\"/sampledata/customSearch/\")" + LINE_SEPARATOR + //
                    "    @Consumes(MediaType.APPLICATION_JSON_VALUE)" + LINE_SEPARATOR + //
                    "    @Produces(MediaType.IMAGE_PNG_VALUE)" + LINE_SEPARATOR + //
                    "    public PaginatedListTo<SampleDataEto> findCustomSampleDataEtos(SampleDataSearchCriteriaTo criteria);"
                    + LINE_SEPARATOR + //
                    LINE_SEPARATOR + //
                    "}");
        }

        generationResult =
            proj.getFile("src/com/devonfw/test/moredatamanagement/service/api/rest/MoredatamanagementRestService.java");
        try (InputStream in = generationResult.getContents()) {
            assertThat(IOUtils.toString(in)).isEqualToIgnoringWhitespace(
                "package com.devonfw.test.moredatamanagement.service.api.rest;" + LINE_SEPARATOR + LINE_SEPARATOR + //
                    "public interface MoredatamanagementRestService {" + LINE_SEPARATOR + //
                    LINE_SEPARATOR + //
                    "}");
        }
    }
}
