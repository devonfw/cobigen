package com.capgemini.cobigen.maven.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testEntityInputDataaccessGeneneration() throws Exception {
        File testProject = new File("src/test/resources/testdata/systemtest/Oasp4JTemplateTest/");
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

    }

}
