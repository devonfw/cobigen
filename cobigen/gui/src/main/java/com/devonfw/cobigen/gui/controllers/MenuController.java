package com.devonfw.cobigen.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.gui.Controller;
import com.devonfw.cobigen.gui.model.TemplateSetModel;
import com.devonfw.cobigen.gui.services.TemplateSetCell;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.retriever.ArtifactRetriever;

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
  public ListView<TemplateSetConfiguration> searchResultsView;

  /**
   * The constructor.
   */
  public MenuController() {

    // Where do we need tags
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

    // the line below sets up the template set cells in observable list
    this.searchResultsView.setCellFactory(resultsView -> new TemplateSetCell());

    this.homeButton.setOnAction(event -> {
      try {
        this.controller.loadHome(event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    Path artifactCachePath = CobiGenPaths.getTemplateSetsFolderPath()
        .resolve(ConfigurationConstants.TEMPLATE_SET_ARTIFACT_CACHE_FOLDER);

    if (!Files.exists(artifactCachePath)) {
      try {
        Files.createDirectory(artifactCachePath);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // Initialize template set artifacts
    // TODO: read CobiGen properties
    List<String> groupIds = Arrays.asList(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    List<URL> urlList = ArtifactRetriever.retrieveTemplateSetXmlDownloadLinks(groupIds,
        MavenUtil.determineMavenSettings());

    List<Path> downloadedArtifacts = ArtifactRetriever.downloadArtifactsFromUrls(urlList);

    List<TemplateSetConfiguration> templateSetConfigurations = ArtifactRetriever
        .retrieveArtifactsFromCache(downloadedArtifacts);

    ObservableList<TemplateSetConfiguration> observableList = FXCollections.observableArrayList();

    observableList.addAll(templateSetConfigurations);

    // binds the List with model
    this.searchResultsView.setItems(observableList);

    // Load increments of selected template set
    // call back functions
    this.searchResultsView.setOnMouseClicked(event -> {

      try {
        MenuController.this.controller.loadDetails();
      } catch (IOException e) {
        e.printStackTrace();
      }

    });

    // Initialize filtered List

    ObservableList<TemplateSetConfiguration> listCopy = TemplateSetModel.getInstance().getTemplateSetObservableList();
    FilteredList<TemplateSetConfiguration> filteredData = new FilteredList<>(
        TemplateSetModel.getInstance().getTemplateSetObservableList(), b -> true);

    // look after the searched text in search bar
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
