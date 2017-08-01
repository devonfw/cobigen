package com.capgemini.cobigen.openapiplugin.unittest.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.openapiplugin.matcher.OpenAPIMatcher;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;

/**
 * Test suite for {@link OpenAPIMatcher}
 */
public class OpenAPIMatcherTest {

    /**
     * Test valid {@link ComponentDef} matching
     */
    @Test
    public void testValidComponentDefMatching() {

        ComponentDef componentDef = new ComponentDef();
        componentDef.setComponent("Tablemanagement");
        componentDef.setVersion("v1");

        OpenAPIMatcher matcher = new OpenAPIMatcher();
        boolean matches = matcher.matches(new MatcherTo("element", "ComponentDef", componentDef));

        assertThat(matches).isTrue();
    }

    /**
     * Test non valid {@link ComponentDef} matching
     */
    @Test
    public void testInvalidComponentDefMatching() {

        ComponentDef componentDef = new ComponentDef();
        componentDef.setComponent("Tablemanagement");
        componentDef.setVersion("v1");

        OpenAPIMatcher matcher = new OpenAPIMatcher();
        boolean matches = matcher.matches(new MatcherTo("element", "ComponentDefs", componentDef));

        assertThat(matches).isFalse();
    }
}
