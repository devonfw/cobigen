package com.devonfw.cobigen.gui;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    try {
      // Create the FXMLLoader
      FXMLLoader loader = new FXMLLoader();
      // Path to the FXML File
      String fxmlDocPath = "C:\\projects\\my-project\\workspaces\\main\\gui\\src\\application\\TemplateSetManagementGui.fxml";
      FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
      // Create the Pane and all Details
      AnchorPane root = (AnchorPane) loader.load(fxmlStream);
      // Create the Scene
      Scene scene = new Scene(root);
      // Set the Scene to the Stage
      primaryStage.setScene(scene);
      // Set the Title to the Stage
      primaryStage.setTitle("A SceneBuilder Example");
      // Display the Stage
      primaryStage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
