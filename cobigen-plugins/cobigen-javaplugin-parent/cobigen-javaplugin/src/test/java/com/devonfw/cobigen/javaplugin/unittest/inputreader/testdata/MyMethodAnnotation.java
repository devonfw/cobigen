package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface MyMethodAnnotation {

    boolean bool();
}
