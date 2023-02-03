package com.devonfw.cobigen.api.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central definition of {@link CobiGenVariableDefinition}s.
 */
public final class CobiGenVariableDefinitions {

  /** {@link CobiGenVariableDefinition} for the Java {@link Class} reflecting the type of the input. */
  public static final CobiGenVariableDefinition<Class<?>> JAVA_TYPE = CobiGenVariableDefinition.ofClass("classObject");

  /** {@link CobiGenVariableDefinition} for the fields of {@link #JAVA_TYPE}. */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final CobiGenVariableDefinition<List<Map<String, Object>>> JAVA_FIELDS = new CobiGenVariableDefinition(
      "fields", List.class, () -> new ArrayList<>(), "attributes");

  /** {@link CobiGenVariableDefinition} for the variables. */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final CobiGenVariableDefinition<Map<String, String>> VARIABLES = new CobiGenVariableDefinition(
      "variables", Map.class, () -> new HashMap<>());

  /** The {@link java.lang.reflect.Field#getName()}. */
  public static final CobiGenVariableDefinition<Field> FIELD = CobiGenVariableDefinition.ofType("field", Field.class);

  /** The {@link java.lang.reflect.Field#getName()}. */
  public static final CobiGenVariableDefinition<String> FIELD_NAME = CobiGenVariableDefinition.ofString("fieldName");

  /** The {@link java.lang.reflect.Field#getType()}. */
  public static final CobiGenVariableDefinition<Type> FIELD_TYPE = CobiGenVariableDefinition.ofType("fieldtype",
      Type.class);

  /** The {@link java.lang.reflect.Field#getType()}. */
  public static final CobiGenVariableDefinition<String> FIELD_TYPE_SIMPLE_NAME = CobiGenVariableDefinition
      .ofString("fieldtypeSimpeName");

}
