package com.devonfw.cobigen.templates.devon4j.utils;

import java.util.*;
import java.util.function.Function;

/**
 * Provides operations to identify and process SQL specific information
 *
 */
public class SQLUtil extends CommonUtil {

  private static int DEFAULT_FIELD_LENGTH = 255;

  /**
   * The constructor.
   */
  public SQLUtil() {

    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Unwraps type to autogenerate a name for the table following devonfw naming convention.
   * 
   * @param entityType String that represents the entity class type
   * @return parsed table name
   */
  public String tableName(String entityType) {

    return entityType.replaceFirst(".*<", "").replaceFirst(">.*", "").replace("Entity", "").toUpperCase();
  }

  /**
   * Parses a @JoinColumn annotation directly into a Foreign Key statement for a @JoinTable
   * 
   * @param joinColumnAnnotation
   * @param defaultTableName Possible to pass TableName in case it's not specified in the annotation and has to be
   *        implied from context
   * @return column + foreign key constraint for this @JoinColumn annotation
   */
  public String parseJoinColumn(Map<String, ?> joinColumnAnnotation, String defaultTableName) {

    Function<String, String> extract = (fieldKey) -> Objects.requireNonNull(getValue(joinColumnAnnotation, fieldKey));
    String name = extract.apply("name");
    boolean nullable = Boolean.parseBoolean(extract.apply("nullable"));
    boolean unique = Boolean.parseBoolean(extract.apply("unique"));
    String table = extract.apply("table");
    String referencedColumnName = extract.apply("referencedColumnName");
    // Check if some fields haven't been defined and replace with defaults
    if (referencedColumnName.equals("")) {
      referencedColumnName = "ID";
    }
    if (table.equals("")) {
      table = defaultTableName;
    }
    if (table.equals("")) {
      throw new IllegalStateException(
          "Cannot infer name for reference table! Error encountered while parsing JoinColumnAnnotation: "
              + joinColumnAnnotation.toString());
    }
    // If the name is empty build the fieldName by appending ID to the tableName
    if (name.equals("")) {
      name = table + "_ID";
    }
    String statement = name + " BIGINT";
    if (unique) {
      statement += " UNIQUE";
    }
    if (!nullable) {
      statement += " NOT NULL";
    }
    String foreignKeyDef = String.format("FOREIGN KEY (%s) REFERENCES %s(%s)", name, table, referencedColumnName);

    return statement + ", " + foreignKeyDef;
  }

  /**
   * Generates a primary key statement from the given field
   * @param field Dynamic hashmap containing field data
   * @return SQL Primary key statement for table as String
   */
  public String primaryKeyStatement(Map<String, ?> field) {

    String fieldName = getFieldName(field);
    Map<String, ?> annotations = getValue(field, "annotations");
    Map<String, ?> columnAnnotations = getValue(annotations, "javax_persistence_Column");
    // Check for @Column to override default fieldname
    if (columnAnnotations != null) {
      String columnFieldName = getValue(columnAnnotations, "name");
      if (columnFieldName != null)
        fieldName = columnFieldName;
    }
    String incrementType = "AUTO_INCREMENT";
    return String.format("%s BIGINT %s PRIMARY KEY", fieldName, incrementType);
  }

  /**
   * Generates a foreign key statement from the given field
   * @param field Dynamic hashmap containing field data
   * @return SQL Foreign key statement as String
   */
  public String foreignKeyStatement(Map<String, ?> field) {

    Map<String, ?> annotations = getValue(field, "annotations");
    Map<String, ?> joinColumnAnnotation = getValue(annotations, "javax_persistence_JoinColumn");
    String fieldName = Objects.requireNonNull(getValue(field, "name")), fieldType = "BIGINT",
        refTable = Objects.requireNonNull(getValue(field, "type")), referencedColumnName = "id";
    Boolean unique = false, nullable = true;

    // Try extracting tablename from type following devonfw naming conventions: AwdeEntity -> AWDE
    refTable = refTable.replace("Entity", "").toUpperCase();

    // Try autogenerating foreign key name through naming convention
    fieldName = fieldName.replace("Entity", "").toUpperCase() + "_ID";

    // Parse @JoinColumn values and override defaults if values are present
    if (joinColumnAnnotation != null) {
      // Each field is extracted and controlled, if present override the defaults.
      String nameOverride = getValue(joinColumnAnnotation, "name");
      if (!Objects.equals(nameOverride, ""))
        fieldName = nameOverride;

      String tableOverride = getValue(joinColumnAnnotation, "table");
      if (!Objects.equals(tableOverride, ""))
        refTable = tableOverride;

      String refColOverride = getValue(joinColumnAnnotation, "referencedColumnName");
      if (!Objects.equals(refColOverride, ""))
        referencedColumnName = refColOverride;

      unique = isUnique(joinColumnAnnotation);
      nullable = isNullable(joinColumnAnnotation);
    }
    // Build column definition
    String columnDef = fieldName + " " + fieldType;
    if (unique)
      columnDef += " UNIQUE";
    if (!nullable)
      columnDef += " NOT NULL";
    // Build Foreign Key constraint and append it to column definition
    String foreignKeyDef = String.format("FOREIGN KEY (%s) REFERENCES %s(%s)", fieldName, refTable,
        referencedColumnName);
    return columnDef + ", " + foreignKeyDef;
  }

  /**
   * Basic SQL column statements derived from field hashmaps
   * @param field Dynamic hashmap with field data
   * @return Basic SQL statement as String
   */
  public String basicStatement(Map<String, ?> field) {

    Map<String, ?> columnAnnotation = chainAccess(field, new String[] { "annotations", "javax_persistence_Column" });
    String fieldName = getFieldName(field), typeString = Objects.requireNonNull(getValue(field, "type")),
        fieldType = mapType(typeString);
    Integer fieldLength = DEFAULT_FIELD_LENGTH;
    boolean nullable = true, unique = false;
    // Try to infer fieldType from possible annotations
    Map<String, ?> enumerateAnnotation = chainAccess(field,
        new String[] { "annotations", "javax_persistence_Enumerated" });
    if (enumerateAnnotation != null) {
      String enumType = Objects.requireNonNull(getValue(enumerateAnnotation, "value"));
      if (enumType.equals("STRING")) {
        fieldType = "VARCHAR";
      } else {
        fieldType = "INTEGER";
      }
    }
    // Parse @Column if present
    if (columnAnnotation != null) {
      fieldLength = Integer.parseInt(Objects.requireNonNull(getValue(columnAnnotation, "length")));
      nullable = isNullable(columnAnnotation);
      unique = isUnique(columnAnnotation);
    }

    // If fieldType is still empty throw exception
    if (fieldType == null)
      throw new IllegalArgumentException("Couldn't map Java type to SQL type for typeString: " + typeString);

    // Add size to VARCHAR fields
    if (fieldType.equals("VARCHAR")) {
      fieldType = String.format("VARCHAR(%d)", fieldLength);
    }
    String statement = String.format("%s %s", fieldName, fieldType);
    if (!nullable)
      statement += " NOT NULL";
    if (unique)
      statement += " UNIQUE";
    return statement;
  }

  /**
   * Extracts value of nullable from @Column and @JoinColumn annotations
   * 
   * @param columnAnnotation Map for the Column and JoinColumn annotations
   */
  private static boolean isNullable(Map<String, ?> columnAnnotation) {

    return Boolean.parseBoolean(getValue(columnAnnotation, "nullable"));
  }

  /**
   * Extracts value of unique from @Column and @JoinColumn annotations
   * 
   * @param columnAnnotation Map for the Column and JoinColumn annotations
   */
  private static boolean isUnique(Map<String, ?> columnAnnotation) {

    return Boolean.parseBoolean(getValue(columnAnnotation, "unique"));
  }

  /**
   * Helper function to map simple Java types to SQL types, returns null on unmappable type
   * @param typeString JavaType as String (int, Long, String...)
   * @return SQLType as String
   */
  public static String mapType(String typeString) {

    // Shortcut for case insensitive regex matching with start and ending ignore
    Function<String, Boolean> match = (regex) -> typeString.matches("(?i).*" + "(" + regex + ")" + ".*");
    if (match.apply("(integer)|(int)")) {
      return "INTEGER";
    } else if (match.apply("long")) {
      return "BIGINT";
    } else if (match.apply("short")) {
      return "SMALLINT";
    } else if (match.apply("BigDecimal")) {
      return "NUMERIC";
    } else if (match.apply("String")) {
      return "VARCHAR";
    } else if (match.apply("(char)|(Character)")) {
      return "CHAR(1)";
    } else if (match.apply("byte\\[\\]")) {
      return "BLOB";
    } else if (match.apply("byte")) {
      return "TINYINT";
    } else if (match.apply("boolean")) {
      return "BIT";
    } else if (match.apply("Date")) {
      return "DATE";
    } else if (match.apply("(Class)|(Locale)|(TimeZone)|(Currency)")) {
      return "VARCHAR";
    } else if (match.apply("(Timestamp)|(Calendar)")) {
      return "TIMESTAMP";
    } else if (match.apply("Time")) {
      return "TIME";
    } else {
      return null;
    }
  }

  /**
   * Extracts the name of the field from the Map whilst checking for name-override in @Column annotation
   * @param field Dynamic map for field
   * @return simple name for field as String
   */
  static private String getFieldName(Map<String, ?> field) {

    String fieldName = chainAccess(field, new String[] { "annotations", "javax_persistence_Column", "name" });
    if (fieldName != null && !fieldName.equals("")) {
      return fieldName;
    } else {
      return Objects.requireNonNull(getValue(field, "name"));
    }
  }

  /**
   * Helper function to navigate nested maps dynamically. Returns null on any type of error
   * @param map Dynamic map from which to extract data
   * @param nestedFields ordered array of fields that need to be navigated in the map
   * @return value if found, null otherwise (both on casting errors and value not found)
   */
  static private <T> T chainAccess(Map<String, ?> map, String[] nestedFields) {

    try {
      for (int i = 0; i < nestedFields.length - 1; i++) {
        String key = nestedFields[i];
        map = getValue(map, key);
      }
      return (T) getValue(map, nestedFields[nestedFields.length - 1]);
    } catch (Exception ignored) {
      return null;
    }
  }

  /**
   * Parametrized helper function to dynamically extract data from a map. Returns null on casting errors
   * @param map Dynamic map from which to extract data
   * @param key key for the value
   * @return value if found and cast succeeds, null otherwise
   */
  static private <T> T getValue(Map<String, ?> map, String key) {

    try {
      return (T) map.get(key);
    } catch (Exception ignored) {
      return null;
    }
  }
}
