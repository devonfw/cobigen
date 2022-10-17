package com.devonfw.cobigen.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Controller for the Template Set Management GUI
 */
public class Controller implements Initializable {

  ArrayList<String> templateSets = new ArrayList<>(Arrays.asList("templates-devon4j-tests", "templates-devon4j-utils",
      "crud-openapi-net", "crud-angular-client-app", "crud-ionic-client-app", "rest-documentation"));

  @FXML
  public Button clearSearchResultsButton;

  @FXML
  public TextField searchBar;

  // TODO: Transform to ListView<HBox>
  @FXML
  public ListView<String> searchResultsView;

  @FXML
  public void search(KeyEvent event) {

    this.searchResultsView.getItems().clear();
    this.searchResultsView.getItems().addAll(searchTemplateSets(this.searchBar.getText(), this.templateSets));
  }

  /**
   * Called when clearSearchResultsButton is pressed
   */
  @FXML
  public void clearSearchResults() {

    this.searchBar.clear();
    this.searchResultsView.getItems().clear();
    this.searchResultsView.getItems().addAll(this.templateSets);
  }

  /**
   * Initial View
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {

    this.searchResultsView.getItems().addAll(this.templateSets);
  }

  /**
   * @param text
   * @param templateSets2
   * @return
   */
  private List<String> searchTemplateSets(String searchWords, List<String> listOfStrings) {

    List<String> searchTemplateSetsArray = Arrays.asList(searchWords.trim().split(" "));

    return listOfStrings.stream().filter(input -> {
      return searchTemplateSetsArray.stream().allMatch(word -> input.toLowerCase().contains(word.toLowerCase()));
    }).collect(Collectors.toList());
  }

}
