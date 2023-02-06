package com.devonfw.cobigen.tempeng.agnostic.unittest;

import java.time.LocalDate;

import x_rootpackage_x.general.dataaccess.ApplicationPersistenceEntity;

/**
 *
 */
public class MyExampleEntity extends ApplicationPersistenceEntity {

  private String name;

  private LocalDate birthday;

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getName()}.
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return birthday
   */
  public LocalDate getBirthday() {

    return this.birthday;
  }

  /**
   * @param birthday new value of {@link #getBirthday()}.
   */
  public void setBirthday(LocalDate birthday) {

    this.birthday = birthday;
  }

}
