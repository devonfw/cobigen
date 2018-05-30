package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface MyFieldAnnotation {
    byte b();

    short s();

    int i();

    long l();

    float f();

    double d();

    char c();

    String str();

    boolean bool();
}
