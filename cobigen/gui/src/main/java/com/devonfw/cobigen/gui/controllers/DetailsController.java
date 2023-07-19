package com.devonfw.cobigen.gui.controllers;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.retriever.ArtifactRetriever;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * TODO nneuhaus This type ...
 *
 */
public class DetailsController implements Initializable {

  private static final Logger LOG = LoggerFactory.getLogger(ArtifactRetriever.class);

  // TODO: getIncrements()
  private List<String> INCREMENTS = new ArrayList<>();

  private TemplateSetConfiguration templateSet;

  @FXML
  Label titleLabel;

  @FXML
  Text descriptionText;

  @FXML
  Button installButton;

  @FXML
  AnchorPane treeViewPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // increment section of the gui
    showVersion();
    // showTreeView(TreeViewBuilder.buildTreeView(TreeViewBuilder.transformIncrementsToArray(this.INCREMENTS)));

  }

  /**
   * Sets text of install button to Installed
   */
  public void updateTemplateSetInstallStatus() {

    Path templateSetPath = retrieveJarPathFromTemplateSet();
    if (Files.exists(templateSetPath)) {
      this.installButton.setText("Installed");
    } else {
      this.installButton.setText("Install");
    }

  }

  /**
   * returns an instance of selected template Set to be operated on
   *
   * @return templateSet
   */
  public TemplateSetConfiguration getTemplateSet() {

    return this.templateSet;
  }

  /**
   * Set the instance of selected template Set
   *
   * @param templateSet new value of {@link #gettemplateSet}.
   */
  public void setTemplateSet(TemplateSetConfiguration templateSet) {

    this.templateSet = templateSet;
  }

  /**
   *
   */
  public void showName(String name) {

    this.titleLabel.setText(name);

  }

  /**
   *
   */
  private void showVersion() {

    // is it the version used in templateSet or the snapshot version?
    // TODO: getVersion()

  }

  /**
   * @param treeView
   */
  public void showTreeView(TreeView<String> treeView) {

    treeView.setId("treeView");
    AnchorPane.setTopAnchor(treeView, 0.0);
    AnchorPane.setRightAnchor(treeView, 0.0);
    AnchorPane.setBottomAnchor(treeView, 0.0);
    AnchorPane.setLeftAnchor(treeView, 0.0);
    this.treeViewPane.getChildren().add(treeView);
  }

  /**
   * Retrieves the jar file path from the selected template set
   *
   * @return Path to template set jar
   */
  private Path retrieveJarPathFromTemplateSet() {

    // Retrieve template set name information from trigger
    String triggerName = this.templateSet.getContextConfiguration().getTrigger().get(0).getId();
    String mavenArtfifactId = triggerName.replace("_", "-");
    String templateSetVersion = this.templateSet.getVersion().toString();
    Path templateSetsPath = CobiGenPaths.getTemplateSetsFolderPath();

    // Adjust file name
    String FileName = mavenArtfifactId + "-" + templateSetVersion + ".jar";
    Path jarFilePath = templateSetsPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER).resolve(FileName);

    return jarFilePath;
  }

  /**
   * Installs a template set into the template-sets/downloaded folder
   *
   * @param actionEvent the action event
   */
  @FXML
  public void installTemplateSet(javafx.event.ActionEvent actionEvent) {

    // Retrieve template set name information from trigger
    String triggerName = this.templateSet.getContextConfiguration().getTrigger().get(0).getId();
    String mavenArtfifactId = triggerName.replace("_", "-");
    String templateSetVersion = this.templateSet.getVersion().toString();

    // Adjust file name
    String FileName = mavenArtfifactId + "-" + templateSetVersion + ".jar";

    Path templateSetsPath = CobiGenPaths.getTemplateSetsFolderPath();

    // prepare MavenCoordinate list for download
    MavenCoordinate mavenCoordinate = new MavenCoordinate(
        ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID, mavenArtfifactId, templateSetVersion);
    List<MavenCoordinate> mavenCoordinateList = new ArrayList<>();
    mavenCoordinateList.add(mavenCoordinate);

    Path expectedJarFilePath = templateSetsPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER).resolve(FileName);

    if (!Files.exists(expectedJarFilePath)) {
      // Download template set class file into downloaded folder
      TemplatesJarUtil.downloadTemplatesByMavenCoordinates(
          templateSetsPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER), mavenCoordinateList);
      // TODO: update installButton title to Installed and save this state
      updateTemplateSetInstallStatus();
    } else {
      // Alert window if the file is already installed
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Confirmation");
      alert.setHeaderText("The selected template-set is already installed!");
      alert.show();
    }
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void updateTemplateSet(javafx.event.ActionEvent actionEvent) {

    // what model is previous and whats the new one
    // new version rein packen

    // TODO
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void uninstallTemplateSet(javafx.event.ActionEvent actionEvent) {

    // what is referred to by the word uninstall.
    // Delete template set from user ordenr

    // TODO
  }

}
