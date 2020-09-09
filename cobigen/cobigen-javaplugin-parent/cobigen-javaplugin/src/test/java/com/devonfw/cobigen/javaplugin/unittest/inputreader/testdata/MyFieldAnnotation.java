package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface MyFieldAnnotation {
    byte b() default 0;

    short s() default 0;

    int i() default 0;

    long l() default 0l;

    float f() default 0;

    double d() default 0.0;

    char c() default '0';

    String str() default "0";

    boolean bool() default false;
}
