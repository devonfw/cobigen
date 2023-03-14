package com.devonfw.cobigen.gui.services;

import java.util.List;

import com.devonfw.cobigen.impl.config.entity.io.Increment;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Service class to build Tree Views
 *
 */
public class TreeViewBuilder {

  /**
   * @param arrayOfItems to transform to tree
   * @return the complete TreeView
   */
  public static TreeView<String> buildTreeView(List<Increment> arrayOfItems) {

    TreeItem<String> rootItem = new TreeItem<>("Increments");
    rootItem.setExpanded(true);

    for (Increment i : arrayOfItems) {
      TreeItem<String> increment = new TreeItem<>(i.getName());
      TreeItem<String> incrementDescription = new TreeItem<>(i.getDescription());
      increment.getChildren().add(incrementDescription);
      rootItem.getChildren().add(increment);
    }

    TreeView<String> tree = new TreeView<>(rootItem);
    return tree;
  }

}
