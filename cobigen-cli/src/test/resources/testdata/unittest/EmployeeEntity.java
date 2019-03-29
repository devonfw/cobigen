package com.devonfw.poc.jwtsample.employeemanagement.dataaccess.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@javax.persistence.Table(name = "EMPLOYEE")
public class EmployeeEntity {

  @Column(name = "EMPLOYEEID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long employeeId;

  @Column(name = "NAME")
  private String name;

  @Column(name = "SURNAME")
  private String surname;

  @Column(name = "EMAIL")
  private String email;

  private static final long serialVersionUID = 1L;

  public Long getEmployeeId() {

    return this.employeeId;
  }

  public void setEmployeeId(Long employeeId) {

    this.employeeId = employeeId;
  }

  public String getName() {

    return this.name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getSurname() {

    return this.surname;
  }

  public void setSurname(String surname) {

    this.surname = surname;
  }

  public String getEmail() {

    return this.email;
  }

  public void setEmail(String email) {

    this.email = email;
  }

}
