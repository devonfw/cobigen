package com.devonfw.cobigen.templates.devon4j.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
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
   * Helper function to map a Java Type to its equivalent SQL type
   *
   * @param canonicalTypeName {@link String} full qualified class name
   * @return returns the equivalent SQL type
   * @throws IllegalArgumentException when type is not a java type
   */
  public String mapJavaToSqlType(String canonicalTypeName) throws IllegalArgumentException {

    try {

      if (isEnum(canonicalTypeName)) {
        return "INTEGER";
      }

      switch (canonicalTypeName) {
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
        default:
          return "VARCHAR";
      }
    } catch (IllegalArgumentException e) {
      LOG.error("{}: The parameter is not a valid argument", e.getMessage());
      return null;
    }

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
   * Helper method to get a field of a pojo class by its name. Including fields from super classes
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return return the field object throws IllegalArgumentException
   */
  private Field getFieldByName(Class<?> pojoClass, String fieldName) throws IllegalArgumentException {

    if (pojoClass != null) {
      // automatically fetches all fields from pojoClass including its super classes
      List<Field> fields = new ArrayList<>();
      getAllFields(fields, pojoClass);

      Optional<Field> field = fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();

      if (field.isPresent()) {
        return field.get();
      }
    }
    LOG.error("Could not find type of field {}", fieldName);
    return null;
  }

  /**
   * Method to retrieve the type of a field
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return return the type of the field
   */
  public Class<?> getTypeOfField(Class<?> pojoClass, String fieldName) {

    if (pojoClass != null) {
      Field field = getFieldByName(pojoClass, fieldName);
      return field.getType();
    }
    return null;
  }

  /**
   * Method to retrieve the annotations of a field
   *
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return an array with annotations found (length = 0 if now annotations were found)
   */
  public Annotation[] getFieldAnnotations(Class<?> pojoClass, String fieldName) {

    if (pojoClass != null) {
      Annotation[] annotations;
      Field field = getFieldByName(pojoClass, fieldName);
      annotations = field.getAnnotations();
      return annotations;
    }
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

  /**
   * Helper method to build a hash map for foreign key values
   *
   * @param name name of the foreign key
   * @param table the current table name
   * @param columnname referenced column name
   * @return
   */
  private HashMap<String, String> fkMapBuild(String name, String table, String columnname) {

    HashMap<String, String> foreignKeyMap = new HashMap<String, String>() {
      {
        put("key", name);
        put("table", table);
        put("id", columnname);
      }
    };
    return foreignKeyMap;
  }

  /**
   * Get a List of HashMaps holding the information for foreign keys assuming the current field is an entity
   *
   * @param field the pojo field
   * @return List of Hash Map holding the information {"key": name, "table": table, "id": id}
   *
   * @key {@link String} the name of the foreign key
   * @table {@link String} the table which is referenced by the foreign key
   * @id {@link String} the name of the referenced with @id annotated variable
   */
  public List<HashMap<String, String>> getForeignKeyData(Field field) {

    String table = "", tableReceived;
    List<HashMap<String, String>> foreignKeyData = new ArrayList<>();

    // Assumes @JoinColumn is present
    if (field.isAnnotationPresent(JoinColumn.class)) {
      String[] fkDeclaration = getForeignKeyDeclaration(field).split(",");
      String name = getForeignKeyName(field, fkDeclaration[1]);
      String tableName = getForeignKeyTableName(field);
      foreignKeyData.add(fkMapBuild(name, table, fkDeclaration[0]));
    }

    return foreignKeyData;
  }

  /**
   * Get the table name of the current pojo
   *
   * @param field field the pojo field
   * @return {@link String}
   */
  public String getForeignKeyTableName(Field field) {

    try {
      String tableName = "";
      if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
        tableName = getEntityTableName(field.getType().getCanonicalName());
      } else {
        tableName = field.getType().getCanonicalName();
      }

      return tableName;
    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), field.getType().getCanonicalName());
    }
    return null;
  }

  /**
   * Retrieve the name of a referenced column and its type based on the provided field
   *
   * @param field current pojo class
   * @return comma separated String or null
   */
  public String getForeignKeyDeclaration(Field field) {

    String columnName, type;
    try {
      if (field.isAnnotationPresent(JoinColumn.class)
          && !field.getAnnotation(JoinColumn.class).referencedColumnName().isEmpty()) {
        columnName = field.getAnnotation(JoinColumn.class).referencedColumnName();
        type = mapJavaToSqlType(getCanonicalNameOfFieldType(field.getType().getCanonicalName(), columnName));
      } else {
        String[] pkReceived = getPrimaryKey(field.getType().getCanonicalName()).split(",");
        type = mapJavaToSqlType(pkReceived[0]);
        columnName = pkReceived[1];
      }
      return columnName + "," + type;

    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), field.getType().getCanonicalName());
    }
    return null;
  }

  /**
   * Get the name of a @JoinColumn or fall back to a default in name in case the name option is not set
   *
   * @param field current pojo class
   * @param fallBack String for fallback option
   * @return {@link String} Column name
   */
  public String getForeignKeyName(Field field, String fallBack) {

    String name;
    if (field.isAnnotationPresent(JoinColumn.class) && !field.getAnnotation(JoinColumn.class).name().isBlank()) {
      name = field.getAnnotation(JoinColumn.class).name();
    } else {
      name = field.getName() + "_" + fallBack;
    }
    return name;
  }

  /**
   * Method to get the SQL type statement for Primary Keys and simple Columns
   *
   * @param className {@link String} full qualified class name
   * @param fieldName {@link String} the name of the field
   * @return SQL type as a String
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("javadoc")
  public String getSqlTypeStatement(Annotation[] annotations, String className, String fieldName)
      throws ClassNotFoundException {

    try {
      String sqlType = mapJavaToSqlType(getCanonicalNameOfFieldType(className, fieldName));
      String sqlTypeExtension = "";

      if (annotations.length != 0) {
        for (Annotation annotation : annotations) {
          if (sqlType.equals("VARCHAR") && annotation.annotationType().equals(Size.class)) {
            Integer maxSize = ((Size) annotation).max(); // Size.max is always present as it defaults to
                                                         // Integer.MAX_VALUE;
            sqlTypeExtension = sqlTypeExtension + "(" + maxSize.toString() + ")";
          }
          if ((annotation.annotationType().equals(Column.class) && !((Column) annotation).nullable())
              || (annotation.annotationType().equals(NotNull.class))) {
            sqlTypeExtension = sqlTypeExtension + " NOT NULL";
          }
          if (annotation.annotationType().equals(GeneratedValue.class)) {
            sqlTypeExtension = sqlTypeExtension + " AUTO_INCREMENT";
          }
        }
      }
      return sqlType + sqlTypeExtension;
    } catch (ClassNotFoundException e) {
      LOG.error("{}: Could not find {}", e.getMessage(), className);
      return null;
    }
  }

}
