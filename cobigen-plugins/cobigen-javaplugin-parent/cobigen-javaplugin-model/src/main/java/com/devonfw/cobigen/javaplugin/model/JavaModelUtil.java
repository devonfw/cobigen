package com.devonfw.cobigen.javaplugin.model;

import java.util.List;
import java.util.Map;

/**
 * * The {@link JavaModelUtil} class provides helper functions to access the given model.
 *
 * @author fkreis (25.09.2014)
 */
public class JavaModelUtil {

  /**
   * Returns the model root-element ({@link ModelConstant#MODEL_ROOT})
   *
   * @param model raw model
   * @return the model root-element ({@link ModelConstant#MODEL_ROOT})
   */
  public static Map<String, Object> getRoot(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.MODEL_ROOT);
    return pojoMap;
  }

  /**
   * Returns the model annotations-element ({@link ModelConstant#ANNOTATIONS})
   *
   * @param model raw model
   * @return the model annotations-element ({@link ModelConstant#ANNOTATIONS})
   */
  public static Map<String, Object> getAnnotations(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    Map<String, Object> annotations = (Map<String, Object>) model.get(ModelConstant.ANNOTATIONS);
    return annotations;
  }

  /**
   * Returns the list of all field models ({@link ModelConstant#FIELDS})
   *
   * @param model raw model
   * @return the list of all field models
   */
  public static List<Map<String, Object>> getFields(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> fields = (List<Map<String, Object>>) getRoot(model).get(ModelConstant.FIELDS);
    return fields;
  }

  /**
   * Returns the field model with the given field name from model
   *
   * @param model raw model
   * @param fieldName field name to be retrieved
   * @return the field model for the given field name if such a field exists, otherwise null.
   */
  public static Map<String, Object> getField(Map<String, Object> model, String fieldName) {

    Map<String, Object> targetField = null;
    for (Map<String, Object> field : getFields(model)) {
      if (fieldName.equals(field.get(ModelConstant.NAME))) {
        targetField = field;
        break;
      }
    }
    return targetField;
  }

  /**
   * Returns the list of all field models ({@link ModelConstant#METHOD_ACCESSIBLE_FIELDS})
   *
   * @param model raw model
   * @return the list of all field models
   * @author mbrunnli (17.11.2014)
   */
  public static List<Map<String, Object>> getMethodAccessibleFields(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> fields = (List<Map<String, Object>>) getRoot(model)
        .get(ModelConstant.METHOD_ACCESSIBLE_FIELDS);
    return fields;
  }

  /**
   * Returns the field model with the given field name from model
   *
   * @param model raw model
   * @param fieldName field name to be retrieved
   * @return the field model for the given field name if such a field exists, otherwise null.
   * @author mbrunnli (17.11.2014)
   */
  public static Map<String, Object> getMethodAccessibleField(Map<String, Object> model, String fieldName) {

    Map<String, Object> targetField = null;
    for (Map<String, Object> field : getMethodAccessibleFields(model)) {
      if (fieldName.equals(field.get(ModelConstant.NAME))) {
        targetField = field;
        break;
      }
    }
    return targetField;
  }

  /**
   * Returns the model's super type element
   *
   * @param model raw model
   * @return the model's super type element
   */
  public static Map<String, Object> getExtendedType(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    Map<String, Object> supertype = (Map<String, Object>) getRoot(model).get(ModelConstant.EXTENDED_TYPE);
    return supertype;
  }

  /**
   * Returns the model's implemented types element, which is a list consisting of the interface models
   *
   * @param model raw model
   * @return the model's interfaces element
   */
  public static List<Map<String, Object>> getImplementedTypes(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> interfaces = (List<Map<String, Object>>) getRoot(model)
        .get(ModelConstant.IMPLEMENTED_TYPES);
    return interfaces;
  }

  /**
   * Returns the interface model for the given interface name if such an interface exists, otherwise null.
   *
   * @param model raw model
   * @param fqn full qualified name, which identifies the specific interface
   * @return the interface model
   */
  public static Map<String, Object> getImplementedType(Map<String, Object> model, String fqn) {

    Map<String, Object> implType = null;
    for (Map<String, Object> typ : getImplementedTypes(model)) {
      if (fqn.equals(typ.get(ModelConstant.CANONICAL_NAME))) {
        implType = typ;
        break;
      }
    }
    return implType;
  }

  /**
   * Returns the model's methods element, which is a list consisting of the method models
   *
   * @param model raw model
   * @return the model's methods element
   */
  public static List<Map<String, Object>> getMethods(Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> methods = (List<Map<String, Object>>) getRoot(model).get(ModelConstant.METHODS);
    return methods;
  }

  /**
   * Returns the method model of the model with the given methodName.
   *
   * @param model raw model
   * @param methodName method name to search for
   * @return method model for the method with the given name or <code>null</code> if no method with the given name
   *         found.
   * @author mbrunnli (30.01.2015)
   */
  public static Map<String, Object> getMethod(Map<String, Object> model, String methodName) {

    List<Map<String, Object>> methods = getMethods(model);
    for (Map<String, Object> method : methods) {
      if (methodName.equals(method.get(ModelConstant.NAME))) {
        return method;
      }
    }
    return null;
  }

  /**
   * Returns the model's name element, which is the simple name of the input class
   *
   * @param model raw model
   * @return the model's name element
   */
  public static String getName(Map<String, Object> model) {

    String name = (String) getRoot(model).get(ModelConstant.NAME);
    return name;
  }

  /**
   * Returns the model's canonicalName element, which is the full qualified name of the input class
   *
   * @param model raw model
   * @return the model's canonicalName element
   */
  public static String getCanonicalName(Map<String, Object> model) {

    String cName = (String) getRoot(model).get(ModelConstant.CANONICAL_NAME);
    return cName;
  }

  /**
   * Returns the JavaDoc model of the documented element model passed.
   *
   * @param documentedElementModel element model, from which the javaDoc model should be retrieved.
   * @return the JavaDoc model or <code>null</code> if not available.
   * @author mbrunnli (30.01.2015)
   */
  public static Map<String, Object> getJavaDocModel(Map<String, Object> documentedElementModel) {

    Object o = documentedElementModel.get(ModelConstant.JAVADOC);
    if (o != null) {
      @SuppressWarnings("unchecked")
      Map<String, Object> javaDocModel = (Map<String, Object>) o;
      return javaDocModel;
    } else {
      return null;
    }
  }
}
