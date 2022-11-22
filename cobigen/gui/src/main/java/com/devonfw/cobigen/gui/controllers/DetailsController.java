package com.devonfw.cobigen.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.services.TreeViewBuilder;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * TODO nneuhaus This type ...
 *
 */
public class DetailsController implements Initializable {

  // TODO: getIncrements()
  private List<String> INCREMENTS = new ArrayList<>();

  @FXML
  AnchorPane treeViewPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    showName();
    showVersion();
    showTreeView(TreeViewBuilder.buildTreeView(TreeViewBuilder.transformIncrementsToArray(this.INCREMENTS)));

  }

  /**
   *
   */
  private void showName() {

    // TODO: getName()

  }

  /**
   *
   */
  private void showVersion() {

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

    // TODO
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void updateTemplateSet(javafx.event.ActionEvent actionEvent) {

    // TODO
  }

  /**
   * @param actionEvent
   */
  @FXML
  public void uninstallTemplateSet(javafx.event.ActionEvent actionEvent) {

    // TODO
  }

}
