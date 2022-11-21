package com.devonfw.cobigen.gui;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
  public void start(Stage primaryStage) throws IOException {

    this.window = new Stage();
    Parent root = FXMLLoader.load(getClass().getResource("fxml/Primary.fxml"));

    App.scene = new Scene(root);
    App.scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

    Image image = new Image(App.class.getResource("icons/devon-icon.jpg").toExternalForm());
    this.window.setTitle("Template Set Manager");
    this.window.getIcons().add(image);
    this.window.setScene(App.scene);
    this.window.showAndWait();
  }

}
