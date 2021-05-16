package com.devonfw.cobigen.cli.commandtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.commands.GenerateCommand;
import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;

/**
 * Tests the usage of the update command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class UpdateCommandTest extends AbstractCliTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /** temporary pom file */
    private static String tmpPomFileName = "tmpPom.xml";

    /** outdated pom file */
    private static String outdatedPomFileName = "outdatedPom.xml";

    /** root CLI path */
    private static Path rootCLIPath = null;

    /**
     * Original CobiGen CLI pom file
     */
    private File originalPom = null;

    /**
     * Sets of the correct CLI root path.
     * @throws URISyntaxException
     *             failing
     */
    @Before
    public void setCliPath() throws URISyntaxException {
        if (rootCLIPath == null) {
            rootCLIPath = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParentFile().toPath();
        }
        originalPom = new CobiGenUtils().extractArtificialPom(rootCLIPath);
    }

    /**
     * Replaces the original pom with and outdated one.
     * @throws IOException
     *             test fails
     */
    @Before
    public void replacePom() throws IOException {

        File tmpPom = new File(Paths.get(testFileRootPath, tmpPomFileName).toString());

        // Storing original pom
        FileUtils.copyFile(originalPom, tmpPom);
        // Replacing the original pom with an outdated one
        File outdatedPom = new File(Paths.get(testFileRootPath, outdatedPomFileName).toString());
        FileUtils.copyFile(outdatedPom, originalPom);

    }

    /**
     * Restores the original pom.
     * @throws IOException
     *             test fails
     */
    @After
    public void restorePom() throws IOException {
        File tmpPom = new File(Paths.get(testFileRootPath, tmpPomFileName).toString());

        // Restoring original pom
        FileUtils.copyFile(tmpPom, originalPom);
        FileUtils.deleteQuietly(tmpPom);
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
     * @param artifactId
     *            plugin id
     * @return the plugin version
     */
    private String getArtifactVersion(File pomFile, String artifactId) {
        String version = null;

        List<Dependency> dependencies;
        try {
            dependencies = readPom(pomFile);
            // Get plugin version
            Optional<Dependency> matchingObject =
                dependencies.stream().filter(p -> p.getArtifactId().equals(artifactId)).findFirst();

            version = matchingObject.get().getVersion();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Plugin update test. The original pom is replaced with an outdated one that needs to updated. The
     * outdated pom gets updated. The tests checks whether the updating process was successful by comparing
     * the versions of the updated plugins.
     */
    @Test
    public void pluginUpdateTest() {
        String pluginId = "tsplugin";

        String oldVersion = getArtifactVersion(originalPom, pluginId);

        assertNotNull(oldVersion);

        String args[] = new String[2];
        args[0] = "update";
        args[1] = "--all";
        CobiGenCLI.main(args);

        File updatedPom = new File(Paths.get(rootCLIPath.toString(), MavenConstants.POM).toString());
        String newVersion = getArtifactVersion(updatedPom, pluginId);

        assertThat(newVersion).isNotNull();
        assertThat(oldVersion).isNotEqualTo(newVersion);
    }
}
