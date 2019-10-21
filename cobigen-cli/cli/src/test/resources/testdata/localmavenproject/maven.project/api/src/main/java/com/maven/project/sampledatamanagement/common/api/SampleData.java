package com.maven.project.sampledatamanagement.common.api;

import com.devonfw.module.basic.common.api.entity.GenericEntity;

public interface SampleData extends GenericEntity<Long> {

  /**
   * getter
   * 
   * @return nameId
   */

  public String getName();

  /**
   * @param name setter for name attribute
   */

  public void setName(String name);

  /**
   * @return surnameId
   */

  public String getSurname();

  /**
   * @param surname setter for surname attribute
   */

  public void setSurname(String surname);

  /**
   * @return ageId
   */

  public Integer getAge();

  /**
   * @param age setter for age attribute
   */

  public void setAge(Integer age);

  /**
   * @return mailId
   */

  public String getMail();

  /**
   * @param mail setter for mail attribute
   */

  public void setMail(String mail);

}
