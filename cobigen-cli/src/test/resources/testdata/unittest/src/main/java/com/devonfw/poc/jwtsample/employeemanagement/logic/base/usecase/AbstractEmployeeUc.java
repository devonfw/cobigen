package com.devonfw.poc.jwtsample.employeemanagement.logic.base.usecase;

import com.devonfw.poc.jwtsample.general.logic.base.AbstractUc;
import com.devonfw.poc.jwtsample.employeemanagement.dataaccess.api.repo.EmployeeRepository;

import javax.inject.Inject;

/**
 * Abstract use case for Employees, which provides access to the commonly necessary data access objects.
 */
public class AbstractEmployeeUc extends AbstractUc {

	  /** @see #getEmployeeRepository() */
	  @Inject
    private EmployeeRepository employeeRepository;

    /**
     * Returns the field 'employeeRepository'.
     * @return the {@link EmployeeRepository} instance.
     */
    public EmployeeRepository getEmployeeRepository() {

      return this.employeeRepository;
    }

}
