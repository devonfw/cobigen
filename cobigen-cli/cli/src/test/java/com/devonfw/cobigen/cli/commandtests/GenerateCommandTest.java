package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.cli.commands.CobiGenCommand;
import com.ea.agentloader.AgentLoader;

import classloader.Agent;
import picocli.CommandLine;

/**
 * Tests the usage of the generate command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class GenerateCommandTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /** Declare ArrayList variable for adding generate increment */
    ArrayList<File> generatedList = new ArrayList<>();

    /**
     * Input Java entity used in the tests
     */
    private File entityInputFile = new File(testFileRootPath
        + "localmavenproject/maven.project/core/src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/SampleDataEntity.java");

    /**
     * Commandline to pass arguments to
     */
    private final CommandLine commandLine = new CommandLine(new CobiGenCommand());

    /**
     * We need to dynamically load the Java agent before the tests. Note that Java 9 requires
     * -Djdk.attach.allowAttachSelf=true to be present among JVM startup arguments.
     */
    @Before
    public void loadJavaAgent() {
        AgentLoader.loadAgentClass(Agent.class.getName(), "");
    }

    /**
     * Integration test of the generation of templates from a Java Entity. It does not specify the project to
     * generate the folders to.
     */
    @Test
    public void generateFromEntityTest() {
        File baseProject = new File(testFileRootPath + "localmavenproject/maven.project/core/");

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "8";

        commandLine.execute(args);

        File generatedFiles = baseProject.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();
        generatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * Test generating non-java files from java input
     */
    @Test
    public void generateNonJavaFilesFromJavaInputTest() {
        File inputFile = new File(testFileRootPath
            + "localmavenproject/maven.project/api/src/main/java/com/maven/project/sampledatamanagement/logic/api/to/SampleDataEto.java");

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = inputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "16";

        commandLine.execute(args);

        // clean up generated files
        File generatedFiles = new File(testFileRootPath + "localmavenproject/devon4ng-application-template");
        generatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * Integration test of the generation of templates from a Java Entity. It will generate all the templates
     * in the output root path passed.
     */
    @Test
    public void generateFromEntityWithOutputRootPathTest() {
        File outputRootPath = new File(testFileRootPath + "generatedcode/root");

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootPath.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "0";

        commandLine.execute(args);

        File generatedFiles = outputRootPath.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();

        generatedList.add(generatedFiles);
        // If you want to remove the generated files
        generatedList.add(outputRootPath.toPath().resolve("src").toFile());
        generatedList.add(outputRootPath.getParentFile().toPath().resolve("api").toFile());
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * Integration test of the generation of templates from an OpenAPI file. It will generate all the
     * templates in the output root path passed.
     */
    @Test
    public void generateFromOpenApiTest() {

        // Prepare
        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File openApiFile = new File(testFileRootPath + "openAPI.yml");

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1,15,22";

        commandLine.execute(args);

        Path rootPath = outputRootFile.toPath();
        File generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/salemanagement").toFile();
        generatedList.add(generatedFiles);
        generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/shopmanagement").toFile();
        generatedList.add(generatedFiles);
        generatedFiles = new File(testFileRootPath + "/devon4ng-ionic-application-template");
        generatedList.add(generatedFiles);
        generatedList.add(rootPath.resolve("src").toFile());
        generatedList.add(rootPath.resolve("docs").toFile());
        generatedList.add(outputRootFile.getParentFile().toPath().resolve("api").toFile());
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * This method is check whether generated file is exist or not
     * @param generateFiles
     *            list of generated files
     */
    private static void deleteGeneratedFiles(ArrayList<File> generateFiles) {

        for (File generatedFile : generateFiles) {
            assertTrue(generatedFile.exists());
            try {
                FileUtils.deleteDirectory(generatedFile);
            } catch (IOException e) {
                continue;
            }
        }

    }

    /**
     * Integration test of the generation of templates from a Java Entity with number selection.
     */
    @Test
    public void generateTemplatesFromEntityTest() {
        File baseProject = new File(testFileRootPath + "localmavenproject/maven.project/core/");

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "-t";
        args[3] = "1";

        commandLine.execute(args);

        File generatedFiles = baseProject.toPath().resolve("src/main/java/com/maven/project/general/").toFile();
        generatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * This method test the unit test of multiple input file (Entity and Open API)
     */
    @Test
    public void generateFromMultipleTypeInputTest() {
        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File openApiFile = new File(testFileRootPath + "openAPI.yml");
        String args[] = new String[6];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath() + "," + entityInputFile.getAbsolutePath();

        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1";

        commandLine.execute(args);

        Path rootPath = outputRootFile.toPath();
        File generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/salemanagement").toFile();
        generatedList.add(generatedFiles);
        File generateFiles =
            outputRootFile.toPath().resolve("src/main/java/com/maven/project/general/logic/base").toFile();
        generatedList.add(generateFiles);
        generateFiles = outputRootFile.toPath().resolve("src/main/java/com/maven/project/general/common").toFile();
        generatedList.add(generateFiles);

        generatedList.add(outputRootFile.toPath().resolve("src/").toFile());
        generatedList.add(outputRootFile.getParentFile().toPath().resolve("api").toFile());
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * This method test the generation from typescript files.
     */
    @Test
    public void generateFromTsFileTest() {

        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File tsFile = new File(testFileRootPath + "some.entity.ts");
        String args[] = new String[6];
        args[0] = "generate";
        args[1] = tsFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1,2,3,4,5,6";

        commandLine.execute(args);

        Path rootPath = new File(testFileRootPath).toPath();
        File generatedFiles = rootPath.resolve("devon4ng-application-template/src/app").toFile();
        generatedList.add(generatedFiles);
        generatedFiles = rootPath.resolve("devon4ng-application-template/src/assets").toFile();
        generatedList.add(generatedFiles);
        generatedFiles = rootPath.resolve("devon4ng-application-template/src/environments").toFile();
        generatedList.add(generatedFiles);
        generatedFiles = new File(testFileRootPath + "/devon4ng-application-template"); //$NON-NLS-1$
        generatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        generatedList.clear();
    }

    /**
     * Integration test of the generation of templates from an input file whose path contains spaces and
     * quotes.
     * @throws IOException
     */
    @Test
    public void generateFromArgsWithQuote() throws IOException {

        // Prepare
        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File openApiOriginalFile = new File(testFileRootPath + "openAPI.yml");
        File openApiFile = new File(testFileRootPath + "openAPI file.yml");
        // duplicate openapi file while changing the name
        FileUtils.copyFile(openApiOriginalFile, openApiFile);

        String args[] = new String[6];
        args[0] = "generate";
        // input file with quote
        args[1] = '"' + openApiFile.getAbsolutePath() + '"';
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "15";

        commandLine.execute(args);

        Path rootPath = outputRootFile.toPath();
        generatedList.add(rootPath.resolve("docs").toFile());
        GenerateCommandTest.deleteGeneratedFiles(generatedList);
        openApiFile.delete();
        generatedList.clear();
    }
}
