package com.devonfw.cobigen.impl.config.entity;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.mmm.util.lang.api.CaseSyntax;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.api.util.StringUtil;
import com.devonfw.cobigen.impl.config.reader.CobiGenPropertiesReader;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;
import com.devonfw.cobigen.impl.model.ModelBuilderImpl;

/**
 * This class is a container for variables that can inherit from parent {@link Variables} building a hierarchy. The
 * {@link #containsKey(String) keys} for {@link #get(String) getting} and {@link #put(String, String) setting} variables
 * are internally normalized (see {@link CaseSyntax#normalizeExample(String)}) and therefore treated case-insensitive as
 * well as stripped from special characters. Hence, you should name and use variable names in CobiGen templates and
 * their paths accordingly. For legacy support also the original variable name is used with priority so that special
 * characters are still supported for legacy syntax (e.g. <code>${Variable-Name}</code>).
 */
public class Variables {

  /** The variables prefix. */
  private static final String PREFIX_VARIABLES = ModelBuilderImpl.NS_VARIABLES + ".";

  /**
   * A {@link Character#isLetter(char) letter} character that is not to be expected to occur in regular input values.
   */
  private static final char DUMMY_LETTER_FOR_DOT = 'ʵ';

  /** Regex {@link Pattern} for variable in dollar syntax (<code>${...}</code>). */
  private static final Pattern PATTERN_VARIABLE_DOLLAR_SYNTAX = Pattern
      .compile("\\$\\{([^?#}]+)(((\\?|#)[^}?#]+)*)\\}");

  /** Regex {@link Pattern} for variable in language-agnostic case-syntax (<code>x_..._x</code>). */
  private static final Pattern PATTERN_VARIABLE_CASE_SYNTAX = Pattern.compile("[xX]__([a-zA-Z0-9_-]+?)__[xX]");

  /** The parent {@link Variables} to inherit or {@code null}. */
  private final Variables parent;

  /** The {@link Properties} containing the local variables. */
  private final Properties properties;

  /**
   * The constructor for the root variables.
   */
  public Variables() {

    this(null, null);
  }

  /**
   * The constructor for the root variables.
   *
   * @param properties the internal {@link Properties} with the variables locally defined here.
   */
  public Variables(Properties properties) {

    this(properties, null);
  }

  /**
   * The constructor for the child variables.
   *
   * @param parent the parent {@link Variables} to inherit from or {@code null} for the root {@link Variables}.
   */
  public Variables(Variables parent) {

    this(null, parent);
  }

