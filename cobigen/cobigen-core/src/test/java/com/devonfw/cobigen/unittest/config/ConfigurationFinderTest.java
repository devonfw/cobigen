package com.devonfw.cobigen.unittest.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

/**
 * mdukhan Class to test the method loadTemplateSetConfigurations in ConfigurationFinder
 *
 */
public class ConfigurationFinderTest {

  /**
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if invalid properties found, to load the default
   * values.
   *
   * @throws IOException
   */
  @Test
  @Ignore
  public void emptyConfigurationTest() throws IOException {

    File folder = this.tmpFolder.newFolder("TemplateSetsTest");
    Path emptyConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/emptyConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.loadTemplateSetConfigurations(emptyConfiguration,
        folder.toPath());

    assertThat(conf.getGroupIds()).contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getHideTemplates()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   *
   * @throws IOException
   */
  @Test
  @Ignore
  public void validConfigurationTest() throws IOException {

    File folder = this.tmpFolder.newFolder("TemplateSetsTest1");
    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/validConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.loadTemplateSetConfigurations(validConfiguration,
        folder.toPath());

    assertThat(conf.getGroupIds()).containsSequence("devonfw-cobigen-bla", "abcd", "blablob",
        ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.isAllowSnapshots()).isTrue();
    assertThat(conf.getHideTemplates().get(0).getArtifactId().equals("com.devonfw"));
    assertThat(conf.getHideTemplates().get(0).getGroupId().equals("test-artifact"));
    assertThat(conf.getHideTemplates().get(0).getVersion().equals("3.2.1-SNAPSHOT"));
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   *
   * @throws IOException
   */
  @Test
  @Ignore
  public void invalidInputConfigurationTest() throws IOException {

    File folder = this.tmpFolder.newFolder("TemplateSetsTest2");
    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/invalidConfigProperties/config.properties");
    ConfigurationProperties conf = ConfigurationFinder.loadTemplateSetConfigurations(validConfiguration,
        folder.toPath());

    assertTrue(conf.getHideTemplates().isEmpty());
    assertTrue(conf.getMavenCoordinates().isEmpty());
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if file *.properties not found , to load the
   * default values.
   *
   * @throws IOException
   *
   */
  @Test
  @Ignore
  public void invalidPathTest() throws IOException {

    File folder = this.tmpFolder.newFolder("TemplateSetsTest3");
    Path invalidPath = Paths.get("path/which/does/not/exist");
    ConfigurationProperties conf = ConfigurationFinder.loadTemplateSetConfigurations(invalidPath, folder.toPath());

    assertThat(conf.getGroupIds()).contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getHideTemplates()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }
}
