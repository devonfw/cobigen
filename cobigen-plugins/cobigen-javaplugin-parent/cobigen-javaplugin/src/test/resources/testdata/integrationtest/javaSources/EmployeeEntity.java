package com.example.domain.myapp.employeemanagement.dataaccess.api;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
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
}