package com.capgemini.cobigen.maven.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.maven.config.constant.MavenMetadata;

/** Test suite for testing the maven plugin with whole released template sets */
public class Oasp4JTemplateTest {

    /** Root of all test resources of this test suite */
    public static final String TEST_RESOURCES_ROOT = "src/test/resources/testdata/systemtest/Oasp4JTemplateTest/";

    /** Temporary folder rule to create new temporary folder and files */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Processes a generation of oasp4j template increments daos and entity_infrastructure and just checks
     * whether the files have been generated. Takes an entity (POJO) as input.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testEntityInputDataaccessGeneration() throws Exception {
        File testProject = new File(TEST_RESOURCES_ROOT + "TestEntityInputDataaccessGeneration/");
        assertThat(testProject).exists();

        File testProjectRoot = tmpFolder.newFolder();
        FileUtils.copyDirectoryStructure(testProject, testProjectRoot);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(testProjectRoot);
        request.setGoals(Collections.singletonList("package"));
        Properties mavenProperties = new Properties();
        mavenProperties.put("pluginVersion", MavenMetadata.VERSION);
        request.setProperties(mavenProperties);
        request.setShowErrors(true);

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).as("Exit Code").isEqualTo(0);

        assertThat(testProjectRoot.list()).containsExactly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 3 from entity_infrastructure, 4 from daos, 1 input file
        assertThat(numFilesInTarget).isEqualTo(8);
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
        assertThat(testProject).exists();

        File testProjectRoot = tmpFolder.newFolder();
        FileUtils.copyDirectoryStructure(testProject, testProjectRoot);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(testProjectRoot);
        request.setGoals(Collections.singletonList("package"));
        Properties mavenProperties = new Properties();
        mavenProperties.put("pluginVersion", MavenMetadata.VERSION);
        request.setProperties(mavenProperties);
        request.setShowErrors(true);

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).as("Exit Code").isEqualTo(0);

        assertThat(testProjectRoot.list()).containsExactly("pom.xml", "src", "target");
        long numFilesInTarget =
            Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
        // 2+2 from entity_infrastructure, 4+2 from daos, 2 input files
        assertThat(numFilesInTarget).isEqualTo(12);
    }
}