  /**
   * The constructor for the child variables.
   *
   * @param properties the internal {@link Properties} with the variables locally defined here.
   * @param parent the parent {@link Variables} to inherit from or {@code null} for the root {@link Variables}.
   */
  public Variables(Properties properties, Variables parent) {

    super();
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = properties;
    }
    this.parent = parent;
  }

  /**
   * @param key the raw key.
   * @return the normalized key.
   */
  private String normalizeKey(String key) {

    return CaseSyntax.normalizeExample(key);
  }

  /**
   * @see #containsKey(String)
   *
   * @param key the raw key to check.
   * @return {@code true} if this {@link Variables} contain the given {@code key}, {@code false} otherwise.
   */
  private boolean containsKeyInternal(String key) {

    if (this.properties.containsKey(key)) {
      return true;
    }
    if (this.parent != null) {
      return this.parent.containsKeyInternal(key);
    }
    return false;
  }

  /**
   * @see Map#containsKey(Object)
   *
   * @param key the key to check.
   * @return {@code true} if this {@link Variables} contain the given {@code key}, {@code false} otherwise.
   */
  public boolean containsKey(String key) {

    if (containsKeyInternal(key)) {
      return true;
    }
    String normalizeKey = normalizeKey(key);
    if (normalizeKey.equals(key)) {
      return false;
    }
    return containsKeyInternal(normalizeKey);
  }

  /**
   * @see #getInternal(String)
   *
   * @param key the raw key to get.
   * @return the value of the variable with the given {@code key}. May be {@code null}.
   */
  private String getInternal(String key) {

    String value = this.properties.getProperty(key);
    if ((value == null) && !containsKeyInternal(key) && (this.parent != null)) {
      value = this.parent.getInternal(key);
    }
    return value;
  }

  /**
   * @see Map#get(Object)
   *
   * @param key the key to get.
   * @return the value of the variable with the given {@code key}. May be {@code null}.
   */
  public String get(String key) {

    String value = getInternal(key);
    if (value == null) {
      String normalizeKey = normalizeKey(key);
      if (!normalizeKey.equals(key)) {
        value = getInternal(normalizeKey);
      }
    }
    return value;
  }

  /**
   * @see Map#put(Object, Object)
   *
   * @param key the key of the variable to set.
   * @param value the value of the variable to set.
   * @return the previous value of the given variable.
   */
  public String put(String key, String value) {

    String old = getInternal(key);
    this.properties.setProperty(key, value);
    String normalizeKey = normalizeKey(key);
    if (!normalizeKey.equals(key)) {
      if (old == null) {
        old = getInternal(normalizeKey);
      }
      this.properties.put(normalizeKey, value);
    }
    return old;
  }

  /**
   * @return this {@link Variables} as {@link Map}. Further changes to this {@link Variables} or one of its ancestors
   *         are NOT reflected by the returned {@link Map}. This is an expensive operation that may only be called to
   *         pass {@link Variables} to a templating engine that cannot support {@link Variables} directly.
   */
  public Map<String, String> asMap() {

    HashMap<String, String> map = new HashMap<>(this.properties.size());
    asMap(map);
    return map;
  }

  /**
   * @see #asMap()
   * @param map the {@link Map} to populate.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void asMap(Map<String, String> map) {

    if (this.parent != null) {
      this.parent.asMap(map);
    }
    map.putAll((Map) this.properties);
  }

  /**
   * Creates a new variables instance from the given map.
   *
   * @param map variable entries to be added.
   * @return the newly created instance.
   */
  public static Variables fromMap(Map<String, String> map) {

    Variables variables = new Variables();
    variables.putAll(map);
    return variables;
  }

  /**
   * @param folder the {@link Path} pointing to a child-folder potentially containing
   *        {@link ConfigurationConstants#COBIGEN_PROPERTIES cobigen.properties}.
   * @return a new {@link Variables} instance inherited from this one with the
   *         {@link ConfigurationConstants#COBIGEN_PROPERTIES cobigen.properties} set or this {@link Variables} if no
   *         such properties exists.
   */
  public Variables forChildFolder(Path folder) {

    Properties childProperties = CobiGenPropertiesReader.load(folder, this.properties);
    if (childProperties == this.properties) {
      return this;
    }
    return new Variables(childProperties, this);
  }

  /**
   * @param string the {@link String} where to resolve all variables.
   * @return the given {@code string} with all variables (e.g. <code>${variableName#uncapfirst}</code> or
   *         <code>$_VariableName_$</code>) replaced with those defined by this {@link Variables}.
   */
  public String resolve(String string) {

    return resolve(string, '.');
  }

  /**
   *
   * @param string the {@link String} where to resolve all variables.
   * @param replacementForDot the character used as replacement for the dot character ('.') or '\0' for no replacement
   *        (remove dots according to case syntax).
   * @return the given {@code string} with all variables (e.g. <code>${variableName#uncapfirst}</code> or
   *         <code>$_VariableName_$</code>) replaced with those defined by this {@link Variables}.
   */
  public String resolve(String string, char replacementForDot) {

    String resolvedString = resolveVariables(string, PATTERN_VARIABLE_DOLLAR_SYNTAX, false, replacementForDot);
    resolvedString = resolveVariables(resolvedString, PATTERN_VARIABLE_CASE_SYNTAX, true, replacementForDot);
    return resolvedString;
  }

  /**
   * Resolves all variables from the given {@code string}.
   *
   * @param string the string to resolve.
   * @param pattern the {@link Pattern} with the variable syntax. The {@link Matcher#group(int) group(1)} has to match
   *        the actual variable name.
   * @param supportCase {@code true} for the new case transformation by example, {@code false} otherwise.
   * @param replacementForDot the character used as replacement for the dot character ('.') or '\0' for no replacement.
   * @return the given {@code string} with all variables resolved.
   */
  private String resolveVariables(String string, Pattern pattern, boolean supportCase, char replacementForDot) {

    Matcher m = pattern.matcher(string.toString());
    StringBuffer out = new StringBuffer();
    while (m.find()) {
      String variableKey = m.group(1);
      if (!supportCase && (variableKey.startsWith(PREFIX_VARIABLES))) {
        variableKey = variableKey.substring(PREFIX_VARIABLES.length());
      }
      // a variable like ${detail} can be explicitly set to null
      // this is considered as the empty string but null instead of "" is required for free-marker
      if (!containsKey(variableKey)) {
        throw new UnknownContextVariableException(variableKey);
      }

      String variableValue = get(variableKey);
      if (variableValue != null) {
        boolean containsDot = variableValue.contains(".");
        if (containsDot && (replacementForDot != '\0')) {
          if (supportCase) {
            variableValue = variableValue.replace('.', DUMMY_LETTER_FOR_DOT);
          } else {
            variableValue = variableValue.replace('.', replacementForDot);
          }
        }
        if (supportCase) {
          CaseSyntax syntax = CaseSyntax.ofExample(variableKey, true);
          variableValue = syntax.convert(variableValue);
          if (containsDot) {
            variableValue = variableValue.replace(DUMMY_LETTER_FOR_DOT, replacementForDot);
          }
        } else {
          variableValue = resolveFunction(variableValue, m.group(2));
        }
        m.appendReplacement(out, variableValue);
      } else {
        m.appendReplacement(out, "");
      }
    }
    m.appendTail(out);

    return out.toString();

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
          continue; // ignore first as always empty due to beginning '?'
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

  /**
   * @param map the {@link Map} with the variables to add.
   */
  public void putAll(Map<String, String> map) {

    for (Entry<String, String> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
}
