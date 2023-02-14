package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.devonfw.cobigen.api.template.condition.CobiGenPredicate;

import io.github.mmm.base.lang.Conjunction;

/**
 * Annotation for a condition in a {@link CobiGenTemplate}. Annotated types, methods, fields, constructors, etc. will
 * only be generated, if the {@link #value() condition} applies and otherwise the entire annotated element will be
 * omitted and does not occur in the generated output.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CobiGenCondition {

  /**
   * @return the condition(s) that have to apply.
   */
  Class<? extends CobiGenPredicate>[] value();

  /**
   * @return the {@link Conjunction} to combine multiple {@link CobiGenPredicate conditions}.
   */
  Conjunction conjunction() default Conjunction.AND;

}
