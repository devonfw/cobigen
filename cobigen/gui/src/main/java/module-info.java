module gui {
  requires javafx.controls;

  requires javafx.fxml;

  requires transitive javafx.graphics;

  opens com.devonfw.cobigen.gui to javafx.fxml;

  exports com.devonfw.cobigen.gui;
}