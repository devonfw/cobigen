package com.devonfw.cobigen.gui;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

/**
 * TODO nneuhaus This type ...
 *
 */
public class TestFXBase extends ApplicationTest {

  /**
   * @throws Exception
   */
  @SuppressWarnings("javadoc")
  @Before
  public void setUpClass() throws Exception {

    ApplicationTest.launch(App.class);
  }

  @Override
  public void start(Stage stage) throws Exception {

    // TODO Auto-generated method stub

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
