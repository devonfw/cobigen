package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.controllers.DetailsController;
import com.devonfw.cobigen.gui.controllers.HomeController;
import com.devonfw.cobigen.gui.controllers.MenuController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for the Template Set Management GUI
 */
public class Controller implements Initializable {

  @FXML
  private Parent home;

  @FXML
  private Parent details;

  @FXML
  private MenuController menuController;

  @FXML
  private HomeController homeController;

  @FXML
  private DetailsController detailsController;

  @FXML
  private AnchorPane leftPane;

  @FXML
  private AnchorPane searchPane;

  @FXML
  private AnchorPane rightPane;

  @FXML
  private AnchorPane detailsPane;

  /**
   * Initial View
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

    this.menuController.injectController(this);
  }

  /**
   * @param actionEvent
   * @throws IOException
   */
  @FXML
  public void loadHome(javafx.event.ActionEvent actionEvent) throws IOException {

    this.details.setVisible(false);
    this.home.setVisible(true);
    this.menuController.clearSearchResults();
    this.menuController.searchResultsView.getSelectionModel().clearSelection();
  }

  /**
   * @throws IOException
   */
  public void loadDetails() throws IOException {

    // TODO: getIncrements() for Tree View when #1517 is merged
    // Add parameter Increments

    TemplateSet selectedItem = this.menuController.searchResultsView.getSelectionModel().getSelectedItem();

    if (selectedItem == null) {
      this.home.setVisible(true);
      this.details.setVisible(false);
    } else {
      this.home.setVisible(false);
      this.details.setVisible(true);
      this.detailsController.showName(selectedItem.getName());
    }

  }
}
