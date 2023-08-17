package com.devonfw.cobigen.unittest.config.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.impl.config.entity.Variables;
import com.devonfw.cobigen.impl.config.resolver.PathExpressionResolver;

import io.github.mmm.base.text.CaseSyntax;

/**
 * Test suite for {@link PathExpressionResolver}
 */
public class PathExpressionResolverTest {

    /**
     * Test target
     */
    private static PathExpressionResolver target;

    static {
        Variables variables = new Variables();
        variables.put("v1", "praefix Value Suffix");
        variables.put("v2", "Praefix Value Suffix");
        variables.put("variablename", "PrefixValueSuffix");
        variables.put("variablekey", "prefix_value_suffix");
        variables.put("PackageName", "my.pkg.name");
        target = new PathExpressionResolver(variables);
    }

    /**
     * Tests expression resolving without any expression
     */
    @Test
    public void testEvaluateExpressionNoExpression() {

        assertThat(target.evaluateExpressions("asdf asdf")).isEqualTo("asdf asdf");
    }

    /**
     * Tests expression resolving with ?cap_first expression
     */
    @Test
    public void testEvaluateExpressionCapFirst() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?cap_first} asdf"))
                .isEqualTo("asdfPraefix Value Suffix asdf");
    }

    /**
     * Tests expression resolving with ?uncap_first expression
     */
    @Test
    public void testEvaluateExpressionUncapFirst() {

        assertThat(target.evaluateExpressions("asdf${variables.v2?uncap_first} asdf"))
                .isEqualTo("asdfpraefix Value Suffix asdf");
    }

    /**
     * Tests expression resolving with ?lower_case expression
     */
    @Test
    public void testEvaluateExpressionLowerCase() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?lower_case} asdf"))
                .isEqualTo("asdfpraefix value suffix asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionUpperCase() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?upper_case} asdf"))
                .isEqualTo("asdfPRAEFIX VALUE SUFFIX asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionReplace() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?replace('Value', 'Replacement')} asdf"))
                .isEqualTo("asdfpraefix Replacement Suffix asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionReplaceAll() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?replace('x', 'XXX')} asdf"))
                .isEqualTo("asdfpraefiXXX Value SuffiXXX asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionRemoveSuffix() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?removeSuffix('Suffix')} asdf"))
                .isEqualTo("asdfpraefix Value  asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionRemovePraefix() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?removePrefix('praefix')} asdf"))
                .isEqualTo("asdf Value Suffix asdf");
    }

    /**
     * Tests expression resolving with ?upper_case expression
     */
    @Test
    public void testEvaluateExpressionConcatenation() {

        assertThat(target.evaluateExpressions("asdf${variables.v1?lower_case?removePrefix('praefix')} asdf"))
                .isEqualTo("asdf value suffix asdf");
    }

  /**
   * Test of {@link PathExpressionResolver#evaluateExpressions(String)} using the
   * {@link io.github.mmm.base.text.CaseSyntax} with arbitrary cases.
   */
  @Test
  public void testEvaluateExpressionCaseSyntax() {

    assertThat(target.evaluateExpressions("foo-X_VariableName_X-bar-x_variableName_x-some"))
        .isEqualTo("foo-PrefixValueSuffix-bar-prefixValueSuffix-some");
    assertThat(target.evaluateExpressions("fooX_VariableName_Xbar")).isEqualTo("fooPrefixValueSuffixbar");
  }
}
