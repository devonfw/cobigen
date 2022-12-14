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
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * TODO nneuhaus This type ...
 *
 */
public class HomeController implements Initializable {

  @FXML
  AnchorPane treeViewPane;

  @FXML
  MenuButton settings;

  @FXML
  MenuItem light;

  @FXML
  MenuItem dark;

  private String[] EXAMPLE_LIST = { "Title of Increment 1", "Description of Increment 1", "Title of Increment 2",
  "Description of Increment 2", "Title of Increment 3", "Description of Increment 3" };

  // TODO: get path dynamically from file name
  private String lightTheme = "file:/C:/projects/my-project/workspaces/main/cobigen/cobigen/gui/eclipse-target/classes/com/devonfw/cobigen/gui/styles.css";

  private String darkTheme = "file:/C:/projects/my-project/workspaces/main/cobigen/cobigen/gui/eclipse-target/classes/com/devonfw/cobigen/gui/dark_theme.css";

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

  public void changeToDark(javafx.event.ActionEvent actionEvent) throws IOException {

    Scene scene = this.treeViewPane.getScene();
    scene.getStylesheets().remove(this.lightTheme);
    if (!scene.getStylesheets().contains(this.darkTheme)) {
      scene.getStylesheets().add(this.darkTheme);
    }

  }

  public void changeToLight(javafx.event.ActionEvent actionEvent) throws IOException {

    Scene scene = this.treeViewPane.getScene();
    scene.getStylesheets().remove(this.darkTheme);
    if (!scene.getStylesheets().contains(this.lightTheme)) {
      scene.getStylesheets().add(this.lightTheme);
    }

  }
}
