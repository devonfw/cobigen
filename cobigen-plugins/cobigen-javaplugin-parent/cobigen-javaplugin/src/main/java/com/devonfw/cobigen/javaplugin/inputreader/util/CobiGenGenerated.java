package com.devonfw.cobigen.javaplugin.inputreader.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CobiGenGenerated {

  String value() default "CobiGen";

  String date() default "";

}
