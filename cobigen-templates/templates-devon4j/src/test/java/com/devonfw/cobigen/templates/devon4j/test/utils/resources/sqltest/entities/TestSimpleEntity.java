package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import jakarta.validation.constraints.NotNull;

/**
 * This is a simple Entity Class for testing.
 *
 */

@Entity
public class TestSimpleEntity {

  @Id
  private Long id;

  @Column(name = "TEST_SIMPLE_NAME", length = 50, nullable = false)
  private String name;

  @Column(name = "TEST_SIMPLE_AGE", length = 50)
  @NotNull
  private Integer age;

}
