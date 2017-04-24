package com.capgemini.cobigen.maven.common;

import java.io.File;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.junit.Rule;

import com.capgemini.cobigen.maven.GenerateMojo;

/** Test utils for maven specific stuff */
public abstract class AbstractMavenTest {

    @Rule
    public MojoRule mojoRule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * Retrieves the CobiGen generate mojo from the maven project with the given pom.
     * @param testPom
     *            root pom of the project under test
     * @return the {@link GenerateMojo}
     * @throws Exception
     *             test fails
     */
    protected GenerateMojo getGenerateMojo(File testPom) throws Exception {
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        ProjectBuilder projectBuilder = mojoRule.lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(testPom, buildingRequest).getProject();

        GenerateMojo mojo = (GenerateMojo) mojoRule.lookupConfiguredMojo(project, "generate");
        return mojo;
    }
}
