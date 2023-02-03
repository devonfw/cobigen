package com.devonfw.cobigen.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link CobiGenModel}.
 */
public abstract class AbstractCobiGenModel implements CobiGenModel {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractCobiGenModel.class);

  private final AbstractCobiGenModel parent;

  private final Map<String, Object> map;

  /**
   * The constructor.
   *
   * @param parent the parent model or {@code null} if this is the root model.
   */
  public AbstractCobiGenModel(CobiGenModel parent) {

    this(parent, new HashMap<>());
  }

  /**
   * The constructor.
   *
   * @param parent the parent model or {@code null} if this is the root model.
   * @param map the underlying {@link Map}.
   */
  protected AbstractCobiGenModel(CobiGenModel parent, Map<String, Object> map) {

    super();
    this.parent = (AbstractCobiGenModel) parent;
    this.map = map;
  }

  /**
   * @return the inherited parent {@link AbstractCobiGenModel model} or {@code null} if this is the root model.
   */
  @Override
  public AbstractCobiGenModel getParent() {

    return this.parent;
  }

  /**
   * This method is only for CobiGen internal use!
   *
   * @return the original {@link Map} with variables that preserves the original syntax and case of the variables.
   */
  public Map<String, Object> getOriginalMap() {

    return this.map;
  }

  @Override
  public Object get(String name) {

    Object value = getInternal(name);
    if (value == null) {
      String normalizeKey = CobiGenVariableDefinition.normalizeName(name);
      if (!normalizeKey.equals(name)) {
        value = getInternal(normalizeKey);
      }
    }
    return value;
  }

  private Object getInternal(String key) {

    Object value = this.map.get(key);
    if ((value == null) && (this.parent != null) && !this.map.containsKey(key)) {
      value = this.parent.getInternal(key);
    }
    return value;
  }

  @Override
  public boolean containsKey(String name) {

    if (containsKeyInternal(name)) {
      return true;
    }
    String normalizedName = CobiGenVariableDefinition.normalizeName(name);
    if (normalizedName.equals(name)) {
      return false;
    }
    return containsKeyInternal(normalizedName);
  }

  /**
   * @param key the raw key to check.
   * @return {@code true} if {@link #containsKeyInternal(String) this model itself contains} the given {@code key} or
   *         one of its parents does, {@code false} otherwise.
   */
  private boolean containsKeyInternal(String key) {

    if (this.map.containsKey(key)) {
      return true;
    }
    if (this.parent != null) {
      return this.parent.containsKeyInternal(key);
    }
    return false;
  }

  /**
   * @param name the name of the variable to set.
   * @param value the value of the variable to set.
   * @return the previous value that has been replaced because it has the same
   *         {@link CobiGenVariableDefinition#normalizeName(String) normalized name} or {@code null} if this variable
   *         has been initially defined in this model.
   * @see java.util.Map#put(Object, Object)
   */
  @Override
  public Object put(String name, Object value) {

    return put(name, value, false);
  }

  /**
   * @param name the name of the variable to set.
   * @param value the value of the variable to set.
   * @param skipOriginalMap - {@code true} to skip putting the value also into {@link #getOriginalMap() original map}
   *        (e.g. from Constructor), {@code false} otherwise.
   * @return the previous value that has been replaced because it has the same
   *         {@link CobiGenVariableDefinition#normalizeName(String) normalized name} or {@code null} if this variable
   *         has been initially defined in this model.
   * @see java.util.Map#put(Object, Object)
   */
  protected Object put(String name, Object value, boolean skipOriginalMap) {

    String normalizedName = CobiGenVariableDefinition.normalizeName(name);
    Object old = this.map.put(normalizedName, value);
    if (!skipOriginalMap) {
      Map<String, Object> originalMap = getOriginalMap();
      if (originalMap != this.map) {
        originalMap.put(name, value);
      }
    }
    return old;
  }

  /**
   * @param modelAsMap the raw model as plain {@link Map}.
   * @see Map#putAll(Map)
   */
  public void putAll(Map<String, Object> modelAsMap) {

    putAll(modelAsMap, false);
  }

  /**
   * @param modelAsMap the raw model as plain {@link Map}.
   * @param skipOriginalMap - {@code true} to skip putting the value also into {@link #getOriginalMap() original map}
   *        (e.g. from Constructor), {@code false} otherwise.
   * @see #putAll(Map)
   */
  protected void putAll(Map<String, Object> modelAsMap, boolean skipOriginalMap) {

    for (Entry<String, Object> entry : modelAsMap.entrySet()) {
      String name = entry.getKey();
      Object value = entry.getValue();
      put(name, value, skipOriginalMap);
    }
  }

  @Override
  public String resolve(String string, char replacementForDot, VariableSyntax syntax) {

    Pattern pattern = syntax.getPattern();
    Matcher matcher = pattern.matcher(string);
    if (!matcher.find()) {
      return string;
    }
    StringBuilder sb = new StringBuilder(string.length());
    do {
      String variableName = syntax.getVariable(matcher);
      String variableValue = getVariable(variableName);
      String replacement;
      if (variableValue == null) {
        LOG.warn("Undefined variable {}", variableName);
        replacement = matcher.group();
      } else {
        replacement = syntax.resolve(variableValue, matcher, variableName);
      }
      matcher.appendReplacement(sb, replacement);
    } while (matcher.find());
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * @return this model as {@link Map}. Further changes to this model or one of its ancestors are NOT reflected by the
   *         returned {@link Map}. This is an expensive operation that may only be called to pass the model to a
   *         template-engine that cannot support {@link CobiGenModel} directly.
   */
  public Map<String, Object> asMap() {

    HashMap<String, Object> result = new HashMap<>(this.map.size());
    asMap(result);
    return result;
  }

  /**
   * @see #asMap()
   * @param result the {@link Map} to populate.
   */
  private void asMap(Map<String, Object> result) {

    if (this.parent != null) {
      this.parent.asMap(result);
    }
    result.putAll(getOriginalMap());
  }

  @Override
  public CobiGenModel copy() {

    Map<String, Object> newMap = new HashMap<>(this.map.size());
    AbstractCobiGenModel model = this;
    while (model != null) {
      newMap.putAll(model.getOriginalMap());
      model = model.parent;
    }
    return new CobiGenModelDefault(newMap);
  }

}
