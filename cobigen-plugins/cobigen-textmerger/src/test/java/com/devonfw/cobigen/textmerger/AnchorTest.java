package com.devonfw.cobigen.textmerger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.textmerger.anchorextension.Anchor;
import com.devonfw.cobigen.textmerger.anchorextension.MergeStrategy;

/**
 *
 */
public class AnchorTest {

  private static final MergeStrategy testStrat = MergeStrategy.APPEND;

  /**
   * Tests if anchors equals when their definition is the same.
   */
  @Test
  public void testEquals() {

    Anchor one = new Anchor("// ", "test", testStrat, false, false);
    Anchor two = new Anchor("// ", "test2", testStrat, false, false);
    Anchor three = new Anchor("// ", "test", testStrat, false, false);
    String four = "";

    assertThat(one).isEqualTo(three);
    assertThat(one).isNotEqualTo(two);
    assertThat(one).isNotEqualTo(four);
  }

  /**
   * Tests if a null parameter (happens when an anchor is created from an anchor definition like
   * anchor:something:anchorend) is converted to an empty String or the mergestrategy error(which will be an empty
   * string when getting the whole anchor), which will not match the regular expression for a correct anchor that
   * requires text between colons, thus throwing an exception when splitting a text by anchors.
   */
  @Test
  public void testNullParameterConvertsToEmpty() {

    Anchor test = new Anchor("// ", "test", null, false, false);
    String result = "// anchor:test::anchorend";
    Anchor test2 = new Anchor("// ", null, testStrat, false, false);
    String result2 = "// anchor::append:anchorend";
    assertThat(test.getAnchor()).isEqualTo(result);
    assertThat(test.getMergeStrat()).isEqualTo(MergeStrategy.ERROR);
    assertThat(test2.getAnchor()).isEqualTo(result2);
  }

  /**
   * Tests if getting the newline name puts _newline or newline_ correctly only if there is supposed to be a newline
   */
  @Test
  public void testNewlineName() {

    Anchor test1 = new Anchor("// ", "test1", testStrat, true, true);
    Anchor test2 = new Anchor("// ", "test2", testStrat, true, false);
    Anchor test3 = new Anchor("// ", "test3", testStrat, false, true);
    Anchor test4 = new Anchor("// ", "test4", testStrat, false, false);

    assertThat(test1.getNewlineName()).isEqualTo("newline_append");
    assertThat(test2.getNewlineName()).isEqualTo("append_newline");
    assertThat(test3.getNewlineName()).isEqualTo("append");
    assertThat(test4.getNewlineName()).isEqualTo("append");
  }

}
