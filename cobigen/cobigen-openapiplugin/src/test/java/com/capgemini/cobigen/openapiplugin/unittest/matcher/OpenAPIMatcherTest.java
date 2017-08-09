package com.capgemini.cobigen.openapiplugin.unittest.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.openapiplugin.matcher.OpenAPIMatcher;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;
import com.capgemini.cobigen.openapiplugin.model.EntityDef;

/**
 * Test suite for {@link OpenAPIMatcher}
 */
public class OpenAPIMatcherTest {

    /**
     * Test valid {@link ComponentDef} matching
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
     * Test non valid {@link ComponentDef} matching
     */
    @Test
    public void testInvalidEntityDefMatching() {

        EntityDef entityDef = new EntityDef();
        entityDef.setComponentName("Tablemanagement");

        OpenAPIMatcher matcher = new OpenAPIMatcher();
        boolean matches = matcher.matches(new MatcherTo("element", "EntityDefs", entityDef));

        assertThat(matches).isFalse();
    }
}
