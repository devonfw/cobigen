package com.devonfw.cobigen.api.model;

import java.lang.reflect.Type;

/**
 * Interface for the model of CobiGen. It is similar to {@link java.util.Map} or {@link java.util.Properties}. However,
 * {@link #get(String) retrieval} is case-insensitive. Also is provides the ability to {@link #resolve(String) resolve}
 * variables from any given {@link String} replacing them with the values from this model according to different
 * variants of supported {@link VariableSyntax} including case-conversions and other advanced features. Also a
 * {@link CobiGenModel} may inherit from a parent model.
 */
public interface CobiGenModel {

  /**
   * @param variableName the {@link CobiGenVariableDefinition#getName() name} of the variable to resolve.
   * @return the value of the requested variable or {@code null} if not defined.
   * @see java.util.Map#get(Object)
   */
  Object get(String variableName);

  /**
   * @param <T> type of the requested variable.
   * @param variable the {@link CobiGenVariableDefinition}.
   * @return the value of the specified variable.
   * @see CobiGenVariableDefinition#getValue(CobiGenModel)
   */
  default <T> T get(CobiGenVariableDefinition<T> variable) {

    return variable.getValue(this);
  }

  /**
   * @param variableName the name of the variable to resolve.
   * @return the value of the requested variable or {@code null} if not defined.
   * @throws IllegalArgumentException if the requested variable exists but its {@link #get(String) value} is not a
   *         {@link String}.
   * @see #get(String)
   */
  default String getVariable(String variableName) throws IllegalArgumentException {

    Object value = get(variableName);
    if (value == null) {
      return null;
    } else if ((value instanceof CharSequence) || (value instanceof Number) || (value instanceof Boolean)) {
      return value.toString();
    } else if (value instanceof Class) {
      Class<?> type = (Class<?>) value;
      if ("java.lang".equals(type.getPackageName())) {
        return type.getSimpleName();
      } else {
        return type.getName();
      }
    } else if (value instanceof Type) {
      return ((Type) value).getTypeName();
    } else {
      throw new IllegalArgumentException("The variable '" + variableName
          + "' was requested as String but is actually of type " + value.getClass().getName());
    }
  }

  /**
   * @param string the {@link String} where to resolve all variables.
   * @return the given {@code string} with all variables (e.g. <code>${variableName#uncapfirst}</code> or
   *         <code>X_VariableName_X</code>) replaced with those defined by this model.
   */
  default String resolve(String string) {

    return resolve(string, '.');
  }

  /**
   * @param string the {@link String} where to resolve all variables.
   * @param replacementForDot the character used as replacement for the dot character ('.') or '\0' for no replacement
   *        (remove dots according to case syntax).
   * @return the given {@code string} with all variables (e.g. <code>${variableName#uncapfirst}</code> or
   *         <code>X_VariableName_X</code>) replaced with those defined by this model.
   */
  default String resolve(String string, char replacementForDot) {

    String result = string;
    for (VariableSyntax syntax : VariableSyntax.values()) {
      result = resolve(result, replacementForDot, syntax);
    }
    return result;
  }

  /**
   * @param string the {@link String} where to resolve all variables.
   * @param replacementForDot the character used as replacement for the dot character ('.') or '\0' for no replacement
   *        (remove dots according to case syntax).
   * @param syntax the {@link VariableSyntax}.
   * @return the given {@code string} with all variables (e.g. <code>${variableName#uncapfirst}</code> or
   *         <code>X_VariableName_X</code>) replaced with those defined by this model.
   */
  String resolve(String string, char replacementForDot, VariableSyntax syntax);

  /**
   * @param variableName the name of the variable to check.
   * @return {@code true} if the requested variable is defined, {@code false} otherwise.
   * @see java.util.Map#containsKey(Object)
   */
  boolean containsKey(String variableName);

  /**
   * @param name the name of the variable to set.
   * @param value the value of the variable to set.
   * @return the previous value that has been replaced because it has the same normalized
   *         {@link CobiGenVariableDefinition#getName() name} or {@code null} if this variable has been initially
   *         defined in this model.
   * @see java.util.Map#put(Object, Object)
   */
  Object put(String name, Object value);

  /**
   * @param <T> type of the variable value.
   * @param variableDefinition the {@link CobiGenVariableDefinition} of the variable to set.
   * @param value the value of the variable to set.
   * @return the previous value that has been replaced because it has the same normalized name or {@code null} if this
   *         variable has been initially defined in this model.
   */
  default <T> Object put(CobiGenVariableDefinition<T> variableDefinition, T value) {

    return variableDefinition.setValue(this, value);
  }

  /**
   * @return the parent {@link CobiGenModel model} to inherit from or {@code null} if this is the root model.
   */
  CobiGenModel getParent();

  /**
   * @return a new {@link CobiGenModel} instance that is a flat copy of this model.
   */
  CobiGenModel copy();

}
