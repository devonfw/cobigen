package com.devonfw.cobigen.api.template.out.java;

import com.devonfw.cobigen.api.template.out.CobiGenOutput;
import com.devonfw.cobigen.api.template.out.CobiGenOutputTypeFactory;

/**
 * Implementation of {@link CobiGenOutputTypeFactory} for Java.
 */
public class CobiGenOutputTypeFactoryJava implements CobiGenOutputTypeFactory {

  @Override
  public String getType() {

    return "java";
  }

  @Override
  public CobiGenOutput create(String filename) {

    return new CobiGenOutputJava(filename);
  }

}
