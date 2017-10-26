package com.capgemini.cobigen.maven.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.maven.config.constant.MavenMetadata;

/**
 * Abstract implementation of a maven test, running the maven executor and setting the correct local
 * repository.
 */
public class AbstractMavenTest {

    /** Temporary folder rule to create new temporary folder and files */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /** The maven settings file used by maven invoker for test execution */
    protected File mvnSettingsFile;

    /**
     * Copy settings file to get a file handle required by maven invoker API
     * @throws IOException
     *             if the file could not be read/written
     */
    @Before
    public void getSettingsFile() throws IOException {
        mvnSettingsFile = tmpFolder.newFile();
        Files.write(mvnSettingsFile.toPath(),
            IOUtil.toByteArray(AbstractMavenTest.class.getResourceAsStream("/test-maven-settings.xml")));
    }

    /**
     * @see #runMavenInvoker(File, File)
     */
    @SuppressWarnings("javadoc")
    protected File runMavenInvoker(File testProject) throws Exception {
        return runMavenInvoker(testProject, null);
    }

    /**
     * Runs the maven invoker with goal package and the default devon settings file. Makes sure, that the
     * local repository of the executing maven process is used.
     * @param testProject
     *            the test project to build
     * @param templatesProject
     *            the templates project to be used for generation. May be {@code null}
     * @return the temporary copy of the test project, the build was executed in
     * @throws Exception
     *             if anything fails
     */
    protected File runMavenInvoker(File testProject, File templatesProject) throws Exception {
        assertThat(testProject).exists();

        File testProjectRoot = tmpFolder.newFolder();
        FileUtils.copyDirectoryStructure(testProject, testProjectRoot);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(testProjectRoot);
        request.setGoals(Collections.singletonList("package"));
        setTestProperties(request, templatesProject);
        request.setShowErrors(true);
        request.setDebug(false);
        request.setGlobalSettingsFile(mvnSettingsFile);

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
        mavenProperties.put("locRep", MavenMetadata.LOCAL_REPO);
        if (templatesProject != null) {
            mavenProperties.put("templatesProject", templatesProject.getAbsolutePath());
        }
        request.setProperties(mavenProperties);
    }
}
