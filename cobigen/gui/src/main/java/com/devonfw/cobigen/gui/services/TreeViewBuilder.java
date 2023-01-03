package com.devonfw.cobigen.gui.services;

import java.util.List;

import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetIncrement;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Service class to build Tree Views
 *
 */
public class TreeViewBuilder {

  /**
   * Extract template set increments detail
   *
   * @param templateSetIncrements
   * @return the string array of increment descriptions
   */
  public static String[] transformIncrementsToArray(List<TemplateSetIncrement> templateSetIncrements) {

    String[] incrementsWithDescriptions = new String[templateSetIncrements.size() * 2];

    for (int i = 0, j = 0; i < templateSetIncrements.size(); i++) {
      incrementsWithDescriptions[j++] = templateSetIncrements.get(i).getName();

      incrementsWithDescriptions[j++] = templateSetIncrements.get(i).getDescription();
    }
    return incrementsWithDescriptions;
  }

  /**
   * @param arrayOfItems to transform to tree
   * @return the complete TreeView
   */
  public static TreeView<String> buildTreeView(String[] arrayOfItems) {

    TreeItem<String> rootItem = new TreeItem<>("Increments");
    rootItem.setExpanded(true);

    for (int i = 0; i < arrayOfItems.length; i++) {
      TreeItem<String> increment = new TreeItem<>(arrayOfItems[i]);
      i++;
      TreeItem<String> incrementDescription = new TreeItem<>(arrayOfItems[i]);
      increment.getChildren().add(incrementDescription);
      rootItem.getChildren().add(increment);
    }

    TreeView<String> tree = new TreeView<>(rootItem);
    return tree;
  }

}
