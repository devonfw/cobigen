package com.devonfw.cobigen.gui.services;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class CellFactory {
  public static ListView<TemplateSetCell> getListView() {

    ObservableList<TemplateSetCell> wordsList = FXCollections.observableArrayList();
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

    return listViewOfWords;
  }

  public static ArrayList<TemplateSetCell> getTestList() {

    ArrayList<TemplateSetCell> testList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      testList.add(createTemplateSetCell("TS" + i, "DF" + i));
    }
    return testList;
  }

  public static TemplateSetCell createTemplateSetCell(String word, String definition) {

    return new TemplateSetCell(word, definition);
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

}
