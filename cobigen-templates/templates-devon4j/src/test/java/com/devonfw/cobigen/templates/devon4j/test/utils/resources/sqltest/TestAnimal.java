package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

import javax.persistence.Id;

/**
 * This is a parent class for all animals for testing purposes.
 *
 */
public class TestAnimal {
  @Id
  Long id;

  private String name;

  public TestAnimal(String name) {

    this.name = name;
  }

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getName}.
   */
  public void setName(String name) {

    this.name = name;
  }

}
