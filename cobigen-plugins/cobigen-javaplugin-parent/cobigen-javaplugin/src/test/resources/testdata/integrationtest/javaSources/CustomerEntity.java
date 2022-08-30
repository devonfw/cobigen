package com.example.dataaccess.api;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * This is the JavaDoc of {@link CustomerEntity}.
 */
@Entity
@javax.persistence.Table(name = "CUSTOMERDATA")
public class CustomerEntity {

  @Column(name = "NAME")
  private String name;

  @Column(name = "SURNAME")
  private String surname;

  @Column(name = "AGE")
  private Integer age;

  private static final long serialVersionUID = 1L;

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return surname
   */
  public String getSurname() {

    return this.surname;
  }

  /**
   * @param surname new value of {@link #getsurname}.
   */
  public void setSurname(String surname) {

    this.surname = surname;
  }

  /**
   * @return age
   */
  public Integer getAge() {

    return this.age;
  }

  /**
   * @param age new value of {@link #getage}.
   */
  public void setAge(Integer age) {

    this.age = age;
  }

  public Long getId() {

    // TODO Auto-generated method stub
    return null;
  }

  public void setId(Long id) {

    // TODO Auto-generated method stub

  }

  public int getModificationCounter() {

    // TODO Auto-generated method stub
    return 0;
  }

  public void setModificationCounter(int modificationCounter) {

    // TODO Auto-generated method stub

  }

}
