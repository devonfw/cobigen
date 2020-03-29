package com.devonfw.cobigen.maven.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.maven.config.constant.MavenMetadata;

/**
 * Abstract implementation of a maven test, running the maven executor and setting the correct local
 * repository.
 */
public class AbstractMavenTest {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMavenTest.class);

    /** Temporary folder rule to create new temporary folder and files */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /** The maven settings file used by maven invoker for test execution */
    protected File mvnSettingsFile;

    /**
     * Set maven.home system property to enable maven invoker execution
     */
    @Before
    public void setMavenHome() {

        String m2Home = System.getenv().get("MAVEN_HOME");
        if (m2Home != null) {
            System.setProperty("maven.home", m2Home);
        } else {
            m2Home = System.getenv().get("M2_HOME");
            if (m2Home != null) {
                System.setProperty("maven.home", m2Home);
            } else if ("true".equals(System.getenv("TRAVIS"))) {
                System.setProperty("maven.home", "/usr/local/maven"); // travis only
            } else {
                LOG.warn("Could not determine maven home from environment variables MAVEN_HOME or M2_HOME");
            }
        }
    }

    /**
     * Copy settings file to get a file handle required by maven invoker API
     * @throws IOException
     *             if the file could not be read/written
     */
    @Before
    public void getSettingsFile() throws IOException {
        mvnSettingsFile = tmpFolder.newFile();
        Files.write(mvnSettingsFile.toPath(),
            IOUtils.toByteArray(AbstractMavenTest.class.getResourceAsStream("/test-maven-settings.xml")));
        LOG.info("Temporary settings file created in " + mvnSettingsFile.getAbsolutePath());
    }

    /**
     * Runs the maven invoker with goal package. Makes sure, that the local repository of the executing maven
     * process is used.
     * @param testProject
     *            the test project to build
     * @param localRepoPath
     *            local repository path of the current execution
     * @return the temporary copy of the test project, the build was executed in
     * @throws Exception
     *             if anything fails
     */
    protected File runMavenInvoker(File testProject, String localRepoPath) throws Exception {
        return runMavenInvoker(testProject, null, localRepoPath);
    }

    /**
     * Runs the maven invoker with goal package and the default devonfw settings file. Makes sure, that the
     * local repository of the executing maven process is used.
     * @param testProject
     *            the test project to build
     * @param templatesProject
     *            the templates project to be used for generation. May be {@code null}
     * @param localRepoPath
     *            local repository path of the current execution
     * @return the temporary copy of the test project, the build was executed in
     * @throws Exception
     *             if anything fails
     */
    protected File runMavenInvoker(File testProject, File templatesProject, String localRepoPath) throws Exception {
        return runMavenInvoker(testProject, templatesProject, localRepoPath, false);
    }

    /**
     * Runs the maven invoker with goal package and the default devonfw settings file. Makes sure, that the
     * local repository of the executing maven process is used.
     * @param testProject
     *            the test project to build
     * @param templatesProject
     *            the templates project to be used for generation. May be {@code null}
     * @param localRepoPath
     *            local repository path of the current execution
     * @param debug
     *            enable debug logging
     * @return the temporary copy of the test project, the build was executed in
     * @throws Exception
     *             if anything fails
     */
    protected File runMavenInvoker(File testProject, File templatesProject, String localRepoPath, boolean debug)
        throws Exception {
        assertThat(testProject).exists();

        File testProjectRoot = tmpFolder.newFolder();
        FileUtils.copyDirectory(testProject, testProjectRoot);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(testProjectRoot);
        request.setGoals(Collections.singletonList("package"));
        setTestProperties(request, templatesProject);
        request.getProperties().put("locRep", localRepoPath);
        request.setShowErrors(true);
        request.setDebug(debug);
        request.setGlobalSettingsFile(mvnSettingsFile);
        request.setUserSettingsFile(mvnSettingsFile);
        request.setMavenOpts("-Xmx4096m");

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).as("Exit Code").isEqualTo(0);

        return testProjectRoot;
    }

    /**
     * Set test properties to the maven environment to be used in the test pom.xml files
     * @param request
     *            to be enriched by the test properties.
     * @param templatesProject
     *            the templates project to be used for generation. May be {@code null}
     */
    protected void setTestProperties(InvocationRequest request, File templatesProject) {
        Properties mavenProperties = new Properties();
        mavenProperties.put("pluginVersion", MavenMetadata.VERSION);

        if (templatesProject != null) {
            mavenProperties.put("templatesProject", templatesProject.getAbsolutePath());
        }
        request.setProperties(mavenProperties);
    }
}
