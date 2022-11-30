package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.Controller;
import com.devonfw.cobigen.gui.TemplateSet;
import com.devonfw.cobigen.gui.services.TemplateSetCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

  @FXML
  public ListView<TemplateSet> searchResultsView;

  public MenuController() {

    this.templateSetObservableList = FXCollections.observableArrayList();

    // Add the template sets, populate observable list ( before initialize)
    this.templateSetObservableList.addAll(new TemplateSet("Template Set 1"), new TemplateSet("Template Set 2"),
        new TemplateSet("Template Set 3"), new TemplateSet("Template Set 4"));
  }

  public void injectController(Controller controller) {

    this.controller = controller;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    this.searchResultsView.setCellFactory(resultsView -> new TemplateSetCell());

    this.homeButton.setOnAction(event -> {
      try {
        this.controller.loadHome(event);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    this.searchResultsView.setItems(this.templateSetObservableList);

    // Initialize filtered List
    ObservableList<TemplateSet> listCopy = this.templateSetObservableList;
    FilteredList<TemplateSet> filteredData = new FilteredList<>(this.templateSetObservableList, b -> true);

    this.searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
      filteredData.setPredicate(templateSets -> {
        // if no search value, then display all records or whatever records it currently has, no changes
        if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
          return true;
        }

        String searchKeyword = newValue.toLowerCase();
        // found a match in the name
        if (templateSets.getName().toLowerCase().indexOf(searchKeyword) > -1) {
          return true;
        }
        // add more if statements of this form
        // if more search relevant attributes are added to the TemplateSet Class!

        else
          return false;
      });

      this.searchResultsView.setItems(filteredData);

    });

  }

  /**
   * Called when clearSearchResultsButton is pressed
   */
  @FXML
  public void clearSearchResults() {

    this.searchBar.clear();
  }

}
