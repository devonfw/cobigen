package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * TODO nneuhaus This type ...
 *
 */
public class App extends Application {

  /**
   * latch for waiting for the app
   */
  public static final CountDownLatch latch = new CountDownLatch(1);

  /**
   * The app itself
   */
  public static App app = null;

  /**
   * The scene to set in the window
   */
  private static Scene scene;

  /**
   * The window to show in the app
   */
  public Stage window;

  /**
   * @return the app when it is ready
   */
  public static App waitForApp() {

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return app;
  }

  /**
   * Sets the given app as the general app
   *
   * @param app0 gets set as the app
   */
  public static void setApp(App app0) {

    app = app0;
    latch.countDown();
  }

  /**
   * The constructor.
   */
  public App() {

    setApp(this);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    this.window = new Stage();
    Parent root = FXMLLoader.load(getClass().getResource("fxml/Primary.fxml"));

    System.out.println(1);
    App.scene = new Scene(root);
    App.scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

    Image image = new Image(App.class.getResource("icons/devon-icon.jpg").toExternalForm());
    this.window.setTitle("Template Set Manager");
    this.window.getIcons().add(image);
    this.window.initStyle(StageStyle.TRANSPARENT);
    this.window.setResizable(true);

    this.window.setScene(App.scene);
    this.window.showAndWait();
  }

}
