package com.devonfw.cobigen.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation allowing activation criteria for a plug-in
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface Activation {

  /**
   * @return the file extensions for which the plug-in should be activated respectively for which the plug-in provides
   *         an input reader. The file extension should be noted by the extension only without dot or asterix, i.e. {
   *         'html', 'xhtml' }
   */
  String[] byFileExtension() default {};

  /**
   * @return whether this plug-in can read a folder as input.
   */
  boolean byFolder() default false;

  /**
   * @return the merge strategies provided by this plug-in, which will cause the plug-in lazily to be loaded just in
   *         case a merge strategy is requested which is provided by this plug-in
   */
  String[] byMergeStrategy() default {};

}
