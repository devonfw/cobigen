package com.devonfw.cobigen.openapiplugin.unittest.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.openapiplugin.matcher.OpenAPIMatcher;
import com.devonfw.cobigen.openapiplugin.model.ComponentDef;
import com.devonfw.cobigen.openapiplugin.model.EntityDef;

/**
 * Test suite for {@link OpenAPIMatcher}
 */
public class OpenAPIMatcherTest {

  /**
   * Test valid {@link EntityDef} matching
   */
  @Test
  public void testValidEntityDefMatching() {

    EntityDef entityDef = new EntityDef();
    entityDef.setComponentName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "EntityDef", entityDef));

    assertThat(matches).isTrue();
  }

  /**
   * Test valid {@link ComponentDef} matching
   */
  @Test
  public void testValidComponentDefMatching() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "ComponentDef", componentDef));

    assertThat(matches).isTrue();
  }

  /**
   * Test non valid {@link EntityDef} matching
   */
  @Test
  public void testInvalidEntityDefMatching() {

    EntityDef entityDef = new EntityDef();
    entityDef.setComponentName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "EntityDefs", entityDef));

    assertThat(matches).isFalse();
  }

  /**
   * Test non valid {@link ComponentDef} matching
   */
  @Test
  public void testInvalidComponentDefMatching() {

    ComponentDef componentDef = new ComponentDef();
    componentDef.setName("Tablemanagement");

    OpenAPIMatcher matcher = new OpenAPIMatcher();
    boolean matches = matcher.matches(new MatcherTo("element", "ComponentDefs", componentDef));

    assertThat(matches).isFalse();
  }
}
