package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * Tests the usage of the adapt-templates command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true to be
 * present among JVM startup arguments.
 */
public class AdaptTemplatesCommandTest extends AbstractCliTest {

  /**
   * Checks if adapt-templates command successfully created cobigen templates folder
   *
   * @throws Exception test fails
   */
  @Test
  public void adaptTemplatesTest() throws Exception {

    String args[] = new String[1];
    args[0] = "adapt-templates";

    execute(args, false);

    Path cobigenTemplatesFolderPath = this.currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
    assertThat(cobigenTemplatesFolderPath).exists();
  }
}
