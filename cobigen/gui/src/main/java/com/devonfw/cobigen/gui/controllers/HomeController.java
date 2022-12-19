package com.devonfw.cobigen.gui.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.App;
import com.devonfw.cobigen.gui.services.TreeViewBuilder;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * TODO nneuhaus This type ...
 *
 */
public class HomeController implements Initializable {

  @FXML
  AnchorPane homePane;

  @FXML
  AnchorPane treeViewPane;

  @FXML
  MenuButton settings;

  private String[] EXAMPLE_LIST = { "Title of Increment 1", "Description of Increment 1", "Title of Increment 2",
  "Description of Increment 2", "Title of Increment 3", "Description of Increment 3" };

  private String lightTheme = App.class.getResource("styles.css").toExternalForm();

  private String darkTheme = App.class.getResource("dark_theme.css").toExternalForm();

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // Build the tree view
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

  public void changeTheme(javafx.event.ActionEvent actionEvent) throws IOException {

    Scene scene = this.treeViewPane.getScene();

    if (scene.getStylesheets().contains(this.lightTheme)) {
      scene.getStylesheets().remove(this.lightTheme);
      scene.getStylesheets().add(this.darkTheme);
    } else {
      scene.getStylesheets().remove(this.darkTheme);
      scene.getStylesheets().add(this.lightTheme);
    }
  }
}
