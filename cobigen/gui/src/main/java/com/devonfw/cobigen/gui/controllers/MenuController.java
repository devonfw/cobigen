package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.Controller;
import com.devonfw.cobigen.gui.TemplateSet;
import com.devonfw.cobigen.gui.services.TemplateSetCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * TODO nneuhaus This type ...
 *
 */
public class MenuController implements Initializable {

  public ObservableList<TemplateSet> templateSetObservableList;

  @FXML
  private Controller controller;

  @FXML
  public Button homeButton;

  @FXML
  public TextField searchBar;

  @FXML
  public Button clearSearchResultsButton;

  @FXML
  public Button goSearch;

  // TODO: Transform to ListView<HBox>
  @FXML
  public ListView<TemplateSet> searchResultsView;

  public MenuController() {

    this.templateSetObservableList = FXCollections.observableArrayList();

    // Add the template sets
    this.templateSetObservableList.addAll(new TemplateSet("Template Set 1"), new TemplateSet("Template Set 2"),
        new TemplateSet("Template Set 3"), new TemplateSet("Template Set 4"));
  }

  public void injectController(Controller controller) {

    this.controller = controller;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    this.searchResultsView.setItems(this.templateSetObservableList);
    this.searchResultsView.setCellFactory(resultsView -> new TemplateSetCell());

    this.homeButton.setOnAction(event -> {
      try {
        this.controller.loadHome(event);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

  }

  /**
   * @param event
   * @throws IOException
   */
  @FXML
  public void search(javafx.event.ActionEvent event) throws IOException {

    this.searchResultsView.getItems().clear();
    // this.searchResultsView.getItems().addAll(getTemplateSetsSearchResults(this.searchBar.getText(),
    // this.templateSets));
  }

  /**
   * Called when clearSearchResultsButton is pressed
   */
  @FXML
  public void clearSearchResults() {

    this.searchBar.clear();
    this.searchResultsView.getItems().clear();
    // this.searchResultsView.getItems().addAll(this.templateSets);
  }

}
