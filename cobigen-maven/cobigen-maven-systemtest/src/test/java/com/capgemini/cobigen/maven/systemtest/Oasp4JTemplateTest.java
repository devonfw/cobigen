package com.capgemini.cobigen.maven.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import com.capgemini.cobigen.maven.test.AbstractMavenTest;

/** Test suite for testing the maven plugin with whole released template sets */
public class Oasp4JTemplateTest extends AbstractMavenTest {

    /** Root of all test resources of this test suite */
    public static final String TEST_RESOURCES_ROOT = "src/test/resources/testdata/systemtest/Oasp4JTemplateTest/";

    /**
     * Processes a generation of oasp4j template increments daos and entity_infrastructure and just checks
     * whether the files have been generated. Takes an entity (POJO) as input.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testEntityInputDataaccessGeneration() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestEntityInputDataaccessGeneration/");
        File testProjectRoot = runMavenInvoker(testProject);

        assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 3 from entity_infrastructure, 4 from daos, 1 input file
        assertThat(numFilesInTarget).isEqualTo(8);
    }

    /**
     * Processes a generation of oasp4j template increments daos and entity_infrastructure and just checks
     * whether the files have been generated. Takes an entity (POJO) as input. <br/>
     * This is the same test as {@link #testEntityInputDataaccessGeneration()} but using the oasp4j templates
     * version 2.0.0. Those templates use Java classes that need to be loaded by the maven plugin
     * @throws Exception
     *             test fails
     */
    @Test
    public void testEntityInputDataaccessGenerationForTemplates2_0_0() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestEntityInputDataaccessGenerationWithTemplates2-0-0/");
        File testProjectRoot = runMavenInvoker(testProject);

        assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 3 from entity_infrastructure, 4 from daos, 1 input file
        assertThat(numFilesInTarget).isEqualTo(8);
    }

    /**
     * Processes a generation of oasp4j template increments daos and entity_infrastructure and just checks
     * whether the files have been generated. Takes an entity (POJO) as input. <br/>
     * This is the same test as {@link #testEntityInputDataaccessGeneration()} but using the oasp4j templates
     * version 2.0.0. Those templates use Java classes that need to be loaded by the maven plugin
     * @throws Exception
     *             test fails
     */
    @Test
    public void testEntityInputDataaccessGenerationForTemplateFolder() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestEntityInputDataaccessGenerationWithTemplateFolder/");
        File templatesProject = new File(TEST_RESOURCES_ROOT, "templates-oasp4j");
        File testProjectRoot = runMavenInvoker(testProject, templatesProject);

        assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        assertThat(numFilesInTarget).isEqualTo(2);
    }

    /**
     * Processes a generation of oasp4j template increments daos and entity_infrastructure and just checks
     * whether the files have been generated. Takes a package as input.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testPackageInputDataaccessGeneration() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestPackageInputDataaccessGeneration/");
        File testProjectRoot = runMavenInvoker(testProject);

        assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 2+2 from entity_infrastructure, 4+2 from daos, 2 input files
        assertThat(numFilesInTarget).isEqualTo(12);
    }

    @Test
    public void testClassloadingWithTemplateFolderAndTestClasses() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestClassLoadingWithTemplateFolder/");
        File testTemplatesProject = new File(TEST_RESOURCES_ROOT + "templates-classloading-testclasses/");
        File testProjectRoot = runMavenInvoker(testProject, testTemplatesProject);

        assertThat(testProjectRoot.toPath().resolve("Sample.txt")).hasContent("Test InputEntity name");
    }
}
