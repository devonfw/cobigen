package com.cobigen.picocli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.cobigen.picocli.CobiGenCLI;

/**
 * Tests the usage of the generate command
 */
public class GenerateCommandTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /**
     * Input Java entity used in the tests
     */
    private File entityInputFile = new File(testFileRootPath
        + "localmavenproject/maven.project/core/src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/SampleDataEntity.java");

    /**
     * Integration test of the generation of templates from a Java Entity. It does not specify the project to
     * generate the folders to.
     * @throws IOException
     *             when the test was not able to remove the just generated templates
     */
    @Test
    public void generateFromEntityTest() throws IOException {
        File baseProject = new File(testFileRootPath + "localmavenproject/maven.project/core/");

        String args[] = new String[2];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();

        CobiGenCLI.main(args);

        File generatedFiles = baseProject.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();

        assertTrue(generatedFiles.exists());
        // If you want to remove the generated files
        FileUtils.deleteDirectory(generatedFiles);
    }

    /**
     * Integration test of the generation of templates from a Java Entity. It will generate all the templates
     * in the output root path passed.
     * @throws IOException
     *             when the test was not able to remove the just generated templates
     */
    @Test
    public void generateFromEntityWithOutputRootPathTest() throws IOException {
        File outputRootPath = new File(testFileRootPath + "generatedcode/root");

        String args[] = new String[3];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = outputRootPath.getAbsolutePath();

        CobiGenCLI.main(args);

        File generatedFiles = outputRootPath.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();

        assertTrue(generatedFiles.exists());
        // If you want to remove the generated files
        FileUtils.deleteDirectory(outputRootPath.toPath().resolve("src").toFile());
        FileUtils.deleteDirectory(outputRootPath.getParentFile().toPath().resolve("api").toFile());
    }

    /**
     * Integration test of the generation of templates from an OpenAPI file. It will generate all the
     * templates in the output root path passed.
     * @throws IOException
     *             when the test was not able to remove the just generated templates
     */
    @Test
    public void generateFromOpenApiTest() throws IOException {
        // Prepare
        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File openApiFile = new File(testFileRootPath + "openAPI.yml");

        String args[] = new String[3];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath();
        args[2] = outputRootFile.getAbsolutePath();

        // Act
        CobiGenCLI.main(args);

        // Assert
        Path rootPath = outputRootFile.toPath();

        File generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/salemanagement").toFile();
        assertTrue(generatedFiles.exists());

        generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/shopmanagement").toFile();
        assertTrue(generatedFiles.exists());

        generatedFiles = new File(testFileRootPath + "/devon4ng-ionic-application-template");
        assertTrue(generatedFiles.exists());

        // If you want to remove the generated files
        FileUtils.deleteDirectory(rootPath.resolve("src").toFile());
        FileUtils.deleteDirectory(rootPath.resolve("docs").toFile());
        FileUtils.deleteDirectory(outputRootFile.getParentFile().toPath().resolve("api").toFile());

        Path frontEndPath = generatedFiles.getParentFile().toPath();
        FileUtils.deleteDirectory(generatedFiles);
        FileUtils.deleteDirectory(frontEndPath.resolve("devon4ng-application-template").toFile());
        FileUtils.deleteDirectory(frontEndPath.resolve("devon4net-application-template").toFile());
    }

}
