package com.devonfw.cobigen.templates.devon4j.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides operations to identify and process SQL specific information
 *
 */
public class SQLUtil extends CommonUtil {

  /**
   * Logger for this class
   */
  private static final Logger LOG = LoggerFactory.getLogger(JavaUtil.class);

  /**
   * The constructor.
   */
  public SQLUtil() {

    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Helper methods to get all fields recursively including fields from super classes
   *
   * @param fields list of fields to be accumulated during recursion
   * @param cl class to find fields
   * @return list of all fields
   */
  private static List<Field> getAllFields(List<Field> fields, Class<?> cl) {

    fields.addAll(Arrays.asList(cl.getDeclaredFields()));

    if (cl.getSuperclass() != null) {
      getAllFields(fields, cl.getSuperclass());
    }

    return fields;
  }

  /**
   * Helper method to retrieve the type of a field including fields from super classes
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return return the type of the field
   */
  private Class<?> getTypeOfField(Class<?> pojoClass, String fieldName) {

    if (pojoClass != null) {
      // automatically fetches all fields from pojoClass including its super classes
      List<Field> fields = new ArrayList<>();
      getAllFields(fields, pojoClass);

      Optional<Field> field = fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();

      if (field.isPresent()) {
        return field.get().getType();
      }
    }
    LOG.error("Could not find type of field {}", fieldName);
    return null;
  }

  /**
   * Get the annotated table name of a given Entity class
   *
   * @param className {@link String} full qualified class name
   * @return return the annotated table name if
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("javadoc")
  public String getEntityTableName(String className) throws ClassNotFoundException {

    if (!className.endsWith("Entity")) {
      LOG.error("Could not return table name because {} is not an Entity class", className);
      return null;
    }

    try {
      Class<?> entityClass = Class.forName(className);
      Table table = entityClass.getAnnotation(Table.class);
      return table == null
          ? StringUtils.left(entityClass.getSimpleName(), entityClass.getSimpleName().length() - "Entity".length())
          : table.name();
    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), className);
      return null;
    }
  }

  /**
   * @param className {@link String} full qualified class name
   * @param fieldName {@link String} the name of the field
   * @return type of the field in the string
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("javadoc")
  public String getCanonicalNameOfFieldType(String className, String fieldName) throws ClassNotFoundException {

    try {
      Class<?> entityClass = Class.forName(className);
      Class<?> type = getTypeOfField(entityClass, fieldName);
      if (type != null) {
        return type.getCanonicalName();
      }
    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), className);
    }
    return null;
  }

  /**
   * Get the primary key and its type of a given Entity class
   *
   * @param className {@link String} full qualified class name
   * @return the primary key and its type if found or null
   */
  public String getPrimaryKey(String className) {

    try {
      Class<?> entityClass = Class.forName(className);
      List<Field> fields = new ArrayList<>();
      getAllFields(fields, entityClass);
      for (Field field : fields) {
        if (field.isAnnotationPresent(Id.class)) {
          return field.getType().getCanonicalName() + ","
              + (field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name() : field.getName());
        } else {
          Optional<Method> getterOptional = Arrays.stream(entityClass.getMethods())
              .filter(m -> m.getName()
                  .equals("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
                  && m.isAnnotationPresent(Id.class))
              .findFirst();
          if (getterOptional.isPresent()) {
            Method getter = getterOptional.get();
            return getter.getReturnType().getCanonicalName() + ","
                + (getter.isAnnotationPresent(Column.class) ? getter.getAnnotation(Column.class).name()
                    : field.getName());
          }
        }
      }
    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), className);
    }
    LOG.error("Could not find the field or getter with @Id annotated");
    return null;
  }

  // public String getSqlType(String className, String fieldName) throws ClassNotFoundException {
  //
  // try {
  // String javaType = getCanonicalNameOfFieldType(className, fieldName);
  // } catch (ClassNotFoundException e) {
  // LOG.error("{}: Could not find {}", e.getMessage(), className);
  // }
  // return "";
  // }
  //
  // public String getSqlStrategy(Field field) {
  //
  // return "";
  // }
}
