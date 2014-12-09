package com.capgemini.cobigen.javaplugin.unittest.matcher.resolver;

import org.junit.Assert;
import org.junit.Test;

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
}
