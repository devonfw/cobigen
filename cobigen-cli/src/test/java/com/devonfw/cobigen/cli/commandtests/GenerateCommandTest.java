package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.devonfw.cobigen.cli.CobiGenCLI;

/**
 * Tests the usage of the generate command
 */
public class GenerateCommandTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";
    /** Declare ArrayList variable for adding generate increment*/
    ArrayList<File> geneatedList = new ArrayList<File>();

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

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--increments";
        args[3] = "8";

        CobiGenCLI.main(args);

        File generatedFiles = baseProject.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();
        geneatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(geneatedList);
        geneatedList.clear();
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

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootPath.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "0";

        CobiGenCLI.main(args);

        File generatedFiles = outputRootPath.toPath()
            .resolve("src/main/java/com/maven/project/sampledatamanagement/dataaccess/api/repo").toFile();

        geneatedList.add(generatedFiles);
		// If you want to remove the generated files
		geneatedList.add(outputRootPath.toPath().resolve("src").toFile());
		geneatedList.add(outputRootPath.getParentFile().toPath().resolve("api").toFile());
		GenerateCommandTest.deleteGeneratedFiles(geneatedList);
		geneatedList.clear();
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

        String args[] = new String[6];
        args[0] = "generate";
        args[1] = openApiFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1,15,22";

        // Act
        CobiGenCLI.main(args);

     // Assert
     		Path rootPath = outputRootFile.toPath();
     		File generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/salemanagement").toFile();
     		geneatedList.add(generatedFiles);
     		generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/shopmanagement").toFile();
     		geneatedList.add(generatedFiles);
     		generatedFiles = new File(testFileRootPath + "/devon4ng-ionic-application-template");
     		geneatedList.add(generatedFiles);
     		geneatedList.add(rootPath.resolve("src").toFile());
     		geneatedList.add(rootPath.resolve("docs").toFile());
     		geneatedList.add(outputRootFile.getParentFile().toPath().resolve("api").toFile());
     		GenerateCommandTest.deleteGeneratedFiles(geneatedList);
     		geneatedList.clear();
    }
    /**
     * This method is check whether generated file is exist or not  
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
     * @throws IOException
     *             when the test was not able to remove the just generated templates
     */
    @Test
    public void generateTemplatesFromEntityTest() throws IOException {
        File baseProject = new File(testFileRootPath + "localmavenproject/maven.project/core/");

        String args[] = new String[4];
        args[0] = "generate";
        args[1] = entityInputFile.getAbsolutePath();
        args[2] = "-t";
        args[3] = "1,5,7";

        CobiGenCLI.main(args);

        File generatedFiles =
            baseProject.toPath().resolve("src/main/java/com/maven/project/general/logic/base").toFile();
        geneatedList.add(generatedFiles);
		generatedFiles = baseProject.toPath().resolve("src/main/java/com/maven/project/general/common").toFile();
		geneatedList.add(generatedFiles);
		generatedFiles = baseProject.getParentFile().toPath()
				.resolve("api/src/main/java/com/maven/project/sampledatamanagement/logic").toFile();
		geneatedList.add(generatedFiles);
		GenerateCommandTest.deleteGeneratedFiles(geneatedList);
		geneatedList.clear();
    }
    
    /**
     *  This method test the unit test of multiple input file (Entity and Open API)
     * @throws IOException 
     */
    @Test
	public void generateFromMultipleTypeInputTest() throws IOException {
		File outputRootFile = new File(testFileRootPath + "generatedcode/root");
		File openApiFile = new File(testFileRootPath + "openAPI.yml");		
		String args[] = new String[6];
		args[0] = "generate";
		args[1] = openApiFile.getAbsolutePath() + "," + entityInputFile.getAbsolutePath();

		args[2] = "--out";
		args[3] = outputRootFile.getAbsolutePath();
		args[4] = "--increments";
		args[5] = "1";

		// Act
		CobiGenCLI.main(args);
		// Assert
		Path rootPath = outputRootFile.toPath();
		File generatedFiles = rootPath.resolve("src/main/java/com/devonfw/angular/test/salemanagement").toFile();
		geneatedList.add(generatedFiles);
		File generateFiles = outputRootFile.toPath().resolve("src/main/java/com/maven/project/general/logic/base")
				.toFile();
		geneatedList.add(generateFiles);
		generateFiles = outputRootFile.toPath().resolve("src/main/java/com/maven/project/general/common").toFile();
		geneatedList.add(generateFiles);
		
		geneatedList.add(outputRootFile.toPath().resolve("src/").toFile());
		geneatedList.add(outputRootFile.getParentFile().toPath().resolve("api").toFile());
		GenerateCommandTest.deleteGeneratedFiles(geneatedList);
		geneatedList.clear();
	}
    
    /**
     *  This method test the unit test of multiple input file (Entity and Open API)
     * @throws IOException 
     */
    @Test
    public void generateFromTsFileTest() throws IOException {
        
        
        File outputRootFile = new File(testFileRootPath + "generatedcode/root");
        File tsFile = new File(testFileRootPath + "some.entity.ts");      
        String args[] = new String[6];
        args[0] = "generate";
        args[1] = tsFile.getAbsolutePath();
        args[2] = "--out";
        args[3] = outputRootFile.getAbsolutePath();
        args[4] = "--increments";
        args[5] = "1,2,3,4,5,6";

        // Act
        CobiGenCLI.main(args);
        
        // Assert
        Path rootPath = new File(testFileRootPath ).toPath();
        File generatedFiles = rootPath.resolve("devon4ng-application-template/src/app").toFile();
        geneatedList.add(generatedFiles);
        generatedFiles = rootPath.resolve("devon4ng-application-template/src/assets").toFile();
        geneatedList.add(generatedFiles);
        generatedFiles = rootPath.resolve("devon4ng-application-template/src/environments").toFile();
        geneatedList.add(generatedFiles);      
        generatedFiles = new File(testFileRootPath + "/devon4ng-application-template");
        geneatedList.add(generatedFiles);
        GenerateCommandTest.deleteGeneratedFiles(geneatedList);
        geneatedList.clear();
    }

}
