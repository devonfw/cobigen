package com.devonfw.cobigen.gui;

import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TestFXBase extends ApplicationTest {

  Pane mainRoot;

  Stage mainStage;

  Controller controller;

  protected static ResourceBundle bundle;

  /**
   *
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
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {

  }

  @Override
  public void start(Stage stage) throws Exception {

    this.mainStage = stage;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Primary.fxml"));
    this.mainRoot = loader.load();
    this.controller = loader.getController();
    stage.setScene(new Scene(this.mainRoot));
    stage.show();
    stage.toFront();
  }

  /**
   * @throws TimeoutException
   */
  @SuppressWarnings("javadoc")
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

    return (T) lookup(query).queryAll().iterator().next();
  }

}
