package io.github.devonfw.cobigen.generator.service.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Transfer object for an address.
 */
@ApiModel
public class InputTo {

  /** 
   * Test 
   * @value test
   * @required true
   * @example test
   * @datatype test
   * @emptyvalue false
   * */
  @ApiModelProperty(value = "test", required = true, example = "test")
  private String test;

  /** Test2 */
  @ApiModelProperty(value = "test2", required = true, example = "test2")
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