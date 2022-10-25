package com.devonfw.cobigen.unittest.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

/**
 * mdukhan Class to test the method loadTemplateSetConfigurations in ConfigurationFinder
 *
 */
public class ConfigurationFinderTest {

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if invalid properties found, to load the default
   * values.
   */
  @Test
  @Ignore
  public void emptyConfigurationTest() {

    Path emptyConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/emptyConfigProperties/config.properties");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(emptyConfiguration);

    assertThat(conf.getConfigurationProperties().getGroupIds())
        .contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getConfigurationProperties().getHideTemplates()).isEmpty();
    assertThat(conf.getConfigurationProperties().isAllowSnapshots()).isFalse();
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   */
  @Test
  @Ignore
  public void validConfigurationTest() {

    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/validConfigProperties/config.properties");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(validConfiguration);

    assertThat(conf.getConfigurationProperties().getGroupIds()).containsSequence("devonfw-cobigen-bla", "abcd",
        "blablob", ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getConfigurationProperties().isAllowSnapshots()).isTrue();
    assertThat(conf.getConfigurationProperties().getHideTemplates().get(0).getArtifactId().equals("com.devonfw"));
    assertThat(conf.getConfigurationProperties().getHideTemplates().get(0).getGroupId().equals("test-artifact"));
    assertThat(conf.getConfigurationProperties().getHideTemplates().get(0).getVersion().equals("3.2.1-SNAPSHOT"));
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   */
  @Test
  @Ignore
  public void invalidInputConfigurationTest() {

    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/invalidConfigProperties/config.properties");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(validConfiguration);

    assertTrue(conf.getConfigurationProperties().getHideTemplates().isEmpty());
    assertTrue(conf.getMavenCoordinates().isEmpty());
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if file *.properties not found , to load the
   * default values.
   *
   */
  @Test
  @Ignore
  public void invalidPathTest() {

    Path invalidPath = Paths.get("path/which/does/not/exist");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(invalidPath);

    assertThat(conf.getConfigurationProperties().getGroupIds())
        .contains(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    assertThat(conf.getConfigurationProperties().getHideTemplates()).isEmpty();
    assertThat(conf.getConfigurationProperties().isAllowSnapshots()).isFalse();
  }
}
