package com.devonfw.cobigen.unittest.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

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
  public void emptyConfigurationTest() {

    Path emptyConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/emptyConfigProperties/config.properties");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(emptyConfiguration);

    assertThat(conf.getGroupIds()).contains("com.devonfw.cobigen");
    assertThat(conf.getHideTemplates()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if valid properties found, to load these valid
   * properties correctly.
   */
  @Test
  public void validConfigurationTest() {

    Path validConfiguration = Paths
        .get("src/test/resources/testdata/unittest/config/properties/validConfigProperties/config.properties");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(validConfiguration);

    assertThat(conf.getGroupIds()).containsSequence("devonfw-cobigen-bla", "abcd", "blablob", "com.devonfw.cobigen");
    assertThat(conf.isAllowSnapshots()).isTrue();
    assertThat(conf.getHideTemplates()).contains("com.devonfw(:test-artifact(:3.2.1-SNAPSHOT))");
  }

  /**
   * Test loadTemplateSetConfigurations Method in ConfigurationFinder if file *.properties not found , to load the
   * default values.
   *
   */
  @Test
  public void invalidPathTest() {

    Path invalidPath = Paths.get("path/which/does/not/exist");
    TemplateSetConfiguration conf = ConfigurationFinder.loadTemplateSetConfigurations(invalidPath);

    assertThat(conf.getGroupIds()).contains("com.devonfw.cobigen");
    assertThat(conf.getHideTemplates()).isEmpty();
    assertThat(conf.isAllowSnapshots()).isFalse();
  }
}