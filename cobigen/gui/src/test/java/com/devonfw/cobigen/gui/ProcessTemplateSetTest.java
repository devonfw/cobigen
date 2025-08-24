package com.devonfw.cobigen.gui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;

import javafx.scene.control.Button;

/**
 * TODO
 *
 */
public class ProcessTemplateSetTest extends TestFXBase {

  /**
   * Root path to all resources used in this test case
   */
  private final static Path TEST_FILE_ROOT_PATH = Paths
      .get("src/test/resources/testdata/integrationtests/ProcessTemplateSetTest");

  @Test
  public void testGetAllTemplateSetsAdapted() throws Exception {

    // TODO:
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER, ConfigurationConstants.ADAPTED_FOLDER);

  }

  /**
   * Tests if a selected template set can be installed (template-set class jar file gets added to
   * template-sets/downloaded folder) and the text of the install button changed from Install to Installed
   *
   * @throws Exception Test fails
   */
  @Test
  public void testInstallTemplateSet() throws Exception {

    // preparation
    File downloaded = this.tmpFolder.newFolder("UserHome", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);

    // simulate template-set-list folder for downloaded template-set.xml files to be used in GUI
    File artifactCacheFolder = this.tmpFolder.newFolder("UserHome", "template-sets", "template-set-list");

    Path templateSetXmlFile1 = TEST_FILE_ROOT_PATH.resolve("crud-java-server-app-2021.12.007-template-set.xml");
    Files.copy(templateSetXmlFile1,
        artifactCacheFolder.toPath().resolve("crud-java-server-app-2021.12.007-template-set.xml"),
        StandardCopyOption.REPLACE_EXISTING);
    Path templateSetXmlFile2 = TEST_FILE_ROOT_PATH.resolve("crud-openapi-server-app-2021.12.007-template-set.xml");
    Files.copy(templateSetXmlFile2,
        artifactCacheFolder.toPath().resolve("crud-openapi-server-app-2021.12.007-template-set.xml"),
        StandardCopyOption.REPLACE_EXISTING);

    Button refreshButton = find("#refreshButton");
    clickOn(refreshButton);

    WaitForAsyncUtils.waitForFxEvents();

    // clicks on first element of searchResultsView
    clickOn(this.searchResultsView.getItems().get(0).getContextConfiguration().getTrigger().get(0).getId());

    Button installButton = find("#installButton");
    String installButtonText = installButton.getText();

    sleep(1000);

    clickOn("Install");

    WaitForAsyncUtils.waitForFxEvents();

    assertThat(downloaded.toPath().resolve("crud-java-server-app-2021.12.007.jar")).exists();

    assertThat(installButtonText).isEqualTo("Installed");

  }

  @Test
  public void testGetAllTemplateSetsDownloaded() throws Exception {

    // preparation
    File userHome = this.tmpFolder.newFolder("UserHome");

    Path templateSetPath = TEST_FILE_ROOT_PATH.resolve("downloaded_template_sets/template-sets");

    FileUtils.copyDirectory(templateSetPath.toFile(), userHome.toPath().resolve("template-sets").toFile());

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {
      Path downloadedPath = CobiGenPaths.getTemplateSetsFolderPath().resolve(ConfigurationConstants.DOWNLOADED_FOLDER);

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
