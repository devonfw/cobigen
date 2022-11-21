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
   * @param listOfItems list to
   * @return
   */
  public static TreeView<String> buildTreeView(String[] listOfItems) {

    TreeItem<String> rootItem = new TreeItem<>("Increments");
    rootItem.setExpanded(true);

    for (int i = 0; i < listOfItems.length; i++) {
      TreeItem<String> increment = new TreeItem<>(listOfItems[i]);
      i++;
      TreeItem<String> incrementDescription = new TreeItem<>(listOfItems[i]);
      increment.getChildren().add(incrementDescription);
      rootItem.getChildren().add(increment);
    }

    TreeView<String> tree = new TreeView<>(rootItem);
    return tree;
  }

}
