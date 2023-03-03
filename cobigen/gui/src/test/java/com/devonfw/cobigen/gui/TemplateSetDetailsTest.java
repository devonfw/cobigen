package com.devonfw.cobigen.gui;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.retriever.reader.TemplateSetArtifactReader;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;

import javafx.collections.FXCollections;

/**
 * TODO
 *
 */
public class TemplateSetDetailsTest extends TestFXBase {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittests/TemplateSetDetailsTest";

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

    Path templateSetXmlFile = Paths.get(testdataRoot).resolve("template-set.xml");

    // TODO replace with template set reader
    TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();

    TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetXmlFile);

    // adds template set to GUI
    this.templateSetObservableList = FXCollections.observableArrayList();
    this.templateSetObservableList.addAll(templateSet);

    this.searchResultsView.setItems(this.templateSetObservableList);

    String triggerName = templateSet.getTemplateSetConfiguration().getContextConfiguration().getTriggers().getId();
    String templateSetNameInMenu = this.searchResultsView.getItems().get(0).getTemplateSetConfiguration()
        .getContextConfiguration().getTriggers().getId();

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
