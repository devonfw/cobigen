package com.devonfw.cobigen.api.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.api.util.StringUtil;

import io.github.mmm.base.text.CaseConversion;
import io.github.mmm.base.text.CaseSyntax;

/**
 * Enum with the available syntax for variables supported by CobiGen.
 */
public enum VariableSyntax {

  /** Variables are surrounded with "${" and "}" such as <code>${variableName#uncapfirst}</code>. */
  // .....1......2........345
  DOLLAR("(\\$\\{([^?#}]+)(((\\?|#)[^}?#]+)*)\\})") {

    @Override
    public String getVariable(Matcher matcher) {

      String variable = matcher.group(2);
      String nameVariables = CobiGenVariableDefinitions.VARIABLES.getName();
      if (variable.startsWith(nameVariables)) {
        variable = variable.substring(nameVariables.length());
      }
      return variable;
    }

    @Override
    public String resolve(String value, Matcher matcher, String name) {

      return resolveFunction(value, matcher.group(3));
    }

    /**
     * Legacy support for freemarker function syntax.
     *
     * @param value the value of the variable to resolve.
     * @param function the freemarker function(s) to simulate and apply.
     * @return the resolved {@code value} with the given freemarker function(s) applied.
     */
    private String resolveFunction(String value, String function) {

      if (function != null) {
        boolean first = true;
        for (String modifier : function.split("(\\?|#)")) {
          if (first) {
            first = false;
            // ignore first as always empty due to beginning '?'
            continue;
          }
          value = applyStringModifier(modifier, value);
        }
      }
      return value;
    }

    /**
     * Applies the given {@link String} modifier defined by ?modifier behind the variable reference
     *
     * @param modifierName name of the {@link String} modifier to be applied
     * @param string {@link String} the modifier should be applied on
     * @return the modified {@link String}
     * @throws UnknownExpressionException if there is an unknown variable modifier
     */
    private String applyStringModifier(String modifierName, String string) throws UnknownExpressionException {

      // simple operators
      if (modifierName.equals("cap_first")) {
        return StringUtil.capFirst(string);
      } else if (modifierName.equals("uncap_first")) {
        return StringUtil.uncapFirst(string);
      } else if (modifierName.equals("lower_case")) {
        return string.toLowerCase();
      } else if (modifierName.equals("upper_case")) {
        return string.toUpperCase();
      }

      String parameterRegex = "\\s*'([^']*)'\\s*";

      // ?replace(String regex, String replacement)
      Pattern p = Pattern.compile("replace\\(" + parameterRegex + "," + parameterRegex + "\\)");
      Matcher m = p.matcher(modifierName);

      if (m.matches()) {
        return string.replaceAll(m.group(1), m.group(2));
      }

      // ?removeSuffix(String suffix)
      p = Pattern.compile("removeSuffix\\(" + parameterRegex + "\\)");
      m = p.matcher(modifierName);

      if (m.matches() && string.endsWith(m.group(1))) {
        return string.substring(0, string.length() - m.group(1).length());
      }

      // ?removePrefix(String prefix)
      p = Pattern.compile("removePrefix\\(" + parameterRegex + "\\)");
      m = p.matcher(modifierName);

      if (m.matches() && string.startsWith(m.group(1))) {
        return string.substring(m.group(1).length(), string.length());
      }

      throw new UnknownExpressionException("?" + modifierName);
    }

  },

