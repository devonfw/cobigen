package com.devonfw.cobigen.gui.services;

import com.devonfw.cobigen.gui.model.ModifyableTemplateSet;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TemplateSetCellFactory
    implements Callback<ListView<ModifyableTemplateSet>, ListCell<ModifyableTemplateSet>> {

  @Override
  public ListCell<ModifyableTemplateSet> call(ListView<ModifyableTemplateSet> param) {

    return new TemplateSetCell();
  }

}
