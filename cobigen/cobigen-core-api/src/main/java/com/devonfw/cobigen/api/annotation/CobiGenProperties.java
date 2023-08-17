package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a set of {@link CobiGenProperty} annotations to be used in a {@link CobiGenTemplate}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CobiGenProperties {

  /**
   * @return the array of {@link CobiGenProperty} annotations to define.
   */
  CobiGenProperty[] value();

}
