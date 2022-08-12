package com.devonfw.cobigen.templates.devon4j.utils;

import java.lang.reflect.Field;
import java.util.Collection;

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
