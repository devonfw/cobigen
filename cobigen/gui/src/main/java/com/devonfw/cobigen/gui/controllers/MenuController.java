package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.Controller;
import com.devonfw.cobigen.gui.model.ModifyableTemplateSet;
import com.devonfw.cobigen.gui.model.TemplateSetModel;
import com.devonfw.cobigen.gui.services.TemplateSetCell;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * TODO nneuhaus This type ...
 *
 */
public class MenuController implements Initializable {

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
  public ListView<ModifyableTemplateSet> searchResultsView;

  private TemplateSetModel tsModel = new TemplateSetModel();

  /**
   * The constructor.
   */
  public MenuController() {

    this.tsModel.loadallAvaliableTemplateSets();
    // List<TemplateSetTag> tagsList = new ArrayList<>();
    // tagsList.addAll(templateSet.getTemplateSetConfiguration().getContextConfiguration().getTags().getTagsList());
  }

  /**
   * Method to get a reference to the main controller
   */
  public void injectController(Controller controller) {

    this.controller = controller;
  }

  /**
   * Initial method when controller gets activated
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // the line sets up the template cells in observable list
    this.searchResultsView.setCellFactory(resultsView -> new TemplateSetCell());

    // home button brings back the previous state
    this.homeButton.setOnAction(event -> {
      try {
        this.controller.loadHome(event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    // binds the List with model
    this.searchResultsView.setItems(this.tsModel.getTemplateSetObservableList());

    // Load increments of selected template set
    this.searchResultsView.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {

        try {
          MenuController.this.controller.loadDetails();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    // Initialize filtered List

    ObservableList<ModifyableTemplateSet> listCopy = this.tsModel.getTemplateSetObservableList();
    FilteredList<ModifyableTemplateSet> filteredData = new FilteredList<>(this.tsModel.getTemplateSetObservableList(),
        b -> true);

    // no idea what it does
    this.searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
      filteredData.setPredicate(templateSets -> {
        // if no search value, then display all records or whatever records it currently has, no changes
        if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
          return true;
        }

        String searchKeyword = newValue.toLowerCase();
        // found a match in the name
        // if (templateSets.getName().toLowerCase().indexOf(searchKeyword) > -1) {
        // return true;
        // }
        // add more if statements of this form
        // if more search relevant attributes are added to the TemplateSet Class!

        // else
        return false;
      });

      this.searchResultsView.setItems(filteredData);

    });

  }

  /**
   * Update list view of template sets and their installation status
   */
  @FXML
  public void refresh() {

  }

  /**
   * Called when clearSearchResultsButton is clicked
   */
  @FXML
  public void clearSearchResults() {

    this.searchBar.clear();

    // TODO: Should we show the Home Page when clearSearchResultsButton is clicked?
    // try {
    // this.controller.loadHome(null);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

}
