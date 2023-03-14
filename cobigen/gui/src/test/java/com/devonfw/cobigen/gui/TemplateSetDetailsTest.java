package com.devonfw.cobigen.gui;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;

import javafx.collections.FXCollections;

/**
 * TODO
 *
 */
public class TemplateSetDetailsTest extends TestFXBase {

  /**
   * Root path to all resources used in this test case
   */
  private final static Path TEST_FILE_ROOT_PATH = Paths
      .get("src/test/resources/testdata/unittests/TemplateSetDetailsTest");

  /**
  *
  */
  @Test
  public void testVisibilityOnStartUp() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testVisibilityAfterSearchAndSelect() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
   * Tests if a loaded template set is displaying the correct name in the list view
   */
  @Test
  public void testTemplateSetNameIsShownCorrectly() {

    Path templateSetXmlFile = TEST_FILE_ROOT_PATH.resolve("template-set.xml");

    // initialize template set reader
    TemplateSetConfigurationReader reader = new TemplateSetConfigurationReader();

    // read template set xml file/files
    reader.readConfiguration(templateSetXmlFile);

    TemplateSetConfiguration templateSetConfiguration = reader.getTemplateSetConfiguration();

    // adds template set to GUI
    this.templateSetObservableList = FXCollections.observableArrayList();
    this.templateSetObservableList.addAll(templateSetConfiguration);

    this.searchResultsView.setItems(this.templateSetObservableList);

    String triggerName = templateSetConfiguration.getContextConfiguration().getTrigger().get(0).getId();
    String templateSetNameInMenu = this.searchResultsView.getItems().get(0).getContextConfiguration().getTrigger()
        .get(0).getId();

    assertThat(templateSetNameInMenu).isEqualTo(triggerName);
  }

  /**
  *
  */
  @Test
  public void testTemplateSetDescriptionIsShownCorrectly() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testTemplateSetStatusIsShownCorrectly() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testTemplateSetVersionIsShownCorrectly() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testSelectableVersionsAreShownCorrectly() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testInstallButtonDisabledForInstalledTemplateSet() {

    // TODO
    assertThat(false).isTrue();
  }

  /**
  *
  */
  @Test
  public void testTemplateSetStructureIsShownCorrectly() {

    // TODO
    assertThat(false).isTrue();
  }
}
