package com.devonfw.cobigen.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.template.out.CobiGenOutput;

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

  /** The entity name for generations from an entity as input (e.g. "MyExample" for "MyExampleEntity" as input). */
  public static final CobiGenVariableDefinition<String> ENTITY_NAME = CobiGenVariableDefinition.ofString("EntityName");

  /** The root package namespace (e.g. "com.customer.app"). */
  public static final CobiGenVariableDefinition<String> ROOT_PACKAGE = CobiGenVariableDefinition
      .ofString("RootPackage");

  /** The component name (e.g. "mycomponent"). */
  public static final CobiGenVariableDefinition<String> COMPONENT_NAME = CobiGenVariableDefinition
      .ofString("Component");

  /** The detail sub-package(s) (typically empty but can be anything like "special" or "detail.extra"). */
  public static final CobiGenVariableDefinition<String> DETAIL_NAME = CobiGenVariableDefinition.ofString("Detail");

  /** The scope package segment (typically empty but can be the scope as "api", "base", or "impl"). */
  public static final CobiGenVariableDefinition<String> SCOPE_NAME = CobiGenVariableDefinition.ofString("Scope");

  /**
   * The module path (typically empty but can be the relativ path to the module of a multi-module project where to put
   * the generated code).
   */
  public static final CobiGenVariableDefinition<String> MODULE_PATH = CobiGenVariableDefinition.ofString("Module");

  /** The {@link CobiGenOutput} of the currently generated artifact (template). */
  public static final CobiGenVariableDefinition<CobiGenOutput> OUT = CobiGenVariableDefinition.ofType("out",
      CobiGenOutput.class);

}
