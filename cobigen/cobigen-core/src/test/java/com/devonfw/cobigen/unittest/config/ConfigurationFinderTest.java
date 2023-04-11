package com.devonfw.cobigen.unittest.config;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link ConfigurationFinder}
 */
public class ConfigurationFinderTest {

  /**
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Test retrieveCobiGenProperties Method in ConfigurationFinder if invalid properties found, to load the default
   * values.
   *
   * @throws IOException test fails
   */
  @Test
  public void emptyConfigurationTest() throws IOException {

    Path emptyConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/emptyConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.retrieveCobiGenProperties(emptyConfiguration);

    assertThat(conf.getGroupIds()).contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getHideTemplateSets()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }

  /**
   * Test retrieveCobiGenProperties Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   *
   * @throws IOException test fails
   */
  @Test
  public void validConfigurationTest() throws IOException {

    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/validConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.retrieveCobiGenProperties(validConfiguration);

    assertThat(conf.getGroupIds()).containsSequence("devonfw-cobigen-bla", "abcd", "blablob",
        ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.isAllowSnapshots()).isTrue();
    assertThat(conf.getHideTemplateSets().get(0).getArtifactId().equals("com.devonfw"));
    assertThat(conf.getHideTemplateSets().get(0).getGroupId().equals("test-artifact"));
    assertThat(conf.getHideTemplateSets().get(0).getVersion().equals("3.2.1-SNAPSHOT"));
  }

  /**
   * Test retrieveCobiGenProperties Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   *
   * @throws IOException test fails
   */
  @Test
  public void invalidInputConfigurationTest() throws IOException {

    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/invalidConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.retrieveCobiGenProperties(validConfiguration);

    assertTrue(conf.getHideTemplateSets().isEmpty());
    assertTrue(conf.getTemplateSetsInstalled().isEmpty());
  }

  /**
   * Test retrieveCobiGenProperties Method in ConfigurationFinder if file *.properties not found , to load the default
   * values.
   *
   * @throws IOException test fails
   *
   */
  @Test
  public void invalidPathTest() throws IOException {

    Path invalidPath = Paths.get("path/which/does/not/exist");
    ConfigurationProperties conf = ConfigurationFinder.retrieveCobiGenProperties(invalidPath);

    assertThat(conf.getGroupIds()).contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getHideTemplateSets()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }

