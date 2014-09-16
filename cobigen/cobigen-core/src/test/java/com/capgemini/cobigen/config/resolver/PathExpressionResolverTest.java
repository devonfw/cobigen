package com.capgemini.cobigen.config.resolver;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

/**
 * Test suite for {@link PathExpressionResolver}
 * @author mbrunnli (16.09.2014)
 */
public class PathExpressionResolverTest {

    /**
     * Test target
     */
    private static PathExpressionResolver target;

    static {
        Map<String, String> variables = Maps.newHashMap();
        variables.put("v1", "praefix Value Suffix");
        variables.put("v2", "Praefix Value Suffix");
        target = new PathExpressionResolver(variables);
    }

    /**
     * Tests expression resolving without any expression
     */
    @Test
    public void testEvaluateExpressionNoExpression() {
        Assert.assertEquals("asdf asdf", target.evaluateExpressions("asdf asdf"));
    }

    /**
     * Tests expression resolving with ?cap_first expression
     */
    @Test
    public void testEvaluateExpressionCapFirst() {
        Assert.assertEquals("asdfPraefix Value Suffix asdf",
            target.evaluateExpressions("asdf${variables.v1?cap_first} asdf"));
    }

    /**
     * Tests expression resolving with ?uncap_first expression
     */
    @Test
    public void testEvaluateExpressionUncapFirst() {
        Assert.assertEquals("asdfpraefix Value Suffix asdf",
            target.evaluateExpressions("asdf${variables.v2?uncap_first} asdf"));
    }

    /**
     * Tests expression resolving with ?lower_case expression
     */
    @Test
    public void testEvaluateExpressionLowerCase() {
        Assert.assertEquals("asdfpraefix value suffix asdf",
            target.evaluateExpressions("asdf${variables.v1?lower_case} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionUpperCase() {
        Assert.assertEquals("asdfPRAEFIX VALUE SUFFIX asdf",
            target.evaluateExpressions("asdf${variables.v1?upper_case} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionReplace() {
        Assert.assertEquals("asdfpraefix Replacement Suffix asdf",
            target.evaluateExpressions("asdf${variables.v1?replace(\"Value\", \"Replacement\")} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionReplaceAll() {
        Assert.assertEquals("asdfpraefiXXX Value SuffiXXX asdf",
            target.evaluateExpressions("asdf${variables.v1?replace(\"x\", \"XXX\")} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionRemoveSuffix() {
        Assert.assertEquals("asdfpraefix Value  asdf",
            target.evaluateExpressions("asdf${variables.v1?removeSuffix(\"Suffix\")} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionRemovePraefix() {
        Assert.assertEquals("asdf Value Suffix asdf",
            target.evaluateExpressions("asdf${variables.v1?removePraefix(\"praefix\")} asdf"));
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionConcatenation() {
        Assert.assertEquals("asdf value suffix asdf",
            target.evaluateExpressions("asdf${variables.v1?lower_case?removePraefix(\"praefix\")} asdf"));
    }
}
