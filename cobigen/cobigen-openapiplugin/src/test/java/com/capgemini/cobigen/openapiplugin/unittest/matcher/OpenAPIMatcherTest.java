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

        ComponentDef component = new ComponentDef();
        component.setComponent("Tablemanagement");
        component.setVersion("v1");

        OpenAPIMatcher matcher = new OpenAPIMatcher();
        boolean matches = matcher.matches(new MatcherTo("element", "ComponentDef", component));

        assertThat(matches).isTrue();
    }

    /**
     * Test non valid {@link ComponentDef} matching
     */
    @Test
    public void testInvalidComponentDefMatching() {

        ComponentDef component = new ComponentDef();
        component.setComponent("Tablemanagement");
        component.setVersion("v1");

        OpenAPIMatcher matcher = new OpenAPIMatcher();
        boolean matches = matcher.matches(new MatcherTo("element", "ComponentDefs", component));

        assertThat(matches).isFalse();
    }
}
