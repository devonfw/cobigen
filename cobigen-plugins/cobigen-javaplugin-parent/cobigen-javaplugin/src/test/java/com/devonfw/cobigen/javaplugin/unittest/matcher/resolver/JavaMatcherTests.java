package com.devonfw.cobigen.javaplugin.unittest.matcher.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.to.VariableAssignmentTo;
import com.devonfw.cobigen.javaplugin.matcher.JavaMatcher;

/**
 * Test suite for {@link JavaMatcher}.
 */
public class JavaMatcherTests {

  /**
   * Object under test
   */
  private JavaMatcher javaMatcher;

  /**
   * @throws Exception shouldn't occur
   */
  @Before
  public void setUp() throws Exception {

    this.javaMatcher = new JavaMatcher();
  }

  /**
   * Test method for
   * {@link com.devonfw.cobigen.javaplugin.matcher.JavaMatcher#getResolvedVariables(com.devonfw.cobigen.javaplugin.matcher.JavaMatcher.MatcherType, java.lang.String, java.lang.String, java.util.List)}
   * . Tests if the algorithm handles empty variables correctly
   */
  @SuppressWarnings("javadoc")
  @Test
  public void resolveEmptyRegexVariable() {

    List<VariableAssignmentTo> variables = new LinkedList<>();
    VariableAssignmentTo rootPackage = new VariableAssignmentTo("regex", "rootPackage", "1");
    VariableAssignmentTo domain = new VariableAssignmentTo("regex", "domain", "3");
    VariableAssignmentTo component = new VariableAssignmentTo("regex", "component", "4");
    VariableAssignmentTo detail = new VariableAssignmentTo("regex", "detail", "5");
    VariableAssignmentTo typeName = new VariableAssignmentTo("regex", "typeName", "6");
    variables.add(rootPackage);
    variables.add(domain);
    variables.add(component);
    variables.add(detail);
    variables.add(typeName);

    String inputValue = "de.tukl.abc.project.standard.datatype.common.api.LongText";
    String regex = "((.+\\.)?([^.]+))\\.(datatype)\\.common\\.api(\\..*)?\\.([^.]+)";

    Map<String, String> result = this.javaMatcher.getResolvedVariables(null, regex, inputValue, variables);
    assertThat(result.get("detail")).as("value of detail").isEmpty();
  }

  /**
   * Test method for
   * {@link com.devonfw.cobigen.javaplugin.matcher.JavaMatcher#getResolvedVariables(com.devonfw.cobigen.javaplugin.matcher.JavaMatcher.MatcherType, java.lang.String, java.lang.String, java.util.List)}
   * . tests if the algorithm handles non empty variables correctly (control-test to
   * {@link #resolveEmptyRegexVariable()})
   */
  @SuppressWarnings("javadoc")
  @Test
  public void resolveNonEmptyRegexVariable() {

    List<VariableAssignmentTo> variables = new LinkedList<>();
    VariableAssignmentTo rootPackage = new VariableAssignmentTo("regex", "rootPackage", "1");
    VariableAssignmentTo domain = new VariableAssignmentTo("regex", "domain", "3");
    VariableAssignmentTo component = new VariableAssignmentTo("regex", "component", "4");
    VariableAssignmentTo detail = new VariableAssignmentTo("regex", "detail", "5");
    VariableAssignmentTo typeName = new VariableAssignmentTo("regex", "typeName", "6");
    variables.add(rootPackage);
    variables.add(domain);
    variables.add(component);
    variables.add(detail);
    variables.add(typeName);

    String inputValue = "de.tukl.abc.project.standard.datatype.common.api.subpackage.LongText";
    String regex = "((.+\\.)?([^.]+))\\.(datatype)\\.common\\.api(\\..*)?\\.([^.]+)";

    Map<String, String> result = this.javaMatcher.getResolvedVariables(null, regex, inputValue, variables);
    assertThat(result.get("detail")).as("value of detail").isEqualTo(".subpackage");
  }

}
