package com.devonfw.cobigen.gui.services;

import com.devonfw.cobigen.gui.TemplateSet;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TemplateSetCellFactory implements Callback<ListView<TemplateSet>, ListCell<TemplateSet>> {

  @Override
  public ListCell<TemplateSet> call(ListView<TemplateSet> param) {

    return new TemplateSetCell();
  }

}
