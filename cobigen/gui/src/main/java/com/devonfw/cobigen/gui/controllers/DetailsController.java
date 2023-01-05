package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

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

  // TODO: getIncrements()
  private List<String> INCREMENTS = new ArrayList<>();

  private TemplateSet templateSet;

  @FXML
  Label titleLabel;

  @FXML
  Text descriptionText;

  @FXML
  Text installStatusText;

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
   * returns an instance of selected template Set to be operated on
   *
   * @return templateSet
   */
  public TemplateSet getTemplateSet() {

    return this.templateSet;
  }

  /**
   * Set the instance of selected template Set
   *
   * @param templateSet new value of {@link #gettemplateSet}.
   */
  public void setTemplateSet(TemplateSet templateSet) {

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
   * @param actionEvent
   * @throws IOException
   */
  @FXML
  public void installTemplateSet(javafx.event.ActionEvent actionEvent) throws IOException {

    // Retrieving trigger information and move the template set file to user folder
    String triggerName = this.templateSet.getTemplateSetConfiguration().getContextConfiguration().getTriggers().getId();
    String FileName = triggerName.replace("_", "-") + "-" + this.templateSet.getTemplateSetVersion()
        + "-template-set.xml";
    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    Path sourceFilePath = cobigenHome.resolve("template-set-list").resolve(FileName);
    String destinationPath = "C:\\Users\\alsaad\\template-set-installed\\" + FileName;
    Path destinationFilePath = Paths.get(destinationPath);
    if (!Files.exists(destinationFilePath)) {
      Files.copy(sourceFilePath, destinationFilePath);
      System.out.println(sourceFilePath.toString());
      System.out.println(destinationFilePath);
    } else {
      // Alert window if the file is already installed
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Confirmation");
      alert.setHeaderText("the selected template-set is already installed");
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
