package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark and configure a template written in Java.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CobiGenTemplate {

  /**
   * @return the unique identifier of what is currently called "increment" in CobiGen. I would propose to use I18N for
   *         mapping this ID to a display title for CobiGen UI and template collections can ship with individual
   *         resource bundle properties providing these titles. Define a central type in your template collection
   *         defining all groups as constants and always reference a constant for this annotation property value.
   */
  String value();

  /**
   * @return {@code true} if constant or static template for code artefact that does not depend on the input. It will be
   *         generated if triggered and not present. Once generated the file should never be touched.
   */
  boolean constant() default false;

}
