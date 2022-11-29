package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.devonfw.cobigen.gui.controllers.HomeController;
import com.devonfw.cobigen.gui.controllers.MenuController;
import com.devonfw.cobigen.gui.services.TemplateSetCell;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for the Template Set Management GUI
 */
public class Controller implements Initializable {

  @FXML
  private HomeController homeController;

  @FXML
  private MenuController menuController;

  // ArrayList<String> templateSets = new ArrayList<>(Arrays.asList("templates-devon4j-tests",
  // "templates-devon4j-utils",
  // "crud-openapi-net", "crud-angular-client-app", "crud-ionic-client-app", "rest-documentation"));

  // ArrayList<TemplateSetCell> templateSets = CellFactory.getTestList();

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

    try {
      loadHome(null);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.menuController.injectController(this);
  }

  /**
   * @param actionEvent
   * @throws IOException
   */
  @FXML
  public void loadHome(javafx.event.ActionEvent actionEvent) throws IOException {

    this.detailsPane = FXMLLoader.load(getClass().getResource("fxml/Home.fxml"));
    this.rightPane.getChildren().setAll(this.detailsPane);
  }

  /**
   * @param actionEvent
   * @throws IOException
   */
  public void loadDetails(javafx.event.ActionEvent actionEvent) throws IOException {

    this.detailsPane = FXMLLoader.load(getClass().getResource("fxml/TemplateSetDetails.fxml"));
    this.rightPane.getChildren().setAll(this.detailsPane);
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

  private List<TemplateSetCell> getTemplateSetsSearchResults(String searchWords,
      List<TemplateSetCell> listOfTemplateSets) {

    List<String> namesOfTemplateCells = new ArrayList<>();

    for (TemplateSetCell ts : listOfTemplateSets) {
      // namesOfTemplateCells.add(ts.getWord());
    }

    List<String> resultNames = searchTemplateSets(searchWords, namesOfTemplateCells);
    List<TemplateSetCell> resultCells = new ArrayList<>();

    for (TemplateSetCell ts : listOfTemplateSets) {
      // if (resultNames.contains(ts.getWord())) {
      // resultCells.add(ts);
      // }
    }
    return resultCells;
  }
}
