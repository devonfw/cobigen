package com.devonfw.cobigen.templates.devon4j.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.devonfw.cobigen.templates.devon4j.constants.Field;
import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;

/**
 * A class for shared devon4j specific functions in the templates
 *
 */
@SuppressWarnings("restriction")
public class DevonfwUtil {

  /**
   * Check whether the given 'canonicalType' is a devon4j Entity, which is declared in the given 'component'
   *
   * @param canonicalType the type name
   * @param component the component name
   * @return true iff the canonicalType is a devon Entity
   */
  public boolean isEntityInComponent(String canonicalType, String component) {

    return canonicalType.matches(String.format(".+%1$s\\.dataaccess\\.api\\.[A-Za-z0-9]+Entity(<.*)?", component));
  }

  /**
   * Check whether the given 'canonicalType' is declared in the given 'component'
   *
   * @param canonicalType the type name
   * @param component the component name
   * @return true iff the canonicalType is inside the given component
   */
  public boolean isTypeInComponent(String canonicalType, String component) {

    return canonicalType.matches(String.format("%1$s.[A-Za-z0-9]+(<.*)?", component));
  }

  /**
   * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object
   * reference or a direct ID getter
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param component the devon4j component name
   * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} + '()' with
   *         capitalize=true
   */
  public String resolveIdGetter(Map<String, Object> field, boolean byObjectReference, String component) {

    // If field comes from an UML file
    if (field.getClass().toGenericString().contains("freemarker.ext.beans.HashAdapter")) {
      DeferredElementNSImpl umlNode = (DeferredElementNSImpl) field;
      return resolveIdGetter(umlNode, byObjectReference, component);
    }
    return "get" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component) + "()";
  }

  /**
   * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object
   * reference or a direct ID getter
   *
   * This method is used when the field parameter comes from an UML file. The name and type of the attributes must be
   * pre-processed for later inserting them inside the HashMap.
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param component the devon4j component name
   * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} + '()' with
   *         capitalize=true
   */
  public String resolveIdGetter(DeferredElementNSImpl field, boolean byObjectReference, String component) {

    HashMap nodeHash = new HashMap<>();

    // Putting the name of the attribute to the hash
    nodeHash.put(Field.NAME.toString(), field.getAttribute("name"));

    // Putting the type of the attribute to the hash
    NodeList childs = field.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      // Retrieve "type" tag
      if (childs.item(i).getNodeName().equals("type")) {
        NamedNodeMap attrs = childs.item(i).getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Attr attribute = (Attr) attrs.item(j);
          // Try to find the attribute that contains the type
          if (attribute.getName().equals("xmi:idref")) {
            nodeHash.put(Field.TYPE.toString(), attribute.getName().replace("EAJava_", ""));
          }
        }
      }
    }
    return "get" + resolveIdVariableNameOrSetterGetterSuffix(nodeHash, byObjectReference, true, component) + "()";

  }

  /**
   * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object
   * reference or a direct ID getter
   *
   * @param pojoClass the class object of the pojo
   * @param fieldMap the field mapping
   * @param byObjectReference boolean
   * @param component the devon4j component name
   * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} + '()' with
   *         capitalize=true
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
      String component) throws NoSuchFieldException, SecurityException {

    return "get" + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component)
        + "()";
  }

  /**
   * same as {@link #resolveIdGetter(Map, boolean, String)} but with byObjectReference=false and component=""
   *
   * @param field the field
   * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()' with
   *         capitalize=true
   */
  public String resolveIdGetter(Map<String, Object> field) {

    return this.resolveIdGetter(field, false, "");
  }

  /**
   * same as {@link #resolveIdGetter(Class,Map,boolean,String)} but with byObjectReference=false and component=""
   *
   * @param pojoClass the class object of the pojo
   * @param fieldMap the field mapping
   * @return 'get' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} + '()' with
   *         capitalize=true
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdGetter(Class<?> pojoClass, Map<String, Object> fieldMap)
      throws NoSuchFieldException, SecurityException {

    return resolveIdGetter(pojoClass, fieldMap, false, "");
  }

  /**
   * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object
   * reference or a direct ID setter. In contrast to resolveIdGetter, this function does not generate the function
   * parenthesis to enable parameter declaration.
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param component the devon4j component name
   * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
   *         capitalize=true
   */
  public String resolveIdSetter(Map<String, Object> field, boolean byObjectReference, String component) {

    return "set" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component);
  }

  /**
   * same as {@link #resolveIdSetter(Map, boolean, String)} but with byObjectReference=false and component=""
   *
   * @param field the field
   * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)} with
   *         capitalize=true
   */
  public String resolveIdSetter(Map<String, Object> field) {

    return this.resolveIdSetter(field, false, "");
  }

  /**
   * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object
   * reference or a direct ID setter. In contrast to resolveIdGetter, this function does not generate the function
   * parenthesis to enable parameter declaration.
   *
   * @param pojoClass the class object of the pojo
   * @param fieldMap the field mapping
   * @param byObjectReference boolean
   * @param component the devon4j component name
   * @return 'set'+ {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with capitalize=true
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap, boolean byObjectReference,
      String component) throws NoSuchFieldException, SecurityException {

    return "set" + resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, byObjectReference, true, component);
  }

  /**
   * same as {@link #resolveIdSetter(Class,Map,boolean,String)} but with byObjectReference=false and component=""
   *
   * @param pojoClass the class object of the pojo
   * @param fieldMap the field mapping
   * @return 'set' + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map,boolean,boolean,String)} with capitalize=true
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdSetter(Class<?> pojoClass, Map<String, Object> fieldMap)
      throws NoSuchFieldException, SecurityException {

    return resolveIdSetter(pojoClass, fieldMap, false, "");
  }

  /**
   * Determines the variable name for the id value of the 'field'
   *
   * @param field the field
   * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, boolean, String)}) with
   *         byObjectReference=false, capitalize=false and component=""
   */
  public String resolveIdVariableName(Map<String, Object> field) {

    // the component is passed down as an empty string since byObjectReference is false and therefore the
    // component is
    // never touched
    return resolveIdVariableNameOrSetterGetterSuffix(field, false, false, "");
  }

  /**
   * Determines the variable name for the id value of the specified field in the pojo
   *
   * @param pojoClass the class object of the pojo
   * @param fieldMap the field mapping
   * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Class, Map, boolean, boolean, String)}) with
   *         byObjectReference=false, capitalize=false and component=""
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdVariableName(Class<?> pojoClass, Map<String, Object> fieldMap)
      throws NoSuchFieldException, SecurityException {

    // the component is passed down as an empty string since byObjectReference is false and therefore the
    // component is
    // never touched
    return resolveIdVariableNameOrSetterGetterSuffix(pojoClass, fieldMap, false, false, "");
  }

  /**
   * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the
   * ID via an object reference or a direct ID setter/getter
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param capitalize if the field name should be capitalized
   * @param component the devon4j component. Only needed if $byObjectReference is true
   * @return idVariable name or getter/setter suffix
   */
  public String resolveIdVariableNameOrSetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
      boolean capitalize, String component) {

    String fieldName = (String) field.get(Field.NAME.toString());
    if (capitalize) {
      fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    String suffix = "";

    String fieldType = (String) field.get(Field.TYPE.toString());
    String fieldCType = (String) field.get(Field.CANONICAL_TYPE.toString());
    if (fieldType.contains("Entity")) {
      if (fieldCType.startsWith("java.util.List") || fieldCType.startsWith("java.util.Set")) {
        suffix = "Ids";
        if (fieldName.endsWith("s")) {
          // Assume trailing 's' as indicator for a plural
          fieldName = fieldName.substring(0, fieldName.length() - 1);
        }
      } else {
        suffix = "Id";
      }
      if (byObjectReference && isTypeInComponent(fieldCType, component)) {
        // direct references for Entities in same component, so get id of the object reference
        suffix = "().getId";
      }
    }

    return fieldName + suffix;

  }

  /**
   * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the
   * ID via an object reference or a direct ID setter/getter
   *
   * @param pojoClass the {@link Class} object of the pojo
   * @param fieldMap the field mapping
   * @param byObjectReference boolean
   * @param capitalize if the field name should be capitalized
   * @param component the devon4j component. Only needed if byObjectReference is true
   * @return idVariable name or getter/setter suffix
   * @throws NoSuchFieldException indicating a severe problem in the used model
   * @throws SecurityException if the field cannot be accessed for any reason
   */
  public String resolveIdVariableNameOrSetterGetterSuffix(Class<?> pojoClass, Map<String, Object> fieldMap,
      boolean byObjectReference, boolean capitalize, String component) throws NoSuchFieldException, SecurityException {

    String resultName = (String) fieldMap.get(Field.NAME.toString());
    if (capitalize) {
      resultName = resultName.substring(0, 1).toUpperCase() + resultName.substring(1);
    }
    String suffix = "";
    String fieldType = (String) fieldMap.get(Field.TYPE.toString());
    String fieldName = (String) fieldMap.get(Field.NAME.toString());
    if (fieldType.contains("Entity")) {
      if (Collection.class.isAssignableFrom(pojoClass.getDeclaredField(fieldName).getType())) {
        suffix = "Ids";
        if (resultName.endsWith("s")) {
          // Assume trailing 's' as indicator for a plural
          resultName = resultName.substring(0, resultName.length() - 1);
        }
      } else {
        suffix = "Id";
      }
      if (byObjectReference
          && isEntityInComponent(pojoClass.getDeclaredField(fieldName).getType().getName(), component)) {
        // direct references for Entities in same component, so get id of the object reference
        suffix = "().getId";
      }
    }

    return resultName + suffix;

  }

  /**
   * Returns the argument type of the list or set from a field. If the string contains "Entity" it will remove that
   * part. For example, if we have a List &lt;SampleEntity&gt; it will return "Sample"
   *
   * @param field the field
   * @param pojoClass the object class of the Entity that contains the field
   * @return fieldType argument of the list
   * @throws SecurityException if field type could not accessed
   * @throws NoSuchFieldException if field could not be found
   */
  public String getListArgumentType(Map<String, Object> field, Class<?> pojoClass)
      throws NoSuchFieldException, SecurityException {

    JavaUtil javaUtil = new JavaUtil();

    String fieldType = (String) field.get(Field.TYPE.toString());
    String fieldName = (String) field.get(Field.NAME.toString());

    if (fieldType.contains("Entity")) {
      if (javaUtil.isCollection(pojoClass, fieldName)) {

        fieldType = fieldType.replace("Entity", "");
        // Regex: Extracts the argument type of the list 'List<type>' => type
        String regex = "(?<=\\<).+?(?=\\>)";
        Pattern pattern = Pattern.compile(regex);
        Matcher regexMatcher = pattern.matcher(fieldType);

        if (regexMatcher.find()) {
          fieldType = regexMatcher.group(0);
        }
      }
    }
    return fieldType;

  }

  /**
   * Converts all occurrences of devon4j Entity types in the given 'field' simple type (possibly generic) to Longs
   *
   * @param field the field
   * @return the field type as String. If field type contains 'Entity' the result is Long
   */
  public String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

    String fieldType = (String) field.get(Field.TYPE.toString());
    if (fieldType.endsWith("Entity")) {
      fieldType = fieldType.replaceAll("[^<>]+Entity", "Long");
    }
    return fieldType;
  }

  /**
   * If the string last character is an 's', then it gets removed
   *
   * @param targetClassName string to remove plural
   * @return string without 's'
   */
  public String removePlural(String targetClassName) {

    if (targetClassName.charAt(targetClassName.length() - 1) == 's') {
      targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
    }
    return targetClassName;
  }

  /**
   * Checks whether the operation with the given ID corresponds to any standard CRUD method name.
   *
   * @param operationId operation ID interpreted as method name
   * @param entityName entity name to check standard CRUD methods for
   * @return <code>true</code> if the operation ID maps any standard CRUD method name, <code>false</code> otherwise
   */
  public boolean isCrudOperation(String operationId, String entityName) {

    if (operationId == null) {
      return false;
    }
    String opIdLowerCase = operationId.toLowerCase();
    String entityNameLowerCase = entityName.toLowerCase();
    if (opIdLowerCase.contains(entityNameLowerCase)) {
      return opIdLowerCase.equals("find" + entityNameLowerCase)
          || opIdLowerCase.equals("find" + entityNameLowerCase + "Etos")
          || opIdLowerCase.equals("delete" + entityNameLowerCase) || opIdLowerCase.equals("save" + entityNameLowerCase);
    } else {
      return false;
    }
  }

  /**
   * Converts the given media type to the spring Java enum value
   *
   * @param mediaType to be converted
   * @return the spring enum value representing the given media type
   */
  public String getSpringMediaType(String mediaType) {

    switch (mediaType) {
      case "application/xml":
        return "APPLICATION_XML_VALUE";
      case "application / x-www-form-urlencoded":
        return "APPLICATION_FORM_URLENCODED_VALUE";
      case "multipart/form-data":
        return "MULTIPART_FORM_DATA_VALUE";
      case "text/plain":
        return "TEXT_PLAIN_VALUE";
      case "text/html":
        return "TEXT_HTML_VALUE";
      case "application/pdf":
        return "APPLICATION_PDF_VALUE";
      case "image/png":
        return "IMAGE_PNG_VALUE";
      default:
        return "APPLICATION_JSON_VALUE";
    }
  }
}