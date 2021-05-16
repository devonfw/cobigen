package com.devonfw.cobigen.api;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.ConfigurationUtil;

/**
 *
 */
public class ConfigurationUtilTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testFindTemplatesLocation() throws Exception {
        restoreSystemProperties(() -> {
            File userHome = tmpFolder.newFolder("user-home");
            System.setProperty("user.home", userHome.getAbsolutePath());
            Path defaultCobigenHome = userHome.toPath().resolve(ConfigurationConstants.DEFAULT_HOME_DIR_NAME);
            Path templatesFolder = defaultCobigenHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
            Files.createDirectories(templatesFolder);
            String templatesArtifact = "templates-devon4j-1.0.jar";

            // no configuration file exists
            // no templates found
            assertThat(ConfigurationUtil.findTemplatesLocation()).isNull();
            Path templatesProject = templatesFolder.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
            Files.createDirectories(templatesProject);
            Path templatesJar = templatesFolder.resolve(templatesArtifact);
            Files.createFile(templatesJar);
            // found CobiGen_Templates project
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templatesProject.toFile().toURI());
            Files.delete(templatesProject);
            // found templates artifact
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templatesJar.toFile().toURI());

            // configuration file exists
            File randomDirectoryForConfigFile = tmpFolder.newFolder();
            File randomDirectoryForTemplates = tmpFolder.newFolder();
            File configFile = new File(randomDirectoryForConfigFile, ConfigurationConstants.COBIGEN_CONFIG_FILE);
            File templates = new File(randomDirectoryForTemplates, templatesArtifact);
            configFile.createNewFile();
            templates.createNewFile();

            String templatesLocation = templates.getAbsolutePath().replace("\\", "\\\\");
            FileUtils.writeStringToFile(configFile,
                ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH + "=" + templatesLocation);

            withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME,
                randomDirectoryForConfigFile.getAbsolutePath()).execute(
                    () -> {
                        // configuration file found from environment variable
                        assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templates.toURI());
                    });

            Path configFileInCobigenHome = defaultCobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
            FileUtils.copyFile(configFile, configFileInCobigenHome.toFile());
            // configuration file found in cobigen home directory
            assertThat(ConfigurationUtil.findTemplatesLocation()).isEqualTo(templates.toURI());
        });
    }
}
