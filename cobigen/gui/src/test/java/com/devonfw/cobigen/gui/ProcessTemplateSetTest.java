package com.devonfw.cobigen.gui;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.util.WaitForAsyncUtils;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;

/**
 * TODO
 *
 */
public class ProcessTemplateSetTest extends TestFXBase {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

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
    File userHome = this.tmpFolder.newFolder("UserHome");
    File downloaded = this.tmpFolder.newFolder("UserHome", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);

    // simulate template-set-list folder for downloaded template-set.xml files to be used in GUI
    this.tmpFolder.newFolder("UserHome", "template-set-list");

    Path templateSetXmlFile = TEST_FILE_ROOT_PATH.resolve("crud-java-server-app-2021.12.007-template-set.xml");

    // initialize template set reader
    TemplateSetConfigurationReader reader = new TemplateSetConfigurationReader();

    // read template set xml file/files
    reader.readConfiguration(templateSetXmlFile);

    TemplateSetConfiguration templateSetConfiguration = reader.getTemplateSetConfiguration();

    // pass TemplateSetConfiguration to GUI
    this.templateSetObservableList = FXCollections.observableArrayList();
    this.templateSetObservableList.addAll(templateSetConfiguration);

    this.searchResultsView.setItems(this.templateSetObservableList);

    Button installButton = find("#installButton");
    String installButtonText = installButton.getText();

    sleep(1000);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {

      // clicks on first element of searchResultsView
      clickOn(this.searchResultsView.getItems().get(0).getContextConfiguration().getTrigger().get(0).getId());

      sleep(1000);

      clickOn("Install");

      WaitForAsyncUtils.waitForFxEvents();

      assertThat(downloaded.toPath().resolve("crud-java-server-app-2021.12.007.jar")).exists();

      assertThat(installButtonText).isEqualTo("Installed");
    });

  }

  @Test
  public void testGetAllTemplateSetsDownloaded() throws Exception {

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
