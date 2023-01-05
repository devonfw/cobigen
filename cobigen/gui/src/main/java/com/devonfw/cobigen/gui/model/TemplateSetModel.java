package com.devonfw.cobigen.gui.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.retriever.ArtifactRetriever;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton Model Class
 *
 */
public class TemplateSetModel {
  private static TemplateSetModel tsModel;

  private final ObservableList<TemplateSet> templateSetObservableList;

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
  public ObservableList<TemplateSet> getTemplateSetObservableList() {

    return this.templateSetObservableList;
  }

  public void loadallAvaliableTemplateSets() {

    // Load all the paths of template set from cobigen home folder
    // all templates in template-set-list folder
    File templatesetFolder = new File(CobiGenPaths.getCobiGenHomePath().resolve("template-set-list").toString());
    File[] templatesetFileslist = templatesetFolder.listFiles();
    List<Path> templateSetFiles = new ArrayList<>();
    for (File file : templatesetFileslist) {
      templateSetFiles.add(Paths.get(file.getPath()));
    }

    List<TemplateSet> templateSets = ArtifactRetriever.retrieveTemplateSetData(templateSetFiles);

    this.templateSetObservableList.addAll(templateSets);
  }

}
