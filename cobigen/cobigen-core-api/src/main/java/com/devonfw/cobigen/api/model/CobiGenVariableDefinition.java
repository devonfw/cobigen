package com.devonfw.cobigen.api.model;

import java.util.function.Supplier;

import io.github.mmm.base.text.CaseSyntax;

/**
 * Class for the definition of a variable of the {@link CobiGenModel}.
 *
 * @param <T> the {@link #getType() value type}.
 */
public class CobiGenVariableDefinition<T> {

  @SuppressWarnings("rawtypes")
  private static final Supplier NO_DEFAULT_VALUE = () -> null;

  private final String name;

  private final String[] synonyms;

  private final Class<T> type;

  private final Supplier<T> defaultValueSupplier;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() variable name}.
   * @param type the {@link #getType() variable type}.
   */
  public CobiGenVariableDefinition(String name, Class<T> type) {

    this(name, type, NO_DEFAULT_VALUE);
  }

  /**
   * The constructor.
   *
   * @param name the {@link #getName() variable name}.
   * @param type the {@link #getType() variable type}.
   * @param defaultValueSupplier the {@link Supplier} for the {@link #getDefaultValue() default value}.
   * @param synonyms the optional synonyms for the {@link #getName() variable name}. Synonyms are legacy names that are
   *        still supported for backwards compatibility. They are considered deprecated in case they are still used.
   */
  public CobiGenVariableDefinition(String name, Class<T> type, Supplier<T> defaultValueSupplier, String... synonyms) {

    super();
    this.name = name;
    this.type = type;
    this.defaultValueSupplier = defaultValueSupplier;
    this.synonyms = synonyms;
  }

  @SuppressWarnings("unchecked")
  private static <V> Class<V> getType(V value) {

    if (value == null) {
      return null;
    }
    return (Class<V>) value.getClass();
  }

  /**
   * @return the name of the variable.
   * @see CobiGenModel#get(String)
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return the {@link Class} reflecting the value of the variable.
   */
  public Class<T> getType() {

    return this.type;
  }

  /**
   * @return the optional default value to use if the variable is not explicitly configured.
   */
  public T getDefaultValue() {

    return this.defaultValueSupplier.get();
  }

  /**
   * @param model the {@link CobiGenModel}.
   * @return the value of the variable from the given {@link CobiGenModel}. If undefined the {@link #getDefaultValue()
   *         default value} is returned, what may also be {@code null}.
   */
  @SuppressWarnings("unchecked")
  public T getValue(CobiGenModel model) {

    Object value = model.get(this.name);
    if (value == null) {
      return getDefaultValue();
    }
    if (this.type == null) {
      return (T) value;
    } else {
      return this.type.cast(value);
    }
  }

  /**
   * @param model the {@link CobiGenModel}.
   * @param value the new value of the variable for the given {@link CobiGenModel}.
   * @return the previous value that has been replaced because it has the same {@link #normalizeName(String) normalized}
   *         {@link #getName() name} or {@code null} if this variable has been initially defined in the given
   *         {@code model}.
   */
  public Object setValue(CobiGenModel model, T value) {

    Object old = model.put(this.name, value);
    for (String synonym : this.synonyms) {
      model.put(synonym, value);
    }
    return old;
  }

  /**
   * @param name the variable name to normalize.
   * @return the normalized name.
   */
  public static String normalizeName(String name) {

    return CaseSyntax.normalizeExample(name);
  }

  @Override
  public String toString() {

    return this.name;
  }

  /**
   * @param <T> type of the variable value.
   * @param name the {@link #getName() variable name}.
   * @param type the {@link #getType() type} of the {@link #getValue(CobiGenModel) variable value}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  public static <T> CobiGenVariableDefinition<T> ofType(String name, Class<T> type) {

    return new CobiGenVariableDefinition<>(name, type);
  }

  /**
   * @param <T> type of the variable value.
   * @param name the {@link #getName() variable name}.
   * @param defaultValue the {@link #getDefaultValue() default value}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  public static <T> CobiGenVariableDefinition<T> ofDefaultValue(String name, T defaultValue) {

    return new CobiGenVariableDefinition<>(name, getType(defaultValue), () -> defaultValue);
  }

  /**
   * @param name the {@link #getName() variable name}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  public static CobiGenVariableDefinition<String> ofString(String name) {

    return ofType(name, String.class);
  }

  /**
   * @param name the {@link #getName() variable name}.
   * @param defaultValue the {@link #getDefaultValue() default value}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  public static CobiGenVariableDefinition<String> ofString(String name, String defaultValue) {

    return new CobiGenVariableDefinition<>(name, String.class, () -> defaultValue);
  }

  /**
   * @param name the {@link #getName() variable name}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static CobiGenVariableDefinition<Class<?>> ofClass(String name) {

    return new CobiGenVariableDefinition(name, Class.class);
  }

  /**
   * @param name the {@link #getName() variable name}.
   * @param defaultValue the {@link #getDefaultValue() default value}.
   * @return the new {@link CobiGenVariableDefinition} created from the given arguments.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static CobiGenVariableDefinition<Class<?>> ofClass(String name, Class<?> defaultValue) {

    return new CobiGenVariableDefinition(name, Class.class, () -> defaultValue);
  }

}
