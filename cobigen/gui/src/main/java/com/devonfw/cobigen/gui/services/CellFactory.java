package com.devonfw.cobigen.gui.services;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CellFactory extends Application {
  @Override
  public void start(Stage stage) {

    ObservableList<TemplateSetCell> wordsList = FXCollections.observableArrayList();
    wordsList.add(new TemplateSetCell("First Word", "Definition of First Word"));
    wordsList.add(new TemplateSetCell("Second Word", "Definition of Second Word"));
    wordsList.add(new TemplateSetCell("Third Word", "Definition of Third Word"));
    ListView<TemplateSetCell> listViewOfWords = new ListView<>(wordsList);
    listViewOfWords.setCellFactory(param -> new ListCell<TemplateSetCell>() {
      @Override
      protected void updateItem(TemplateSetCell item, boolean empty) {

        super.updateItem(item, empty);

        if (empty || item == null || item.getWord() == null) {
          setText(null);
        } else {
          setText(item.getWord());
        }
      }
    });
    stage.setScene(new Scene(listViewOfWords));
    stage.show();
  }

  public static class TemplateSetCell extends HBox {

    private final String word;

    private final String definition;

    public TemplateSetCell(String word, String definition) {

      this.word = word;
      this.definition = definition;
    }

    public String getWord() {

      return this.word;
    }

    public String getDefinition() {

      return this.definition;
    }
  }

  public static void main(String[] args) {

    launch(args);
  }
}
