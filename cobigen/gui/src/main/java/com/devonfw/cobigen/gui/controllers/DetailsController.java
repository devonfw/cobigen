package com.devonfw.cobigen.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
   */
  @FXML
  public void installTemplateSet(javafx.event.ActionEvent actionEvent) {

    // What does it exactly mean by installing and where
    // TODO
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void updateTemplateSet(javafx.event.ActionEvent actionEvent) {

    // what model is previous and whats the new one

    // TODO
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void uninstallTemplateSet(javafx.event.ActionEvent actionEvent) {

    // what is referred to by the word uninstall.

    // TODO
  }

}
