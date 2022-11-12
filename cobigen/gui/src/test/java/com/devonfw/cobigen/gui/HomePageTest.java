package com.devonfw.cobigen.gui;

import org.junit.Test;
import org.testfx.api.FxRobotException;

/**
 * TODO nneuhaus This type ...
 *
 */
public class HomePageTest extends TestFXBase {

  /**
   * Test if exception is thrown, when the bot tries to click a not existing element
   */
  @Test(expected = FxRobotException.class)
  public void clickOnBogusElement() {

    clickOn("#NotExisting");
  }

}
