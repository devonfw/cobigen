package com.devonfw.cobigen.javaplugin.unittest.matcher.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.devonfw.cobigen.javaplugin.matcher.resolver.TriggerExpressionResolver;

/**
 * The class <code>TriggerExpressionResolverTest</code> contains tests for the class
 * {@link TriggerExpressionResolver}
 */
public class TriggerExpressionResolverTest {

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_instanceof() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(TriggerExpressionResolver.class);

        assertThat(target.evaluateExpression("instanceof java.lang.String")).isFalse();
        assertThat(target.evaluateExpression("instanceof java.lang.Object")).isTrue();
    }

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_isAbstract_valid() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(AbstractIntegrationTest.class);

        assertThat(target.evaluateExpression("isAbstract")).isTrue();
    }

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_isAbstract_invalid() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(getClass());

        assertThat(target.evaluateExpression("isAbstract")).isFalse();
    }
}
