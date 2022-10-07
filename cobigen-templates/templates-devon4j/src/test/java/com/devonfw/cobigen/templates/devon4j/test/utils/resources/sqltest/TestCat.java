package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

/**
 * This is a child animal class for testing purposes.
 *
 */
public class TestCat extends TestAnimal {
  private Integer legs;

  /**
   * The constructor.
   */
  public TestCat(String name, Integer legs) {

    super(name);
    this.legs = legs;
  }

  /**
   * @return legs
   */
  public Integer getLegs() {

    return this.legs;
  }

  /**
   * @param legs new value of {@link #getLegs}.
   */
  public void setLegs(Integer legs) {

    this.legs = legs;
  }

}
