package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AbstractTestClass extends SuperSuperTestClass {

  /**
   * Example JavaDoc
   */
  @MySuperTypeFieldAnnotation
  private Long id;

  @MySuperTypeGetterAnnotation
  public Long getId() {

    return this.id;
  }

  @MySuperTypeIsAnnotation
  public Long isId() {

    return this.id;
  }

  @MySuperTypeSetterAnnotation
  public void setId(Long id) {

    this.id = id;
  }

}

// simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
@interface MySuperTypeFieldAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperTypeGetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperTypeSetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperTypeIsAnnotation {
}
