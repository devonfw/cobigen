package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.interpolation.os.Os;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the usage of the generate command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class GenerateCommandTest extends AbstractCliTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /** Project to execute tests in */
    private Path tmpProject;

    /** Input Java entity used in the tests */
    private File entityInputFile;

    /**
     * Create tmp project for the generation project and make it delete on exit
     * @throws IOException
     *             creation failed
     */
    @Before
    public void createTmpProjectForGenerationOutput() throws IOException {
        File tmpProject = tempFolder.newFolder("playground", "project");
        FileUtils.copyDirectory(new File(testFileRootPath + "localmavenproject"), tmpProject);
        entityInputFile = tmpProject.toPath().resolve(
            "maven.project/core/src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/SampleDataEntity.java")
            .toFile();
        tmpProject.deleteOnExit();
        this.tmpProject = tmpProject.toPath();
    }

    /**
     * Integration test of the generation of templates from a Java Entity. It does not specify the project to
     * generate the folders to.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromEntityTest() throws Exception {
        File baseProject = tmpProject.resolve("maven.project/core/").toFile();

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "springdata-repository";

        execute(args, true);

        assertThat(
            baseProject.toPath().resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo"))
                .exists();
    }

    /**
     * Test with templates downloaded on demand
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromEntityWithDownloadedTemplatesTest() throws Exception {

        File baseProject = tmpProject.resolve("maven.project/core/").toFile();

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "8";

        execute(args, true);

        assertThat(
            baseProject.toPath().resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo"))
                .exists();
    }

    /**
     * Test generating non-java files from java input
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateNonJavaFilesFromJavaInputTest() throws Exception {
        File inputFile = tmpProject.resolve(
            "maven.project/api/src/main/java/com/maven/project/sampledatamanagement/logic/api/to/SampleDataEto.java")
            .toFile();

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = inputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "app_angular_devon4ng_component";

        execute(args, true);

        // clean up generated files
        assertThat(tmpProject.resolve("../../devon4ng-application-template")).exists();
    }

    /**
     * Integration test of the generation of templates from a Java Entity. It will generate all the templates
     * in the output root path passed.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromEntityWithOutputRootPathTest() throws Exception {
        File outputRootPath = tempFolder.newFolder("outputfolder");
        outputRootPath.deleteOnExit();

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootPath.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "all";

        execute(args, true);

        assertThat(
            outputRootPath.toPath().resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo"))
                .exists();
    }

    /**
     * Integration test of the generation of templates from an OpenAPI file. It will generate all the
     * templates in the output root path passed.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromOpenApiTest() throws Exception {

        // Prepare
        File outputRootFile = tempFolder.newFolder("playground2", "rootoutput");
        File openApiFile = new File(testFileRootPath + "openAPI.yml");

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "ionic_component,OpenAPI_Docs,services";

        execute(args, true);

        Path rootPath = outputRootFile.toPath();
        assertThat(rootPath.resolve("../../devon4ng-ionic-application-template"));
    }

    /**
     * Integration test of the generation of templates from a Java Entity with number selection.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateTemplatesFromEntityTest() throws Exception {
        File baseProject = tmpProject.resolve("maven.project/core/").toFile();

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "-t";
        args[3] = "crud_complex_AbstractBeanMapperSupport";

        execute(args, true);

        assertThat(baseProject.toPath().resolve("src/main/java/com/maven/project/general/")).exists();
    }

    /**
     * This method test the unit test of multiple input file (Entity and Open API)
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromMultipleTypeInputTest() throws Exception {
        File outputRootFile = tempFolder.newFolder("playground2", "rootoutput");
        outputRootFile.deleteOnExit();
        File openApiFile = new File(testFileRootPath + "openAPI.yml");
        String args[] = new String[6];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath() + "," + entityInputFile.getAbsolutePath();

        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "rest_service_impl";

        execute(args, true, true);
    }

    /**
     * This method test the generation from typescript files.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromTsFileTest() throws Exception {

        File outputRootFile = tempFolder.newFolder("playground2", "rootoutput");
        outputRootFile.deleteOnExit();
        File tsFile = new File(testFileRootPath + "some.entity.ts");
        String args[] = new String[6];
        args[0] = "generate";
        args[1] = tsFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1,2,3,4,5,6";

        execute(args, true);

        Path rootPath = outputRootFile.toPath();
        assertThat(rootPath.resolve("../../devon4ng-application-template/src/app")).exists();
        assertThat(rootPath.resolve("../../devon4ng-application-template/src/assets")).exists();
        assertThat(rootPath.resolve("../../devon4ng-application-template/src/environments")).exists();
    }

    /**
     * Integration test of the generation of templates from an input file whose path contains spaces and
     * quotes.
     * @throws Exception
     *             test fails
     */
    @Test
    public void generateFromArgsWithQuote() throws Exception {

        // Prepare
        File tmpFolder = tempFolder.newFolder("playground2", "rootoutput");
        tmpFolder.deleteOnExit();
        Path outputRootPath = tmpFolder.toPath();
        File openApiOriginalFile = new File(testFileRootPath + "openAPI.yml");
        Path openApiFile = outputRootPath.resolve("openAPI file.yml");
        // duplicate openapi file while changing the name
        FileUtils.copyFile(openApiOriginalFile, openApiFile.toFile());

        String args[] = new String[6];
        args[0] = "generate";
        // input file with quote
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            args[1] = '"' + openApiFile.toFile().getAbsolutePath() + '"';
        } else {
            args[1] = openApiFile.toFile().getAbsolutePath().replace(" ", "\\ ");
        }
        args[2] = "--out";
        args[3] = outputRootPath.toFile().getAbsolutePath();
        args[4] = "--increments";
        args[5] = "15";

        execute(args, true);

        assertThat(outputRootPath.resolve("docs")).exists();
    }

}
