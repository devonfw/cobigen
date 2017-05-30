package utils;

import org.apache.commons.lang3.ClassUtils;

/**
 * @author sholzer
 *
 */
public class JavaUtil {

  /**
   * The constructor.
   */
  public JavaUtil() {
    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Returns the Object version of a Java primitive or the input if the input isn't a java primitive
   *
   * @param simpleType String
   * @return the corresponding object wrapper type simple name of the input if the input is the name of a primitive java
   *         type. The input itself if not. (e.g. "int" results in "Integer")
   * @throws ClassNotFoundException should not occur.
   */
  public String boxJavaPrimitives(String simpleType) throws ClassNotFoundException {

    if (equalsJavaPrimitive(simpleType)) {
      return ClassUtils.primitiveToWrapper(ClassUtils.getClass(simpleType)).getSimpleName();
    } else {
      return simpleType;
    }

  }

  /**
   * Checks if the given type is a Java primitive
   *
   * @param simpleType the type to be checked
   * @return true iff simpleType is a Java primitive
   */
  public boolean equalsJavaPrimitive(String simpleType) {

    try {
      return ClassUtils.getClass(simpleType).isPrimitive();
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * Checks if the given type is a Java primitive or a Java primitive array
   *
   * @param simpleType the Type name to be checked
   * @return true iff {@link #equalsJavaPrimitive(String)} is true or if simpleType is an array with a primitive component
   */
  public boolean equalsJavaPrimitiveIncludingArrays(String simpleType) {
    Class<?> klasse;
    
    try{
      klasse = ClassUtils.getClass(simpleType).getComponentType();
    }catch(ClassNotFoundException e){
      return false;
    }
    return equalsJavaPrimitive(simpleType) || (klasse!=null && klasse.isPrimitive());
    
  }

  /**
   * Returns a cast statement for a given (java primitive, variable name) pair or nothing of the type isn't a java
   * primitive
   *
   * @param simpleType Java Type
   * @param varName Variable name
   * @return String either of the form '((Java Primitive Object Type)varName)' if simpleType is a primitive or the emtpy
   *         String otherwise
   */
  public String boxJavaPrimitives(String simpleType, String varName) throws ClassNotFoundException {

    if (equalsJavaPrimitive(simpleType)) {
      return String.format("((%1$s)%2$s)", boxJavaPrimitives(simpleType), varName);
    } else {
      return "";
    }

  }

  /**
   * returns the sencha type associated with a Java primitive or {@link String} or {@link java.util.Date}
   *
   * @param simpleType :{@link String} the type to be parsed
   * @return the corresponding sencha type or 'auto' otherwise
   * @throws ClassNotFoundException should not occur.
   */
  public String getSenchaType(String simpleType) {

    switch (simpleType) {
    case "boolean":
    case "Boolean":
      return "boolean";
    case "short":
    case "Short":
    case "int":
    case "Integer":
    case "long":
    case "Long":
      return "int";
    case "float":
    case "Float":
    case "double":
    case "Double":
      return "float";
    case "char":
    case "Character":
    case "String":
      return "string";
    case "Date":
      return "date";
    default:
      return "auto";
    }
  }

  public String getExtType(String simpleType) {

    switch (simpleType) {
    case "short":
    case "Short":
    case "int":
    case "Integer":
    case "long":
    case "Long":
      return "Integer";
    case "float":
    case "Float":
    case "double":
    case "Double":
      return "Number";
    case "boolean":
    case "Boolean":
      return "Boolean";
    case "char":
    case "Character":
    case "String":
      return "String";
    case "Date":
      return "Date";
    default:
      return "Field";
    }
  }
}
