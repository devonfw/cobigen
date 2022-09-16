package com.example.domain.myapp.employeemanagement.dataaccess.api;



import javax.persistence.Entity;

public class EmployeeEntity {

  @Column(name = "NAME")
  private String name;


  private String getSurname();
}