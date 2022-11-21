package com.devonfw.cobigen.gui.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
public class HomeController implements Initializable {

  @FXML
  AnchorPane treeViewPane;

  private String[] EXAMPLE_LIST = { "Title of Increment 1", "Description of Increment 1", "Title of Increment 2",
  "Description of Increment 2", "Title of Increment 3", "Description of Increment 3" };

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    TreeView<String> treeView = TreeViewBuilder.buildTreeView(this.EXAMPLE_LIST);
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
   * @throws URISyntaxException
   */
  @FXML
  public void openWiki(javafx.event.ActionEvent actionEvent) throws IOException, URISyntaxException {

    Desktop.getDesktop().browse(new URI("https://github.com/devonfw/cobigen/wiki"));
  }
}
