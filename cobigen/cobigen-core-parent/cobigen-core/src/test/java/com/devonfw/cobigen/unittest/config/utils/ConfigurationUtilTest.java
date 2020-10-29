package com.devonfw.cobigen.unittest.config.utils;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.util.ConfigurationUtil;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 *
 */
public class ConfigurationUtilTest extends AbstractUnitTest {
    @Rule
    public TemporaryFolder userhome = new TemporaryFolder();

    @Test
    public void testFindTemplatesLocation() throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty("user.home", userhome.getRoot().getAbsolutePath());
            File cobigenHome = userhome.newFolder(ConfigurationConstants.COBIGEN_HOME_FOLDER);
            File templatesFolder =
                userhome.newFolder(ConfigurationConstants.COBIGEN_HOME_FOLDER, ConfigurationConstants.TEMPLATES_FOLDER);
            String templatesArtifact = "templates-devon4j-1.0.jar";

            // no configuration file exists
            // no templates found
            assertThat(ConfigurationUtil.findTemplatesLocation()).isNull();
            File templatesProject = new File(templatesFolder, ConfigurationConstants.COBIGEN_TEMPLATES);
            File templatesJar = new File(templatesFolder, templatesArtifact);
            templatesProject.createNewFile();
            templatesJar.createNewFile();
            // found CobiGen_Templates project
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templatesProject.toURI());
            templatesProject.delete();
            // found templates artifact
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templatesJar.toURI());

            // configuration file exists
            File randomDirectoryForConfigFile = userhome.newFolder();
            File randomDirectoryForTemplates = userhome.newFolder();
            File configFile = new File(randomDirectoryForConfigFile, ConfigurationConstants.COBIGEN_CONFIG_FILE);
            File templates = new File(randomDirectoryForTemplates, templatesArtifact);
            configFile.createNewFile();
            templates.createNewFile();

            String templatesLocation = templates.getAbsolutePath().replace("\\", "\\\\");
            FileUtils.writeStringToFile(configFile,
                ConfigurationConstants.COBIGEN_CONFIG_TEMPLATES_LOCATION_KEY + "=" + templatesLocation);

            withEnvironmentVariable(ConfigurationConstants.COBIGEN_CONFIG_DIR,
                randomDirectoryForConfigFile.getAbsolutePath()).execute(
                    () -> {
                        // configuration file found from environment variable
                        assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templates.toURI());
                    });

            File configFileInCobigenHome = new File(cobigenHome, ConfigurationConstants.COBIGEN_CONFIG_FILE);
            FileUtils.copyFile(configFile, configFileInCobigenHome);
            // configuration file found in cobigen home directory
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templates.toURI());
        });
    }
}
