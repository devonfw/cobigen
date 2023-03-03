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
import com.devonfw.cobigen.retriever.reader.TemplateSetArtifactReader;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.FXCollections;
import javafx.scene.text.Text;

/**
 * TODO
 *
 */
public class ProcessTemplateSetTest extends TestFXBase {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/integrationtests/ProcessTemplateSetTest";

  @Test
  public void testGetAllTemplateSetsAdapted() throws Exception {

    // TODO:
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    this.tmpFolder.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER, ConfigurationConstants.ADAPTED_FOLDER);

  }

  /**
   * Tests if a selected template set can be installed (template-set class jar file gets added to
   * template-sets/downloaded folder) and UNINSTALLED text changes to INSTALLED
   *
   * @throws Exception Test fails
   */
  @Test
  public void testGetAllTemplateSetsDownloaded() throws Exception {

    // preparation
    File userHome = this.tmpFolder.newFolder("UserHome");
    File downloaded = this.tmpFolder.newFolder("UserHome", ConfigurationConstants.TEMPLATE_SETS_FOLDER,
        ConfigurationConstants.DOWNLOADED_FOLDER);

    // simulate template-set-list folder for downloaded template-set.xml files to be used in GUI
    this.tmpFolder.newFolder("UserHome", "template-set-list");

    Path templateSetXmlFile = Paths.get(testdataRoot).resolve("crud-java-server-app-2021.12.007-template-set.xml");

    TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();

    TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetXmlFile);

    // adds template set to GUI
    this.templateSetObservableList = FXCollections.observableArrayList();
    this.templateSetObservableList.addAll(templateSet);

    this.searchResultsView.setItems(this.templateSetObservableList);

    Text installStatustext = find("#installStatusText");

    sleep(1000);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, userHome.getAbsolutePath()).execute(() -> {

      // clicks on first element of searchResultsView
      clickOn(this.searchResultsView.getItems().get(0).getTemplateSetConfiguration().getContextConfiguration()
          .getTriggers().getId());

      sleep(1000);

      clickOn("Install");

      WaitForAsyncUtils.waitForFxEvents();

      assertThat(downloaded.toPath().resolve("crud-java-server-app-2021.12.007.jar")).exists();

      assertThat(installStatustext.getText()).isEqualTo("INSTALLED");
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
