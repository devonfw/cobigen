package com.devonfw.cobigen.gui.services;

import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TemplateSetCellFactory
    implements Callback<ListView<TemplateSetConfiguration>, ListCell<TemplateSetConfiguration>> {

  @Override
  public ListCell<TemplateSetConfiguration> call(ListView<TemplateSetConfiguration> param) {

    return new TemplateSetCell();
  }

}
