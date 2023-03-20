package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.devonfw.cobigen.api.template.generator.CobiGenGenerator;

/**
 * Annotation to mark a type that is not a {@link CobiGenTemplate template} but a dynamic type. Such type allows to be
 * extended or implemented while during instantiation it will be evaluated dynamically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.TYPE_USE, ElementType.TYPE_PARAMETER,
ElementType.LOCAL_VARIABLE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface CobiGenDynamicType {

  /**
   * @return the condition(s) that have to apply.
   */
  Class<? extends CobiGenGenerator> value();

}
