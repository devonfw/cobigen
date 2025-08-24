package com.devonfw.cobigen.gui;

import org.junit.Test;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Tests for the search function
 *
 */
public class SearchTest extends TestFXBase {

  /**
   * Tests if search results list view has the correct list cells
   */
  @Test
  public void hasListCell() {

    // assertThat(this.searchResultsView).hasListCell(this.templateSetObservableList.get(0));
  }

  /**
   * Tests that error is thrown when asked for null list cell
   */
  @Test
  public void hasListCellFailsWithNull() {

    // assertThatThrownBy(() -> assertThat(this.searchResultsView).hasListCell(null))
    // .isExactlyInstanceOf(AssertionError.class);
  }

  /**
   * Tests that error is thrown when asked for bogus list cell
   */
  @Test
  public void hasListCellFailsWithBogus() {

    // assertThatThrownBy(() -> assertThat(this.searchResultsView).hasListCell(this.BOGUS))
    // .isExactlyInstanceOf(AssertionError.class);
  }

  /**
   * Tests that the search results have no bogus list cell
   */
  @Test
  public void doesNotHaveListCell() {

    // assertThat(this.searchResultsView).doesNotHaveListCell(this.BOGUS);
  }

  /**
   * Tests that the amount of elements in the list are shown in the search results
   */
  @Test
  public void hasExactlyNumItems() {

    // assertThat(this.searchResultsView).hasExactlyNumItems(this.templateSetObservableList.size());
  }

  /**
   * Tests that error is thrown when asked for wrong count of items
   */
  @Test
  public void hasExactlyNumItems_fails() {

    // assertThatThrownBy(() -> assertThat(this.searchResultsView).hasExactlyNumItems(1))
    // .isExactlyInstanceOf(AssertionError.class).hasMessage(
    // "Expected: ListView has exactly 1 item\n " + "but: was " + this.templateSetObservableList.size());
  }

  /**
   * Tests that correct items are shown in the search results before the search
   */
  @Test
  public void preSearchTest() {

    // int i = 0;
    // for (TemplateSet ts : this.templateSetObservableList) {
    // clickOn("#searchResultsView");
    // assertThat(this.searchResultsView.getSelectionModel().getSelectedIndex() == i);
    // assertThat(ts.equals(this.searchResultsView.getItems().get(i))).isTrue();
    // i++;
    // }
  }

  /**
   * Tests that each template set is the only search result when searched for its exact name
   */
  @Test
  public void ensureAllTemplateSetsAreFound() {

    String titleOfTemplateSet;
    TextField searchBar = find("#searchBar");
    // for (TemplateSet ts : this.templateSetObservableList) {
    // clickOn("#searchBar");
    // titleOfTemplateSet = ts.getName();
    // eraseText(titleOfTemplateSet.length());
    // write(titleOfTemplateSet);
    // clickOn("#searchResultsView");
    // assertThat(titleOfTemplateSet.equals(this.searchResultsView.getItems().get(0).getName()));
    // assertThat(this.searchResultsView).hasExactlyNumItems(1);
  }

  /**
   * Tests that there are no search results shown with a bogus search term
   */
  @Test
  public void ensureSearchResultsEmptyWithBogusSearchTerm() {

    clickOn("#searchBar");
    write(this.BOGUS);
    // assertThat(this.searchResultsView).hasExactlyNumItems(0);
  }

  /**
   * Tests functionality of the clearSearchResultsButton
   */
  @Test
  public void testClearSearchResultsButton() {

    //
    // int originalItemCount = this.searchResultsView.getItems().size();
    // TextField searchBar = find("#searchBar");
    // clickOn("#searchBar");
    // write(this.templateSetObservableList.get(0).getName());
    // assertThat(this.searchResultsView).hasExactlyNumItems(1);
    // clickOn("#clearSearchResultsButton");
    // assertThat(searchBar.getText().length() == 0);
    // assertThat(this.searchResultsView).hasExactlyNumItems(originalItemCount);
  }

  /**
   * Selects all items in the search results and tests if the correct template set details page is shown
   */
  @Test
  public void testSelectionOfTemplateSet() {

    Label titleLabel;
    // for (int i = 0; i < this.templateSetObservableList.size(); i++) {
    // this.searchResultsView.getSelectionModel().select(i);
    // titleLabel = find("#titleLabel");
    // assertThat(titleLabel.getText() == this.searchResultsView.getSelectionModel().getSelectedItem().getName());
    // }
  }

}
