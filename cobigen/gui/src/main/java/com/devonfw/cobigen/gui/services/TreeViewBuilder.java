package com.devonfw.cobigen.gui.services;

import java.util.List;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Service class to build Tree Views
 *
 */
public class TreeViewBuilder {

  /**
   * @param templateSetIncrements
   * @return
   */
  // TODO: Change type of parameter templateSetIncrements to List<TemplateSetIncrement>
  // Add Description and Explanation?
  public static String[] transformIncrementsToArray(List<String> templateSetIncrements) {

    String[] incrementsWithDescriptions = new String[templateSetIncrements.size() * 2];

    for (int i = 0; i < incrementsWithDescriptions.length; i++) {
      if (i % 2 == 0) {
        // incrementsWithDescriptions[i] = templateSetIncrements.get(i).getName();
      } else {
        // incrementsWithDescriptions[i] = templateSetIncrements.get(i).getDescription();
      }
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
