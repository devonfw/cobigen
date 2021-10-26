package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import org.junit.experimental.categories.Category;

public class TestClassWithAnnotationsContainingObjectArrays {

  private boolean boolvalue;

  /**
   * Returns the field 'boolvalue'
   *
   * @return value of boolvalue
   * @author mbrunnli (05.12.2014)
   */
  @Category({ TestClassWithAnnotationsContainingObjectArrays.class, TestClassWithAnnotations.class })
  public boolean isBoolvalue() {

    return this.boolvalue;
  }

  /**
   * Sets the field 'boolvalue'.
   *
   * @param boolvalue new value of boolvalue
   * @author mbrunnli (05.12.2014)
   */
  public void setBoolvalue(boolean boolvalue) {

    this.boolvalue = boolvalue;
  }

}
