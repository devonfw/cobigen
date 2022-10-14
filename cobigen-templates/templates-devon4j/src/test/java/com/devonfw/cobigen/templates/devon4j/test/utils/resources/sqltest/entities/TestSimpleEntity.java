package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import jakarta.validation.constraints.NotNull;

/**
 * This is a simple Entity Class for testing.
 *
 */

@Entity
@Table(name = "TEST_SIMPLE_ENTITY", schema = "RECORDS")
public class TestSimpleEntity {

  @Id
  private Long id;

  @Column(name = "TEST_SIMPLE_NAME", length = 50, nullable = false)
  private String name;

  @Column(name = "TEST_SIMPLE_AGE", length = 50)
  @NotNull
  private Integer age;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "simpleEntityId")
  private TestAnotherSimpleEntity simpleEntity;

  /**
   * The constructor.
   *
   * @param name {@link String} of this test entity
   * @param age {@link Integer} of this test entity
   */
  public TestSimpleEntity(String name, @NotNull Integer age) {

    this.name = name;
    this.age = age;
  }

  /**
   * @return string
   */
  public String testMethod() {

    return "This method will be overwritten";
  }

}
