package com.devonfw.cobigen.gui.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.retriever.ArtifactRetriever;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * TODO alsaad This type ...
 *
 */
public class TemplateSetModel {
  private final ObservableList<ModifyableTemplateSet> templateSetObservableList;

  private List<String> templatesetNames = new ArrayList<>();

  public TemplateSetModel() {

    this.templateSetObservableList = FXCollections.observableArrayList();
  }

  /**
   * @return templateSetObservableList
   */
  public ObservableList<ModifyableTemplateSet> getTemplateSetObservableList() {

    return this.templateSetObservableList;
  }

  public void loadallAvaliableTemplateSets() {

    // Load all the paths of template set from cobigen home folder or where?
    // How to access paths of all template sets and load it here
    /** Test data root path */
    final String testdataRoot = "src/main/resources/com/devonfw/cobigen/gui/TemplateSetArtifactReaderTest";
    List<ModifyableTemplateSet> mTS = new ArrayList<>();

    List<Path> templateSetFiles = new ArrayList<>();
    templateSetFiles.add(Paths.get(testdataRoot).resolve("crud-java-server-app-2021.08.001-template-set.xml"));
    this.templatesetNames.add(templateSetFiles.get(0).getFileName().toString());
    templateSetFiles.add(Paths.get(testdataRoot).resolve("template-set.xml"));
    this.templatesetNames.add(templateSetFiles.get(1).getFileName().toString());

    List<TemplateSet> templateSets = ArtifactRetriever.retrieveTemplateSetData(templateSetFiles);
    int i = 0;
    for (TemplateSet set : templateSets) {
      ModifyableTemplateSet mts = new ModifyableTemplateSet(set.getTemplateSetVersion(),
          set.getTemplateSetConfiguration(), this.templatesetNames.get(i++));
      mTS.add(mts);

    }

    this.templateSetObservableList.addAll(mTS);
  }

}
