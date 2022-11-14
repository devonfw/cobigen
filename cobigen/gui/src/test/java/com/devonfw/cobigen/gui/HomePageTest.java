package com.devonfw.cobigen.gui;

import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.assertions.api.Assertions;

/**
 * Tests for the Home Page of GUI
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

  /**
   * Test if the home page is shown when GUI is started
   */
  @Test
  public void ensureHomePageIsShownOnStartUp() {

    Assertions.assertThat(this.mainRoot.lookup("#homePane").getParent().equals(this.mainRoot.lookup("#detailsPane")));
  }

  @Test
  public void ensureHomePageIsShownOnHomeButtonClicked() {

    // TODO: Switch to a Template Set before switching back to Home
    String HOME_BUTTON = "#homeButton";
    clickOn(HOME_BUTTON);
    // Assertions.assertThat(lookup("#homePane"));
  }

}
