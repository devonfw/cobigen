package com.devonfw.cobigen.gui;

import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import com.devonfw.cobigen.gui.controllers.DetailsController;
import com.devonfw.cobigen.gui.controllers.MenuController;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Base Test
 *
 */
public class TestFXBase extends ApplicationTest {

  final String BOGUS = "NOT EXISTING TEMPLATE SET";

  Pane mainRoot;

  Stage mainStage;

  Controller controller;

  Parent home;

  Parent details;

  MenuController menuController;

  DetailsController detailsController;

  ListView<TemplateSet> searchResultsView;

  ObservableList<TemplateSet> templateSetObservableList;

  protected static ResourceBundle bundle;

  /**
   * Set up headless testing
   */
  @BeforeClass
  public static void setupHeadlessMode() {

    if (Boolean.getBoolean("headless")) {
      System.setProperty("testfx.robot", "glass");
      System.setProperty("testfx.headless", "true");
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.text", "t2k");
      System.setProperty("java.awt.headless", "true");
    }

    // bundle = ResourceBundle.getBundle("Bundle");
  }

  /**
   * Start the GUI and set everything up
   */
  @Override
  public void start(Stage stage) throws Exception {

    this.mainStage = stage;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Primary.fxml"));
    this.mainRoot = loader.load();
    this.controller = loader.getController();
    stage.setScene(new Scene(this.mainRoot));
    stage.show();
    stage.toFront();

    this.home = find("#home");
    this.details = find("#details");
    this.menuController = this.controller.menuController;
    this.detailsController = this.controller.detailsController;
    this.searchResultsView = find("#searchResultsView");
    // this.templateSetObservableList = this.menuController.templateSetObservableList;
  }

  /**
   * @throws TimeoutException
   */
  @After
  public void afterEachTest() throws TimeoutException {

    FxToolkit.hideStage();
    release(new KeyCode[] {});
    release(new MouseButton[] {});
  }

  /**
   * Helper method to retrieve Java FX GUI components
   *
   * @param <T>
   * @param query
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T extends Node> T find(final String query) {

    // temporary 'fix' because lookup(#home)... throws error
    return (T) this.mainStage.getScene().lookup(query);

    // return (T) lookup(query).queryAll().iterator().next();
  }

}
