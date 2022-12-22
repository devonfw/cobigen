package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.controllers.DetailsController;
import com.devonfw.cobigen.gui.controllers.HomeController;
import com.devonfw.cobigen.gui.controllers.MenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for the Template Set Management GUI
 */
public class Controller implements Initializable {

  @FXML
  private Parent home;

  @FXML
  private Parent details;

  @FXML
  public MenuController menuController;

  @FXML
  private HomeController homeController;

  @FXML
  public DetailsController detailsController;

  @FXML
  private AnchorPane leftPane;

  @FXML
  private AnchorPane searchPane;

  @FXML
  private AnchorPane rightPane;

  @FXML
  private AnchorPane detailsPane;

  @FXML
  private Pane topPane;

  @FXML
  private Button closeButton;

  @FXML
  private Button minButton;

  @FXML
  private Button minMaxButton;

  private double xOffset = 0;

  private double yOffset = 0;

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

  /**
   * @param event: click on the x Button
   */
  @FXML
  protected void handleCloseAction(ActionEvent event) {

    // get the current stage
    Stage stage = (Stage) this.closeButton.getScene().getWindow();
    // close the window
    stage.close();
  }

  /**
   * @param event : click on the Button to switch window view
   */
  @FXML
  protected void handleMinMaxAction(ActionEvent event) {

    // get the current stage
    Stage stage = (Stage) this.minMaxButton.getScene().getWindow();
    // if window is full screen, make it smaller
    // if it is a small window, make it full screen
    if (stage.isMaximized()) {
      stage.setMaximized(false);
    } else {
      stage.setMaximized(true);
    }

  }

  /**
   * @param event : click on the minimize Button
   */
  @FXML
  protected void handleMinAction(ActionEvent event) {

    // this minimizes the window, can be reopened by clicking in the icon in the task bar
    // TODO: fix animation if possible?
    Stage stage = (Stage) this.minButton.getScene().getWindow();
    stage.setIconified(true);

  }

  @FXML
  protected void handleClickAction(MouseEvent event) {

    Stage stage = (Stage) this.topPane.getScene().getWindow();
    this.xOffset = stage.getX() - event.getScreenX();
    this.yOffset = stage.getY() - event.getScreenY();
  }

  @FXML
  protected void handleMovementAction(MouseEvent event) {

    Stage stage = (Stage) this.topPane.getScene().getWindow();

    stage.setX(event.getScreenX() + this.xOffset);
    stage.setY(event.getScreenY() + this.yOffset);
  }
}
