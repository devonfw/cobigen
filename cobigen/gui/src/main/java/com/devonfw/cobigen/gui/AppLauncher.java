package com.devonfw.cobigen.gui;

/**
 * TODO nneuhaus This type ...
 *
 */
public class AppLauncher {

  public App app;

  public AppLauncher() {

    new Thread() {
      @Override
      public void run() {

        javafx.application.Application.launch(App.class);
      }
    }.start();
    this.app = App.waitForApp();
    this.app.printSomething();
  }

  public static void main(String[] args) {

    new Thread() {
      @Override
      public void run() {

        javafx.application.Application.launch(App.class);
      }
    }.start();
    App app = App.waitForApp();
    app.printSomething();
  }

}
