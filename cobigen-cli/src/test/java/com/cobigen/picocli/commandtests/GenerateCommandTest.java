package com.cobigen.picocli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

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
     * Integration test of the generation of templates from a Java Entity. It will generate all the templates
     * in the project folder passed.
     * @throws IOException
     *             when the test was not able to remove the just generated templates
     */
    @Test
    public void generateFromEntityWithProjectTest() throws IOException {
        File baseProject = new File(testFileRootPath + "localmavenproject/maven.project/core/");
        System.out.println("Path => Inside generateEntityTest " + baseProject.getAbsoluteFile());

        File inputFile = baseProject.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/SampleDataEntity.java")
            .toFile();

        String args[] = new String[3];
        args[0] = "generate";
        args[1] = inputFile.getAbsolutePath();
        args[2] = baseProject.getAbsolutePath();

        CobiGenCLI.main(args);

        File generatedFiles = baseProject.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();

        assertTrue(generatedFiles.exists());
        // If you want to remove the generated files
        FileUtils.deleteDirectory(generatedFiles);
    }

}
