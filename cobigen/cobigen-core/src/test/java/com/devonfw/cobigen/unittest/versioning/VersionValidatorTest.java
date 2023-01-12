package com.devonfw.cobigen.unittest.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.api.exception.DeprecatedMonolithicConfigurationException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;

/**
 * Test suite for {@link VersionValidator} class.
 */
public class VersionValidatorTest {

  /**
   * Testing the CobiGen version to be too old for the provided configuration version, which indicates to make an update
   * of CobiGen to read the configuration properly.
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testInvalidCobiGenVersion_tooOld_contextConfiguration() {

    try {
      VersionValidator validator = new VersionValidator(Type.CONTEXT_CONFIGURATION, "2.0.2");
      validator.validate(2.1f);
    } catch (InvalidConfigurationException e) {
      assertThat(e.getMessage()).matches(".* version '2.1' .* context configuration is unknown .* CobiGen '2.0'.*");
      throw e;
    }
  }

  /**
   * Testing the CobiGen version to be too new for the provided configuration version, which indicates to make an update
   * of the configuration to read it properly.
   */
  @Test(expected = DeprecatedMonolithicConfigurationException.class)
  public void testInvalidCobiGenVersion_tooNew_contextConfiguration() {

    try {
      VersionValidator validator = new VersionValidator(Type.CONTEXT_CONFIGURATION, "2.1.0");
      validator.validate(1.2f, false);
    } catch (DeprecatedMonolithicConfigurationException e) {
      assertThat(e.getMessage()).matches(
          "You are using an old templates configuration. Please consider upgrading your templates! Thank you!");
      throw e;
    }
  }

  /**
   * Testing the CobiGen version with higher version number than configuration. However, there is no breaking change in
   * between.
   */
  @Test
  public void testValidCobiGenVersion_higherButNonBreaking_templatesConfiguration() {

    VersionValidator validator = new VersionValidator(Type.TEMPLATES_CONFIGURATION, "1.5.0");
    validator.validate(1.2f);
  }

}
