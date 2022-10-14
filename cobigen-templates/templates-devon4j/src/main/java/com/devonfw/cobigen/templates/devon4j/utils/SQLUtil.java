package com.devonfw.cobigen.templates.devon4j.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Provides operations to identify and process SQL specific information
 *
 */
public class SQLUtil extends CommonUtil {

  /**
   * The constructor.
   */
  public SQLUtil() {

    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Returns the name of a java type as a {@link String}
   *
   * @param field
   * @return
   */
  private String getSimpleJavaTypeString(Field field) {

    // Either a full qualified class name or a simple string like "byte" for primitives
    String canonicalTypeName = field.getType().getCanonicalName();
    Boolean isExistingClass = false;

    // Verifies that the provided class exists if it's not a primitive
    if (!field.getType().isPrimitive()) {
      try {
        Class.forName(canonicalTypeName);
        isExistingClass = true;
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(
            "The provided type can not be converted into an SQL type equivalent because the java class doesn't exist!",
            e);
      }
    }

    // Get the class name from *.Byte or Byte
    Pattern myClassPattern = Pattern.compile("(\\.|^)((?:.(?!\\.))+)$");
    Matcher myClassMatch = myClassPattern.matcher(canonicalTypeName);

    if (field.getType().isPrimitive() || isExistingClass) {
      if (myClassMatch.find()) {
        return myClassMatch.group(2);

      }

    }
    throw new IllegalAccessError(
        "The provided field is neither an existing class nor an existing primitive type. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Helper function to map a Java Type to its equivalent SQL type
   *
   * @param field {@link Field} of the pojo class
   * @return returns the equivalent SQL type
   * @throws IllegalArgumentException when type is not a java type
   * @throws IllegalAccessError when the provided field wa
   */
  private String mapJavaToSqlType(Field field) throws IllegalArgumentException, IllegalAccessError {

    String javaType = getSimpleJavaTypeString(field);

    if (isEnum(field.getType().getCanonicalName())) {
      return "INTEGER";
    }

    switch (javaType) {
      // INTEGER
      case "Integer":
        return "INTEGER";
      case "int":
        return "INTEGER";
      case "Year":
        return "INTEGER";
      case "Month":
        return "INTEGER";
      // BIGINT
      case "Long":
        return "BIGINT";
      case "long":
        return "BIGINT";
      case "Object":
        return "BIGINT";
      // SMALLINT
      case "Short":
        return "SMALLINT";
      case "short":
        return "SMALLINT";
      // FLOAT
      case "Float":
        return "FLOAT";
      case "float":
        return "FLOAT";
      // DOUBLE
      case "Double":
        return "DOUBLE";
      case "double":
        return "DOUBLE";
      // NUMERIC
      case "BigDecimal":
        return "NUMERIC";
      case "BigInteger":
        return "NUMERIC";
      // CHAR
      case "Character":
        return "CHAR";
      case "char":
        return "CHAR";
      // TINYINT
      case "Byte":
        return "TINYINT";
      case "byte":
        return "TINYINT";
      // BOOLEAN
      case "Boolean":
        return "BOOLEAN";
      case "boolean":
        return "BOOLEAN";
      // TIMESTAMP
      case "Instant":
        return "TIMESTAMP";
      case "Timestamp":
        return "TIMESTAMP";
      // DATE
      case "Date":
        return "DATE";
      case "Calendar":
        return "DATE";
      // TIME
      case "Time":
        return "TIME";
      // BINARY
      case "UUID":
        return "BINARY";
      // BLOB
      case "Blob":
        return "BLOB";
      // String, Entities
      default:
        return "VARCHAR";
    }
  }

  /**
   * Get a basic SQL type statement for wrapper and primitives.
   *
   * @param field {@link Field} of the pojo class
   * @return SQL statement as {@link String}
   */
  public String getSimpleSQLtype(Field field) {

    String sqlStatement = "";
    String type = mapJavaToSqlType(field);
    sqlStatement += type;

    if (type.contains("VARCHAR") && field.isAnnotationPresent(Size.class)) {
      Integer maxSize = field.getAnnotation(Size.class).max();
      sqlStatement = sqlStatement + "(" + maxSize.toString() + ")";
    }

    if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).nullable()
        || field.isAnnotationPresent(NotNull.class)) {
      sqlStatement = sqlStatement + " NOT NULL";
    }

    return sqlStatement;
  }

  /**
   * Helper methods to get all fields recursively including fields from super classes
   *
   * @param fields list of fields to be accumulated during recursion
   * @param cl class to find declared fields
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
   * Helper method to get a field of a pojo class by its name. Including fields from super classes
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return return the field object throws IllegalArgumentException
   * @throws IllegalAccessError when the pojoClass is null
   */
  public Field getFieldByName(Class<?> pojoClass, String fieldName) throws IllegalAccessError {

    if (pojoClass != null) {
      // automatically fetches all fields from pojoClass including its super classes
      List<Field> fields = new ArrayList<>();
      getAllFields(fields, pojoClass);

      Optional<Field> field = fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();

      if (field.isPresent()) {
        return field.get();
      } else {
        throw new IllegalAccessError(
            "This field doesn't exist. Cannot generate template as it might obviously depend on reflection.");
      }
    }

    throw new IllegalAccessError(
        "Class object is null. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Method to retrieve the annotations of a field
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return an array with annotations found (length = 0 if now annotations were found)
   */
  public Annotation[] getFieldAnnotations(Class<?> pojoClass, String fieldName) {

    if (fieldName.isBlank() || fieldName == null) {
      throw new IllegalAccessError(
          "It is not possible to look for a non existing field. Cannot generate template as it might obviously depend on reflection.");
    }

    if (pojoClass != null) {
      Annotation[] annotations;
      Field field = getFieldByName(pojoClass, fieldName);
      annotations = field.getAnnotations();
      return annotations;
    }
    throw new IllegalAccessError(
        "Class object is null. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Get the annotated table name of a given Entity class
   *
   * @param field {@link Field} of the pojo class
   * @return return the annotated table name if
   * @throws ClassNotFoundException to Log an error
   */
  public String getEntityTableName(Field field) throws ClassNotFoundException {

    if (field == null) {
      throw new IllegalAccessError(
          "Field object is null. Cannot generate template as it might obviously depend on reflection.");
    }
    if (!field.getType().getCanonicalName().endsWith("Entity")) {
      throw new IllegalAccessError("The field " + field.getName()
          + " is not an entity class. Cannot generate template as it might obviously depend on reflection.");
    }

    try {

      Class<?> entityClass = Class.forName(field.getType().getCanonicalName());
      Table table = entityClass.getAnnotation(Table.class);

      String javaType = getSimpleJavaTypeString(field);

      if (table == null) {
        return StringUtils.left(javaType, javaType.length() - "Entity".length());
      } else {
        return table.name().isEmpty() ? StringUtils.left(javaType, javaType.length() - "Entity".length())
            : table.name();
      }

    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "Class path doesn't exist. Cannot generate template as it might obviously depend on reflection.", e);
    }

  }

  /**
   * Returns the name of a field when the @Column annotation sets the name option that name will be used.
   *
   * @param field the current pojo field
   * @return {@link String}
   */
  public String getColumnName(Field field) {

    if (field == null) {
      throw new IllegalAccessError(
          "Field object is null. Cannot generate template as it might obviously depend on reflection.");
    }
    return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank()
        ? field.getAnnotation(Column.class).name()
        : field.getName();
  }

  /**
   * Returns the name of a method when the @Column annotation sets the name option that name will be used.
   *
   * @param method the current method
   * @return {@link String}
   */
  public String getColumnName(Method method) {

    if (method == null) {
      throw new IllegalAccessError(
          "Method object is null. Cannot generate template as it might obviously depend on reflection.");
    }
    return method.isAnnotationPresent(Column.class) && !method.getAnnotation(Column.class).name().isBlank()
        ? method.getAnnotation(Column.class).name()
        : method.getName();
  }

  /**
   * Get the primary key and its type of a given Entity class
   *
   * @param className {@link String} full qualified class name
   * @return the primary key and its type if found or null
   */
  public String getPrimaryKey(String className) {

    if (className == null) {
      throw new IllegalAccessError(
          "The provides class name can not be null. Cannot generate template as it might obviously depend on reflection.");
    }
    try {
      Class<?> entityClass = Class.forName(className);
      List<Field> fields = new ArrayList<>();
      getAllFields(fields, entityClass);
      String columnName = "";
      for (Field field : fields) {

        if (field.isAnnotationPresent(Id.class)) {

          return field.getType().getCanonicalName() + "," + getColumnName(field);
        } else {
          Optional<Method> getterOptional = Arrays.stream(entityClass.getMethods())
              .filter(m -> m.getName()
                  .equals("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
                  && m.isAnnotationPresent(Id.class))
              .findFirst();
          if (getterOptional.isPresent()) {

            Method getter = getterOptional.get();

            return getter.getReturnType().getCanonicalName() + "," + getColumnName(getter);
          }
        }
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "Class path doesn't exist. Cannot generate template as it might obviously depend on reflection.", e);
    }

    throw new IllegalAccessError(
        "Could not find the field or getter with @Id annotated. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Get a List of HashMap Keys holding information about a given field. Default value overloaded by
   * {@link SQLUtil#getPrimaryKeyStatement(Field, String)}
   *
   * @param field the current pojo field
   * @return HashMap containing name and type of a column
   */
  public HashMap<String, String> getPrimaryKeyStatement(Field field) {

    if (field == null) {
      throw new IllegalAccessError(
          "Field object is null. Cannot generate template as it might obviously depend on reflection.");
    }
    String name = getColumnName(field);
    HashMap<String, String> column = getPrimaryKeyStatement(field, name);
    return column;
  }

  /**
   * Get a List of HashMap Keys holding information about a given field
   *
   * @param field the current pojo field
   * @param name the column name related to that field
   * @return HashMap containing name and type of a column
   */
  public HashMap<String, String> getPrimaryKeyStatement(Field field, String name) {

    if (field == null) {
      throw new IllegalAccessError(
          "Field object is null. Cannot generate template as it might obviously depend on reflection.");
    }

    if (name == null || name.isBlank()) {
      name = getColumnName(field);
    }
    String type = getSimpleSQLtype(field);

    if (!type.contains("NOT NULL")) {
      // Make sure Primary Keys will always be created with the NOT NULL statement
      type = type + " NOT NULL";
    }

    if (field.isAnnotationPresent(GeneratedValue.class)
        && field.getAnnotation(GeneratedValue.class).strategy().equals(GenerationType.AUTO)) {
      type = type + " AUTO INCREMENT";
    }

    HashMap<String, String> column = columnMapBuild(name, type);

    return column;
  }

  /**
   * Helper method to build a hash map for foreign key value pairs
   *
   * @param name name of the column
   * @param type type of the column
   * @return HashMap containing name and type of a column
   */
  private HashMap<String, String> columnMapBuild(String name, String type) {

    HashMap<String, String> columnMap = new HashMap<String, String>() {
      {
        put("name", name);
        put("type", type);
      }
    };

    return columnMap;
  }

  /**
   * Helper method to build a hash map for foreign key value pairs
   *
   * @param name name of the foreign key
   * @param table table the current table name
   * @param columnname referenced column name
   * @return HashMap for name, table and column name
   */
  private HashMap<String, String> fkMapBuild(String name, String table, String columnname) {

    HashMap<String, String> foreignKeyMap = new HashMap<String, String>() {
      {
        put("name", name);
        put("table", table);
        put("columnname", columnname);
      }
    };
    return foreignKeyMap;
  }

  /**
   * Get the name of a @JoinColumn or fall back to a default in name in case the name option is not set
   *
   * @param field current pojo class
   * @param fallBack String for fallback option
   * @return {@link String} Column name
   */
  public String getForeignKeyName(Field field, String fallBack) {

    if (field == null) {
      throw new IllegalAccessError(
          "Field object is null. Cannot generate template as it might obviously depend on reflection.");
    }

    if (fallBack == null || fallBack.isBlank()) {
      fallBack = "default";
    }

    String name;
    if (field.isAnnotationPresent(JoinColumn.class) && !field.getAnnotation(JoinColumn.class).name().isBlank()) {
      name = field.getAnnotation(JoinColumn.class).name();
    } else {
      name = field.getName() + "_" + fallBack;
    }
    return name;
  }

  /**
   * Retrieve the name of a referenced column name and its type statement based on the provided field. @JoinColumn has
   * precedence over @OneToMany when the field is annotated with both the option referencedColumnName from
   * the @JoinColumn annotation and the mappedBy option from the @OneToMany annotation. The method will find the owner
   * of the entity if none of the options are provided. This method will mostly be used on Entity types.
   *
   * @param field current pojo class
   * @return comma separated String or null
   */
  public String getForeignKeyStatement(Field field) {

    String columnName, type = "";

    try {
      // The current field type is the class in which we are searching the primary key
      Class<?> foreignEntityClass = Class.forName(field.getType().getCanonicalName());

      // Building the column name based on provided annotations
      if (field.isAnnotationPresent(JoinColumn.class)
          && !field.getAnnotation(JoinColumn.class).referencedColumnName().isEmpty()) {
        // Annotation @JoinColumn is set and a name was provided
        columnName = field.getAnnotation(JoinColumn.class).referencedColumnName();

      } else if (field.isAnnotationPresent(OneToOne.class)
          && !field.getAnnotation(OneToOne.class).mappedBy().isBlank()) {
        // mappedBy option is set by @OneToOne annotation
        columnName = field.getAnnotation(OneToOne.class).mappedBy();

      } else {
        // No information was provided and we are looking for the primary key manually
        String[] pkReceived = getPrimaryKey(field.getType().getCanonicalName()).split(",");
        columnName = pkReceived[1];

      }

      try {
        Field foreignField = foreignEntityClass.getDeclaredField(columnName);
        type = getSimpleSQLtype(foreignField);
      } catch (NoSuchFieldException e) {
        throw new IllegalAccessError(
            "Couldn't find the foreign key field. Cannot generate template as it might obviously depend on reflection.");
      }

      return columnName + "," + type;

    } catch (ClassNotFoundException e) {
      throw new IllegalAccessError(
          "Class object is null. Cannot generate template as it might obviously depend on reflection.");
    }
  }

  /**
   * Get a List of Key and Value pairs holding the information about foreign keys. It will be assumed that the current
   * field is an entity. This function defaults the {@link String} name parameter of
   * {@link SQLUtil#getForeignKeyData(Field, String)}
   *
   * @param field the pojo field
   * @return HashMap holding the information {"name": name, "table": table, "columnname": columnname} or null
   *
   * @name {@link String} the name of the foreign key
   * @table {@link String} the table which is referenced by the foreign key
   * @columnname {@link String} the column name of the referenced with @id annotated variable
   */
  public HashMap<String, String> getForeignKeyData(Field field) {

    // Assumes @JoinColumn is present
    if (field.isAnnotationPresent(JoinColumn.class)) {
      String[] fkDeclaration = getForeignKeyStatement(field).split(",");
      String name = getForeignKeyName(field, fkDeclaration[0]);

      return getForeignKeyData(field, name);
    }

    throw new IllegalAccessError(
        "The ForeignKeyData couldn't be generated. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Get a List of Key and Value pairs holding the information about foreign keys. It will be assumed that the current
   * field is an entity.
   *
   * @param field the pojo field
   * @param name the name of the foreign key
   * @return HashMap holding the information {"name": name, "table": table, "columnname": columnname} or null
   *
   * @name {@link String} the name of the foreign key
   * @table {@link String} the table which is referenced by the foreign key
   * @columnname {@link String} the column name of the referenced with @id annotated variable
   */
  public HashMap<String, String> getForeignKeyData(Field field, String name) {

    // Assumes @JoinColumn is present
    if (field.isAnnotationPresent(JoinColumn.class)) {
      String[] fkDeclaration = getForeignKeyStatement(field).split(",");
      String tableName = getForeignKeyTableName(field);
      HashMap<String, String> foreignKeyData = fkMapBuild(name, tableName, fkDeclaration[0]);

      return foreignKeyData;
    }

    throw new IllegalAccessError(
        "The ForeignKeyData couldn't be generated. Cannot generate template as it might obviously depend on reflection.");
  }

  /**
   * Get the table for the foreign key that the current field was annotated with
   *
   * @param field current pojo class
   * @return table name as a {@link String} or null
   */
  public String getForeignKeyTableName(Field field) {

    try {
      String tableName;
      if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
        tableName = getEntityTableName(field);
      } else {
        tableName = getSimpleJavaTypeString(field);
      }

      return tableName;
    } catch (ClassNotFoundException e) {
      throw new IllegalAccessError(
          "It is not possible to look for a non existing field. Cannot generate template as it might obviously depend on reflection.");

    }

  }

}
