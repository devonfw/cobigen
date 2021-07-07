package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 * Tests the usage of the update command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class UpdateCommandTest extends AbstractCliTest {

    /**
     * Original CobiGen CLI pom file
     */
    private File originalPom = null;

    /**
     * Sets of the correct CLI root path.
     * @throws Exception
     *             execution failed
     */
    @Before
    public void setCliPath() throws Exception {
        SystemLambda.withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString())
            .execute(() -> {
                originalPom = CobiGenUtils.extractArtificialPom();
            });
    }

    /**
     * Reads the given pom file and extracts the dependencies.
     * @param pomFile
     *            input pom file
     * @return a list of dependencies
     * @throws FileNotFoundException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws XmlPullParserException
     *             test fails
     */
    private List<Dependency> readPom(File pomFile) throws FileNotFoundException, IOException, XmlPullParserException {
        if (pomFile.exists()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomFile));
            List<Dependency> pomDependencies = model.getDependencies();
            return pomDependencies;
        }
        return new ArrayList<>();

    }

    /**
     * Extracts the plugin's version from the given pom file.
     * @param pomFile
     *            to be used
     * @param artifactId
     *            plugin id
     * @return the plugin version
     * @throws Exception
     *             test fails
     */
    private String getArtifactVersion(File pomFile, String artifactId) throws Exception {
        String version = null;

        List<Dependency> dependencies;
        dependencies = readPom(pomFile);
        // Get plugin version
        Optional<Dependency> matchingObject =
            dependencies.stream().filter(p -> p.getArtifactId().equals(artifactId)).findFirst();

        version = matchingObject.get().getVersion();
        return version;
    }

    /**
     * Plugin update test. The original pom is replaced with an outdated one that needs to updated. The
     * outdated pom gets updated. The tests checks whether the updating process was successful by comparing
     * the versions of the updated plugins.
     * @throws Exception
     *             test fails
     */
    @Test
    public void pluginUpdateTest() throws Exception {
        String pluginId = "tsplugin";
        setMavenDependencyVersion(originalPom, pluginId, "1.0.0");
        String oldVersion = getArtifactVersion(originalPom, pluginId);
        assertNotNull(oldVersion);

        String args[] = new String[2];
        args[0] = "update";
        args[1] = "--all";
        execute(args, false);

        File updatedPom = currentHome.resolve(CobiGenUtils.CLI_HOME).resolve(MavenConstants.POM).toFile();
        String newVersion = getArtifactVersion(updatedPom, pluginId);

        assertThat(newVersion).isNotNull();
        assertThat(oldVersion).isNotEqualTo(newVersion);
    }

    /**
     * Write new version to the specific pluginid in maven to pom
     * @param pom
     *            to change
     * @param pluginId
     *            plugin id to change the version for
     * @param versionString
     *            the new version string to write
     * @throws IOException
     *             if it fails
     */
    private void setMavenDependencyVersion(File pom, String pluginId, String versionString) throws IOException {
        Pattern p = Pattern.compile(
            "<artifactId>" + pluginId + "</artifactId>\\s+<version>([0-9a-zA-Z\\-\\.]+)</version>", Pattern.MULTILINE);
        String fileContents = FileUtils.readFileToString(pom, Charset.forName("UTF-8"));
        Matcher m = p.matcher(fileContents);
        if (m.find()) {
            String newFileContents =
                new StringBuilder(fileContents).replace(m.start(1), m.end(1), versionString).toString();
            FileUtils.writeStringToFile(pom, newFileContents, Charset.forName("UTF-8"));
        }
    }
}