  /**
   * Variables are surrounded with "X_" and "_X" respectively "x_" and "_x" and the {@link CaseSyntax} is used for
   * automatic conversion. The following table gives examples that illustrate the behavior.
   * <table border="1">
   * <tr>
   * <th>Variable</th>
   * <th>Raw value</th>
   * <th>Resolved value</th>
   * </tr>
   * <tr>
   * <td><code>X_VariableName_X</code></td>
   * <td><code>MyExample</code></td>
   * <td><code>MyExample</code></td>
   * </tr>
   * <tr>
   * <td><code>x_variableName_x</code></td>
   * <td><code>MyExample</code></td>
   * <td><code>myExample</code></td>
   * </tr>
   * <tr>
   * <td><code>x_variablename_x</code></td>
   * <td><code>MyExample</code></td>
   * <td><code>myexample</code></td>
   * </tr>
   * <tr>
   * <td><code>X_VARIABLE_NAME_X</code></td>
   * <td><code>MyExample</code></td>
   * <td><code>MY_EXAMPLE</code></td>
   * </tr>
   * <tr>
   * <td><code>x_variable-name_x</code></td>
   * <td><code>MyExample</code></td>
   * <td><code>my-example</code></td>
   * </tr>
   * </table>
   */
  // .......1.....23......4.........................5
  AGNOSTIC("(\\.?)(([xX][_.-])([a-zA-Z_][a-zA-Z0-9_.$-]*?)([_.-][xX]))") {

    @Override
    public String getVariable(Matcher matcher) {

      return matcher.group(4);
    }

    @Override
    public String resolve(String value, Matcher matcher, String name) {

      String dot = matcher.group(1);
      if (value.isEmpty()) {
        // TODO this should actually depend on the location of the variable and the CaseSyntax
        // So looking at the following example:
        // package x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x;
        // In case that the variable "scope" or "detail" resolves to the empty string, then this package segment shall
        // be omitted however resolving only the variable to the empty String would result in trailing dots at the end:
        // com.customer.app.mycomponent.dataaccess..;
        // As we already have another edge-case for package segment variables that if the variable contains dots
        // (e.g. rootpackage="com.customer.app") then CaseSyntax.LOWER_CASE would remove the dot as undesired separators
        // As I implemented CaseSyntax mainly for CobiGen and Language-Agnostic-Templates I am more than happy to change
        // it (even incompatible) to fit CobiGen's needs. So if the first character is a special character (no letter)
        // then separators should be preserved. Therefore we would then need to write this instead:
        // package x__rootpackage_x.x__component_x.dataaccess.x__scope_x.x__detail_x;
        // Even though kind of cryptic, but still easy to hanlde once you learned how to do it in Java packages.
        // This is IMHO better than writing this (causing variables split across folder names):
        // package x__root.package_x.x_component_x.dataaccess.x_det.ail_x;
        return "";
      }
      CaseSyntax caseSyntax = CaseSyntax.ofExample(name, true);
      verifySyntax(caseSyntax, matcher);
      if (caseSyntax == CaseSyntax.LOWERCASE) {
        // see comment above
        value = CaseConversion.LOWER_CASE.convert(value);
      } else {
        value = caseSyntax.convert(value);
      }
      if (!dot.isEmpty()) {
        value = dot + value;
      }
      return value;
    }

    void verifySyntax(CaseSyntax caseSyntax, Matcher matcher) {

      // x_entityname_x
      // X_EntityName_X
      // X_Entityname_x
      String prefix = matcher.group(3);
      String suffix = matcher.group(5);
      CaseConversion firstCase = caseSyntax.getFirstCase();
      String prefixCased = firstCase.convert(prefix);
      String suffixCased = firstCase.convert(suffix);
      if (prefix.equals(prefixCased) && (prefix.charAt(1) == suffix.charAt(0))) {
        if (suffix.equals(suffixCased)) {
          return;
        }
        // edge-cases but lets consider syntax like X_Caplowercase_x as valid...
        suffixCased = caseSyntax.getWordStartCase().convert(suffix);
        if (suffix.equals(suffixCased)) {
          return;
        }
        suffixCased = caseSyntax.getOtherCase().convert(suffix);
        if (suffix.equals(suffixCased)) {
          return;
        }
      }
      LOG.warn("Inconsistent variable syntax: {}", matcher.group(2));
    }
  };

  private static final Logger LOG = LoggerFactory.getLogger(VariableSyntax.class);

  private final Pattern pattern;

  VariableSyntax(String regex) {

    this.pattern = Pattern.compile(regex);
  }

  /**
   * @return the regular expression {@link Pattern} for this {@link VariableSyntax}.
   */
  public Pattern getPattern() {

    return this.pattern;
  }

  /**
   * @param matcher the current {@link Matcher}.
   * @return the variable name.
   */
  public abstract String getVariable(Matcher matcher);

  /**
   * @param value the {@link String} with the variable value to resolve.
   * @param matcher the current {@link Matcher}.
   * @param name the variable name as before returned by {@link #getVariable(Matcher)}.
   * @return the resolved {@link String}. May be the given {@code value} itself, but can also be a transformation.
   */
  public abstract String resolve(String value, Matcher matcher, String name);

}