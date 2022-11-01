package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  public static final CountDownLatch latch = new CountDownLatch(1);

  public static App app = null;

  public Stage window;

  private static Scene scene;

  public static App waitForApp() {

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return app;
  }

  public static void setApp(App app0) {

    app = app0;
    latch.countDown();
  }

  public App() {

    setApp(this);
  }

  public void printSomething() {

    System.out.println("You called a method on the application");
  }

  @Override
  public void start(Stage stage) throws IOException {

    this.window = new Stage();

    scene = new Scene(loadFXML("primary"));
    scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

    this.window.setScene(scene);
    this.window.showAndWait();
    // stage.setScene(scene);
    // stage.show();
  }

  static void setRoot(String fxml) throws IOException {

    scene.setRoot(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {

    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {

    launch();
  }

}
