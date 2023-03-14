package com.devonfw.cobigen.gui.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.util.CobiGenPaths;
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

    // Load all the paths of template set from cobigen home folder
    // all templates in template-set-list folder
    File templatesetFolder = new File(CobiGenPaths.getCobiGenHomePath().resolve("template-set-list").toString());
    File[] templatesetFileslist = templatesetFolder.listFiles();
    List<Path> templateSetFiles = new ArrayList<>();
    for (File file : templatesetFileslist) {
      templateSetFiles.add(Paths.get(file.getPath()));
    }

    // List<com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration> templateSets = ArtifactRetriever
    // .retrieveTemplateSetData(templateSetFiles);

    // this.templateSetObservableList.addAll(templateSets);
  }

}
