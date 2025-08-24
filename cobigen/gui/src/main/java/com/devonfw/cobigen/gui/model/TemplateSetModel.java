package com.devonfw.cobigen.gui.model;

import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton Model Class
 *
 */
public class TemplateSetModel {
  private static TemplateSetModel tsModel;

  private final ObservableList<TemplateSetConfiguration> templateSetObservableList;

  private TemplateSetModel() {

    this.templateSetObservableList = FXCollections.observableArrayList();
  }

  public static TemplateSetModel getInstance() {

    if (tsModel == null) {
      tsModel = new TemplateSetModel();
    }

    // returns the singleton object
    return tsModel;
  }

  /**
   * @return templateSetObservableList
   */
  public ObservableList<TemplateSetConfiguration> getTemplateSetObservableList() {

    return this.templateSetObservableList;
  }

  public void loadAllAvailableTemplateSets() {

    // Load all template set artifacts
    // List<TemplateSetConfiguration> templateSetConfigurations = ArtifactRetriever.retrieveArtifactsFromCache();

    // pass TemplateSetConfigurations to GUI
    // for (TemplateSetConfiguration configuration : templateSetConfigurations) {
    // this.templateSetObservableList.addAll(configuration);
    // }

  }

}
