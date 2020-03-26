package com.devonfw.cobigen.maven.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.Test;

import com.devonfw.cobigen.maven.systemtest.config.constant.MavenMetadata;
import com.devonfw.cobigen.maven.test.AbstractMavenTest;

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
        File testProjectRoot = runMavenInvoker(testProject, MavenMetadata.LOCAL_REPO);

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
        File testProjectRoot = runMavenInvoker(testProject, templatesProject, MavenMetadata.LOCAL_REPO);

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
        File testProjectRoot = runMavenInvoker(testProject, MavenMetadata.LOCAL_REPO);

        assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 2+2 from entity_infrastructure, 4+2 from daos, 2 input files
        assertThat(numFilesInTarget).isEqualTo(12);
    }

    /**
     * Test class loading for a configuration folder with java util classes as well as test classes
     * @throws Exception
     *             test fails
     */
    @Test
    public void testClassloadingWithTemplateFolderAndTestClasses() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestClassLoadingWithTemplateFolder/");
        File testTemplatesProject = new File(TEST_RESOURCES_ROOT + "templates-classloading-testclasses/");
        File testProjectRoot = runMavenInvoker(testProject, testTemplatesProject, MavenMetadata.LOCAL_REPO);

        assertThat(testProjectRoot.toPath().resolve("Sample.txt")).hasContent("Test InputEntity asdf");
    }

    /**
     * Test class loading for a configuration folder with java util classes as well as test classes
     * @throws Exception
     *             test fails
     */
    @Test
    public void testNothingGenerated() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestNothingToGenerate/");
        File testTemplatesProject = new File(TEST_RESOURCES_ROOT + "templates-nomatch/");
        runMavenInvoker(testProject, testTemplatesProject, MavenMetadata.LOCAL_REPO);
    }

    /**
     * Test class loading for a configuration folder with java util classes as well as test classes
     * @throws Exception
     *             test fails
     */
    @Test
    public void testThrowExceptionOnNothingGenerated() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestExceptionOnNothingToGenerate/");
        File testTemplatesProject = new File(TEST_RESOURCES_ROOT + "templates-nomatch/");

        assertThat(testProject).exists();

        File testProjectRoot = tmpFolder.newFolder();
        FileUtils.copyDirectory(testProject, testProjectRoot);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(testProjectRoot);
        request.setGoals(Collections.singletonList("package"));
        setTestProperties(request, testTemplatesProject);
        request.setShowErrors(true);
        request.setDebug(false);
        request.setGlobalSettingsFile(mvnSettingsFile);
        request.setUserSettingsFile(mvnSettingsFile);

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExitCode()).as("Exit Code").isEqualTo(1);
        assertThat(result.getExecutionException()).isNull();
    }

    /**
     * Tries to reproduce issue #715 https://github.com/devonfw/cobigen/issues/715 where a Windows path
     * exception is thrown when trying to generate from an OpenApi file. For doing so, processes a generation
     * of oasp4j template increments daos, entity_infrastructure, TOs, Logic and Rest Service and just checks
     * whether the files have been generated. Takes a yaml file as input.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testDifferentFileSystemThrowsNoProviderMismatchException() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestDifferentFileSystems/");
        File testProjectRoot = runMavenInvoker(testProject, MavenMetadata.LOCAL_REPO);
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 4 from from daos + 4 from entity infrastructure + 6 from TOs + 4 from Logic (all in one) + 2 rest
        // service imp = 18
        assertThat(numFilesInTarget).isEqualTo(33);
    }

}
