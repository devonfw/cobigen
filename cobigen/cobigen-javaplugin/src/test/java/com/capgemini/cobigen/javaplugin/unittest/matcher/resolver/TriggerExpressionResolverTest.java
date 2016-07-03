package com.capgemini.cobigen.javaplugin.unittest.matcher.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.capgemini.cobigen.javaplugin.matcher.resolver.TriggerExpressionResolver;

/**
 * The class <code>TriggerExpressionResolverTest</code> contains tests for the class
 * {@link TriggerExpressionResolver}
 *
 * @author mbrunnli (05.04.2013)
 */
public class TriggerExpressionResolverTest {

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_instanceof() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(TriggerExpressionResolver.class);

        Assert.assertFalse(target.evaluateExpression("instanceof java.lang.String"));
        Assert.assertTrue(target.evaluateExpression("instanceof java.lang.Object"));
    }

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_isAbstract_valid() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(AbstractIntegrationTest.class);

        assertTrue(target.evaluateExpression("isAbstract"));
    }

    /**
     * Test for {@link TriggerExpressionResolver#evaluateExpression(String)}
     */
    @Test
    public void testEvaluateExpression_isAbstract_invalid() {
        TriggerExpressionResolver target = new TriggerExpressionResolver(getClass());

        assertFalse(target.evaluateExpression("isAbstract"));
    }
}
