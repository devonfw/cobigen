package com.devonfw.cobigen.gui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.testfx.api.FxRobotException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Tests for the home page
 *
 */
public class HomePageTest extends TestFXBase {

  @FXML
  private Hyperlink wikilink;

  @FXML
  private TreeView incrementsTreeView;

  /**
   * Test if exception is thrown, when the bot tries to click a not existing element
   */
  @Test(expected = FxRobotException.class)
  public void clickOnBogusElement() {

    clickOn("#NotExisting");
  }

  /**
   * Test if home page is shown when GUI is started
   */
  @Test
  public void ensureHomePageIsShownOnStartUp() {

    assertThat(this.home);
  }

  /**
   * Test if home page is shown when Home Button gets clicked
   */
  @Test
  public void ensureHomePageIsShownOnHomeButtonClicked() {

    this.searchResultsView.getSelectionModel().select(0);
    assertThat(this.searchResultsView.getSelectionModel().getSelectedIndex() == 0);
    assertThat(this.details.isVisible());
    assertThat(!this.home.isVisible());
    // Switch back to Home
    clickOn("#homeButton");
    assertThat(this.home.isVisible());
    assertThat(!this.details.isVisible());
  }

  /**
   * Test if link to Cobigen Wiki works
   */
  @Test
  public void testLinkToCobigenWiki() {

    String COBIGEN_WIKI_LINK = "#wikilink";
    this.wikilink = find(COBIGEN_WIKI_LINK);
    assertThat(!this.wikilink.isPressed());
    clickOn(COBIGEN_WIKI_LINK);
    assertThat(this.wikilink.isPressed());
  }

  /**
   * Test if the example tree view is interactive
   */
  @Test
  public void testFoldingIncrementTreeView() {

    String TREEVIEW = "#treeView";
    this.incrementsTreeView = (TreeView) this.mainRoot.lookup(TREEVIEW);
    TreeItem<String> root = this.incrementsTreeView.getRoot();
    assertThat(root.isExpanded());
    ObservableList<TreeItem<String>> children = root.getChildren();
    for (int i = 0; i < children.size(); i++) {
      TreeItem<String> child = children.get(i);
      assertThat(!child.isExpanded());
      assertThat(child.getChildren().get(0).isLeaf());
    }
  }

}
