package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.devonfw.cobigen.api.template.generator.CobiGenGenerator;

/**
 * Annotation to mark a type that is not a {@link CobiGenTemplate template} but a dynamic type. Such type allows to be
 * extended or implemented while during instantiation it will be evaluated dynamically.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CobiGenDynamicType {

  /**
   * @return the condition(s) that have to apply.
   */
  Class<? extends CobiGenGenerator> value();

}
