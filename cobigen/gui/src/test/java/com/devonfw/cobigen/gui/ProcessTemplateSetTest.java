package com.devonfw.cobigen.gui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * TODO
 *
 */
public class ProcessTemplateSetTest extends TestFXBase {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Test
  public void testGetAllTemplateSetsAdapted() throws Exception {

    // TODO:
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER, ConfigurationConstants.ADAPTED_FOLDER);

  }

  @Test
  public void testGetAllTemplateSetsDownloaded() throws Exception {

    File userHome = this.tmpFolder.newFolder("UserHome");
    File templateSets = this.tmpFolder.newFolder("UserHome", ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    File downloaded = this.tmpFolder.newFolder("UserHome", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);
    // TODO: Create Dummy jar in resources
    File jar = new File("");
    FileUtils.copyFile(jar, downloaded.toPath().resolve("template-test.jar").toFile());

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {

    });
  }

  @Test
  public void testGetAllTemplateSetsInstalled() throws Exception {

    File userHome = this.tmpFolder.newFolder("user-home");

    // List<TemplateSet> templateSets = ArtifactRetriever.retrieveTemplateSetData();

  }

  @Test
  public void testGetAllTemplateSetsInfo() {

  }

  // TODO: WireMock
  // ConfigurationUtilTest
  @Test
  public void testGetAllTemplateSetsFromRepo() {

  }

  /**
  *
  */
  @Test
  public void testAllTemplateSetsAreShownWithTrueStatus() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testInstallTemplateSetThroughDetails() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testUpdateTemplateSetThroughDetails() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testUpdateTemplateSetToCertainVersion() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testUninstallTemplateSetThroughDetails() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testInstallTemplateSetThroughSearchResultCell() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testUpdateTemplateSetThroughSearchResultCell() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testUninstallTemplateSetThroughSearchResultCell() {

    // TODO
    assertThat(false).isTrue();
  }
}
