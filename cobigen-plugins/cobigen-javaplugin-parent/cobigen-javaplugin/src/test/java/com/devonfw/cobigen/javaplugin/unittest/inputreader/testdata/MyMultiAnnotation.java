package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyMultiAnnotation {

  public Type[] value();

  public @interface Type {
    public Class<?> value();

    public String name();
  }
}
