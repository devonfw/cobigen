package com.devonfw.cobigen.templates.devon4j.utils;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utility for all objects that inherit from it
 *
 */
public class CommonUtil {

  /**
   * The constructor.
   */
  public CommonUtil() {

    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Logger for this class
   */
  protected static final Logger LOG = LoggerFactory.getLogger(JavaUtil.class);

  /**
   * Checks whether the class given by the full qualified name is an enum
   *
   * @param className full qualified class name
   * @return <code>true</code> if the class is an enum, <code>false</code> otherwise
   */
  public boolean isEnum(String className) {

    try {
      return ClassUtils.getClass(className).isEnum();
    } catch (ClassNotFoundException e) {
      LOG.warn("{}: Could not find {}", e.getMessage(), className);
      return false;
    }
  }

  /**
   * @param pojoClass {@link Class} the class object of the pojo
   * @param fieldName {@link String} the name of the field
   * @return true if the field is an instance of java.utils.Collections
   * @throws NoSuchFieldException indicating something awefully wrong in the used model
   * @throws SecurityException if the field cannot be accessed.
   */
  public boolean isCollection(Class<?> pojoClass, String fieldName) throws NoSuchFieldException, SecurityException {

    if (pojoClass == null) {
      return false;
    }

    Field field = pojoClass.getDeclaredField(fieldName);
    if (field == null) {
      field = pojoClass.getField(fieldName);
    }
    if (field == null) {
      return false;
    } else {
      return Collection.class.isAssignableFrom(field.getType());
    }

  }

}
