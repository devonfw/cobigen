package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

public class TestClassWithAnnotations {

  private boolean boolvalue;

  /**
   * Returns the field 'boolvalue'
   *
   * @return value of boolvalue
   */
  @MyFieldAnnotation(bool = true)
  public boolean isBoolvalue() {

    return this.boolvalue;
  }

  /**
   * Sets the field 'boolvalue'.
   *
   * @param boolvalue new value of boolvalue
   */
  public void setBoolvalue(boolean boolvalue) {

    this.boolvalue = boolvalue;
  }

}
