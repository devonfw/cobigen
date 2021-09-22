package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.subpackage;

import java.util.List;

import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;

/**
 *
 * @author mbrunnli (25.01.2015)
 */
public class SuperClass2 {

  private List<RootClass> genericAccessible;

  /**
   * Returns the field 'genericAccessible'
   *
   * @return value of genericAccessible
   * @author mbrunnli (25.01.2015)
   */
  public List<RootClass> getGenericAccessible() {

    return this.genericAccessible;
  }

  /**
   * Sets the field 'genericAccessible'.
   *
   * @param genericAccessible new value of genericAccessible
   * @author mbrunnli (25.01.2015)
   */
  public void setGenericAccessible(List<RootClass> genericAccessible) {

    this.genericAccessible = genericAccessible;
  }

}
