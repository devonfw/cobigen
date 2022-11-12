/**
 * @author nneuhaus
 *
 */
module gui {
  requires javafx.controls;

  requires javafx.fxml;

  requires transitive javafx.graphics;

  requires junit;

  opens com.devonfw.cobigen.gui to javafx.fxml;

  exports com.devonfw.cobigen.gui;
}