  /**
   * Test of findTemplates without an existing templates or template-set folder, expecting to return a created
   * template-set folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplatesLocation_WithoutExistingFolders() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templateSets = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templateSets)).exists();
      assertThat(Paths.get(templateSets).getFileName().toString())
          .isEqualTo(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    });
  }

  /**
   * Test of findTemplates with an existing template-set/adapted folder, expecting to return the template-set folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplateSetFolderContainingAdaptedFolder() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.ADAPTED_FOLDER);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI adapted = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(adapted)).exists();
      assertThat(Paths.get(adapted).getFileName().toString()).isEqualTo(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    });
  }

  /**
   * Test of findTemplates with an existing template-set/downloaded folder, expecting to return the template-set folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplateSetFolderContainingDownloadedFolder() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI downloaded = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(downloaded)).exists();
      assertThat(Paths.get(downloaded).getFileName().toString()).isEqualTo(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    });
  }

  /**
   * Test of findTemplates with an existing templates folder with template jars, expecting to return the path to a jar.
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplatesFolderContainingJars() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    File templateS = this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATES_FOLDER);
    Files.createFile(templateS.toPath().resolve("templates-devon4j-1.0-sources.jar"));
    Files.createFile(templateS.toPath().resolve("templates-devon4j-1.0.jar"));
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templates = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templates)).exists();
      assertThat(Paths.get(templates).getFileName().toString()).containsSequence("templates-devon4j-1.0");
    });
  }

  /**
   * Test of findTemplates with an existing templates folder without template jars , expecting to return a newly created
   * template-Set folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplatesFolderWithoutJars() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATES_FOLDER);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templates = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templates)).exists();
      assertThat(Paths.get(templates).getFileName().toString()).isEqualTo(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    });
  }

  /**
   * Test of findTemplates with an existing templates/CobiGen_Templates folder, expecting to return the
   * CobiGen_Templates folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplatesFolderContainingCobiGenTemplatesFolder() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATES_FOLDER,
        ConfigurationConstants.COBIGEN_TEMPLATES);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI CobigenTemplates = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(CobigenTemplates)).exists();
      assertThat(Paths.get(CobigenTemplates).getFileName().toString())
          .isEqualTo(ConfigurationConstants.COBIGEN_TEMPLATES);
    });
  }

  /**
   * Test of findTemplates with an existing template-set/downloaded folder and template-set/adapted folder, expecting to
   * return the template-set folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplateSetFolderContainingDownloadedAndAdaptedFolder() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);
    this.tmpFolder.newFolder("user-home", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.ADAPTED_FOLDER);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI downloaded = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(downloaded)).exists();
      assertThat(Paths.get(downloaded).getFileName().toString()).isEqualTo(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    });
  }

  /**
   * Test of findTemplates with templates value set in the cobigen file, expecting to return the value from the .cobigen
   * file
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplatesPropertySet() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    File customTemplatesFolder = this.tmpFolder.newFolder("customTemplatesFolder",
        ConfigurationConstants.TEMPLATES_FOLDER);
    Path propertieFile = Files.createFile(userHome.toPath().resolve(".cobigen"));
    List<String> properties = Arrays.asList("templates=" + customTemplatesFolder.getAbsolutePath());
    Files.write(propertieFile, properties);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templates = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templates)).exists();
      assertThat(templates).isEqualTo(customTemplatesFolder.toURI());
    });
  }

  /**
   * Test of findTemplates with template-set value set in the cobigen file, the value is faulty and the folder does not
   * exist. Expecting to create a template-set folder in the COBIGEN_HOME home directory.
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithIvalidTemplateSetPropertySet() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    File customTemplatesFolder = this.tmpFolder.newFolder("customTemplatesFolder");
    Path propertieFile = Files.createFile(userHome.toPath().resolve(".cobigen"));
    List<String> properties = Arrays
        .asList("template-sets=" + customTemplatesFolder.getAbsolutePath().replaceFirst("custom", "nonExistend"));
    Files.write(propertieFile, properties);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templates = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templates)).exists();
      assertThat(Paths.get(templates))
          .isEqualTo(userHome.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));
    });
  }

  /**
   * Test of findTemplates with template-set value set in the cobigen file, expecting to return the value from the
   * .cobigen file
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithTemplateSetPropertySet() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    File customTemplatesFolder = this.tmpFolder.newFolder("customTemplatesFolder",
        ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path propertieFile = Files.createFile(userHome.toPath().resolve(".cobigen"));
    List<String> properties = Arrays.asList("template-sets=" + customTemplatesFolder.getAbsolutePath());
    Files.write(propertieFile, properties);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templateSets = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templateSets)).exists();
      assertThat(templateSets).isEqualTo(customTemplatesFolder.toURI());
    });
  }

  /**
   *
   * Test of findTemplates with templates value set in the cobigen file, the value is faulty and the folder does not
   * exist. Expecting to create a template-set folder in the COBIGEN_HOME home directory and return the folder.
   *
   * @throws Exception test fails
   */
  @Test
  public void testFindTemplateLocation_WithIvalidTemplatesPropertySet() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");
    File customTemplatesFolder = this.tmpFolder.newFolder("customTemplatesFolder");
    Path propertieFile = Files.createFile(userHome.toPath().resolve(".cobigen"));
    List<String> properties = Arrays
        .asList("templates=" + customTemplatesFolder.getAbsolutePath().replaceFirst("custom", "nonExistend"));
    Files.write(propertieFile, properties);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      URI templateSets = ConfigurationFinder.findTemplatesLocation();
      assertThat(Paths.get(templateSets)).exists();
      assertThat(Paths.get(templateSets))
          .isEqualTo(userHome.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));
    });
  }

}
