package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Test entity to test the correct generation of the enumerated type, the primary key, and name overriding
 *
 */
@Entity
@Table(name = "SQLTEST")
public class SQLTestEntity {
  @Id
  @Column(name = "MY_ID_FIELD")
  private Long id;

  @Column(name = "VALUENAME")
  private Integer integerValue;

  @Enumerated(EnumType.STRING)
  @Column(length = 420, name = "ENUM_TEST_FIELD_NAME_OVERRIDE")
  private EnumForTest enumForTest;

  public Long getId() {

    return this.id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public Integer getIntegerValue() {

    return this.integerValue;
  }

  public void setIntegerValue(Integer value) {

    this.integerValue = value;
  }

  public EnumForTest getEnumForTest() {

    return this.enumForTest;
  }

  public void setEnumForTest(EnumForTest enumForTest) {

    this.enumForTest = enumForTest;
  }

}
