package com.devonfw.cobigen.gui;

import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.assertions.api.Assertions;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Tests for the Home Page of GUI
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

    Assertions.assertThat(this.mainRoot.lookup("#homePane").getParent().equals(this.mainRoot.lookup("#detailsPane")));
  }

  /**
   * Test if home page is shown when Home Button gets clicked
   */
  @Test
  public void ensureHomePageIsShownOnHomeButtonClicked() {

    // TODO: Switch to a Template Set before switching back to Home
    String TEMPLATE_SET = "#templateSet";
    clickOn(TEMPLATE_SET);
    String HOME_BUTTON = "#homeButton";
    clickOn(HOME_BUTTON);
    Assertions.assertThat(this.mainRoot.lookup("#homePane").getParent().equals(this.mainRoot.lookup("#detailsPane")));
  }

  /**
   * Test if link to Cobigen Wiki works
   */
  @Test
  public void testLinkToCobigenWiki() {

    String COBIGEN_WIKI_LINK = "#wikilink";
    this.wikilink = (Hyperlink) this.mainRoot.lookup(COBIGEN_WIKI_LINK);
    Assertions.assertThat(!this.wikilink.isPressed());
    clickOn(COBIGEN_WIKI_LINK);
    Assertions.assertThat(this.wikilink.isPressed());
  }

  /**
   * Test if the example tree view is interactive
   */
  @Test
  public void testFoldingIncrementTreeView() {

    String TREEVIEW = "#treeView";
    this.incrementsTreeView = (TreeView) this.mainRoot.lookup(TREEVIEW);
    TreeItem<String> root = this.incrementsTreeView.getRoot();
    Assertions.assertThat(root.isExpanded());
    ObservableList<TreeItem<String>> children = root.getChildren();
    for (int i = 0; i < children.size(); i++) {
      TreeItem<String> child = children.get(i);
      Assertions.assertThat(!child.isExpanded());
      Assertions.assertThat(child.getChildren().get(0).isLeaf());
    }
  }

}
