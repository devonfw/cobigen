package com.devonfw.poc.jwtsample.employeemanagement.dataaccess.api;

import com.devonfw.poc.jwtsample.employeemanagement.common.api.Employee;
import com.devonfw.poc.jwtsample.general.dataaccess.api.ApplicationPersistenceEntity;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Data access object for Employee entities
 */
@Entity
@javax.persistence.Table(name="Employee")
public class EmployeeEntity extends ApplicationPersistenceEntity implements Employee {

	private static final long serialVersionUID = 1L;

}
