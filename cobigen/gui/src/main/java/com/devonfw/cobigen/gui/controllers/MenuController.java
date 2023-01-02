package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.devonfw.cobigen.gui.Controller;
import com.devonfw.cobigen.gui.services.TemplateSetCell;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.FXCollections;
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

  /**
   * The constructor.
   */
  public MenuController() {

    this.templateSetObservableList = FXCollections.observableArrayList();

    // Add the template sets, populate observable list (before initialize)
    // this.templateSetObservableList.addAll(new TemplateSet("Template Set 1"), new TemplateSet("Template Set 2"),
    // new TemplateSet("Template Set 3"), new TemplateSet("Template Set 4"));

    // Path templateSetFile = Paths.get(testdataRoot).resolve("template-set.xml");
    //
    // TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();
    //
    // TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetFile);
    //
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

    this.searchResultsView.setCellFactory(resultsView -> new TemplateSetCell());

    this.homeButton.setOnAction(event -> {
      try {
        this.controller.loadHome(event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    this.searchResultsView.setItems(this.templateSetObservableList);

    // Handle item selection
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
