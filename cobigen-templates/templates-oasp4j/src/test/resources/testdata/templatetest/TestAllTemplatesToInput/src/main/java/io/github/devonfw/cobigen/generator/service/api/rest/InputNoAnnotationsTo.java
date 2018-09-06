package io.github.devonfw.cobigen.generator.service.data;

/**
 * Transfer object for an address.
 */
public class InputNoAnnotationsTo {

  /** Test */
  private String test;

  /** Test2 */
  private String testTwo;


  /**
   * @param test new value of {@link #gettest}.
   */
  public void setTest(String test) {

    this.test = test;
  }
  
  /**
   * @return test
   */
  public String getTest() {

    return this.test;
  }

  /**
   * @return testTwo
   */
  public String getTestTwo() {

    return this.testTwo;
  }

  /**
   * @param testTwo new value of {@link #testTwo}.
   */
  public void setTestTwo(String testTwo) {

    this.testTwo = testTwo;
  }
}