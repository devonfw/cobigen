package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a property as {@link #key()} {@link #value()} pair for annotation in {@link CobiGenTemplate}.
 */
@Repeatable(CobiGenProperties.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CobiGenProperty {

  /**
   * @return the property key.
   */
  String key();

  /**
   * @return the property value.
   */
  String value();

}
