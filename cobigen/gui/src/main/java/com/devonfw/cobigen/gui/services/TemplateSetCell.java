package com.devonfw.cobigen.gui.services;

import java.io.IOException;

import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TemplateSetCell extends ListCell<TemplateSet> {

  FXMLLoader loader;

  @FXML
  private GridPane gridPane;

  @FXML
  private Label titleLabel;

  @FXML
  private Button installButton;

  // when this method suppose to be called
  @Override
  protected void updateItem(TemplateSet templateSet, boolean empty) {

    super.updateItem(templateSet, empty);
    if (empty || templateSet == null) {
      setText("");
      setGraphic(null);
      setPrefHeight(45.0);
    } else {
      if (this.loader == null) {
        this.loader = new FXMLLoader(
            getClass().getClassLoader().getResource("com/devonfw/cobigen/gui/fxml/TemplateSetCell.fxml"));
        this.loader.setController(this);

        try {
          this.loader.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // name of template set. should not happen here
      this.titleLabel
          .setText(templateSet.getTemplateSetConfiguration().getContextConfiguration().getTriggers().getId());
      this.installButton.setOnAction(event -> {
        System.out.println("INSTALLIEREN!!!");
      });

      setText(null);
      setGraphic(this.gridPane);
    }
  }

}
