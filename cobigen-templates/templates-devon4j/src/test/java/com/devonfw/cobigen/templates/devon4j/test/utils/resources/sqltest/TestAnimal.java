package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This is a parent class for all animals for testing purposes.
 *
 */
public class TestAnimal {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  @Column(name = "ANIMAL_NAME", length = 50, nullable = false)
  @Size
  @NotNull
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
