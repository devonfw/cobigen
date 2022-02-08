package com.maven.project.sampledatamanagement.dataaccess.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import com.maven.project.sampledatamanagement.common.api.SampleData;

/**
 * This is the JavaDoc of {@link SampleDataEntity}.
 */
@Entity
@javax.persistence.Table(name = "SAMPLEDATA")
public class SampleDataEntity implements SampleData {

  @Column(name = "NAME")
  private String name;

  @Column(name = "SURNAME")
  private String surname;

  @Column(name = "AGE")
  private Integer age;

  @Size(max = 30, min = 3)
  private String mail;

  @Column(name = "PRIMITVEINT")
  private int primitiveInt;

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
   * @return email
   */
  public String getMail() {

    return this.mail;
  }

  /**
   * @param email new value of {@link #getemail}.
   */
  public void setMail(String email) {

    this.mail = email;
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

  public int getPrimitiveInt() {
    return this.primitiveInt;
  }

  public void setPrimitiveInt(int primitiveInt) {
    this.primitiveInt = primitiveInt;
  }

}
