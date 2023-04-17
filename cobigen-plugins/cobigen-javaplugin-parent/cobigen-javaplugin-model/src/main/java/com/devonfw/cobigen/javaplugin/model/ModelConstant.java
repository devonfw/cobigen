package com.devonfw.cobigen.javaplugin.model;

import java.util.List;
import java.util.Map;

/**
 * String constants of the java object model for generation. Outcome of the JavaInputReader.
 */
public class ModelConstant {

  /**
   * Node for hand-written template utils
   */
  public static final String UTILS = "utils";

  /**
   * Root element for java model ({@link Map}&lt;{@link String}, {@link Object}&gt;)
   */
  public static final String MODEL_ROOT = "pojo";

  /** Raw class object providing any reflection interaction. ({@link Class}) */
  public static final String CLASS_OBJECT = "classObject";

  /** Package name of the input class ({@link String}) */
  public static final String PACKAGE = "package";

  /**
   * Simple name of the input class ({@link String})
   */
  public static final String NAME = "name";

  /**
   * Full qualified name of the input class ({@link String})
   */
  public static final String CANONICAL_NAME = "canonicalName";

  /**
   * Type of a field of the input class ({@link String})
   */
  public static final String TYPE = "type";

  /**
   * Type of the supertype of the input class ({@link Map}&lt;{@link String}, {@link Object}&gt;)
   */
  public static final String EXTENDED_TYPE = "extendedType";

  /**
   * A list of all implementedTypes (interfaces) of the input class, whereas one interface will be represented by a set
   * of mappings ( {@link List}&lt; {@link Map}&lt;{@link String}, {@link Object} &gt;&gt;)
   */
  public static final String IMPLEMENTED_TYPES = "implementedTypes";

  /**
   * Full qualified type of a field of the input class ({@link String})
   */
  public static final String CANONICAL_TYPE = "canonicalType";

  /**
   * @see #FIELDS
   * @deprecated this is the deprecated accessor for the list of Java fields named 'attributes'. Please use
   *             {@link #FIELDS} for new features. This accessor value for the model will be removed at the next major
   *             release.
   */
  @Deprecated
  public static final String FIELDS_DEPRECATED = "attributes";

  /**
   * A list of all fields, whereas one field will be represented by a set of attribute mappings ( {@link List}&lt;
   * {@link Map}&lt;{@link String}, {@link Object}&gt;&gt;)
   */
  public static final String FIELDS = "fields";

  /**
   * A list of all methods, whereas one method will be represented by a set of attribute mappings ( {@link List}&lt;
   * {@link Map}&lt;{@link String}, {@link Object}&gt;&gt;)
   */
  public static final String METHODS = "methods";

  /**
   * Annotations of methods or fields, which will be represented by a mapping of the full qualified type of an
   * annotation to its value. To gain template compatibility, the key will be stored with '_' instead of '.' in the full
   * qualified annotation type. Furthermore the annotation might be recursively defined and thus be accessed using the
   * same type of {@link #ANNOTATIONS} (Type: {@link Map}&lt;{@link String}, {@link Object}&gt;)
   */
  public static final String ANNOTATIONS = "annotations";

  /**
   * JavaDoc of a method or a field ({@link String})
   */
  public static final String JAVADOC = "javaDoc";

  /**
   * JavaDoc comment (without doclets) or possibly any other comment
   */
  public static final String COMMENT = "comment";

  /**
   * A list of all visible fields accessible via setter and getter methods including inherited fields.
   */
  public static final String METHOD_ACCESSIBLE_FIELDS = "methodAccessibleFields";
}
