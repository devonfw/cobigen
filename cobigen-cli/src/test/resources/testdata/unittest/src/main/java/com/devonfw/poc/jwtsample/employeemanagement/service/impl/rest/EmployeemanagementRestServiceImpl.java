package com.devonfw.poc.jwtsample.employeemanagement.service.impl.rest;

import com.devonfw.poc.jwtsample.employeemanagement.common.api.Employee;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.Employeemanagement;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeEto;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeSearchCriteriaTo;
import com.devonfw.poc.jwtsample.employeemanagement.service.api.rest.EmployeemanagementRestService;
import org.springframework.data.domain.Page;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * The service implementation for REST calls in order to execute the logic of component {@link Employeemanagement}.
 */
@Named("EmployeemanagementRestService")
public class EmployeemanagementRestServiceImpl implements EmployeemanagementRestService {

	@Inject
	private Employeemanagement employeemanagement;

	@Override
	public EmployeeEto getEmployee(long id) {
    return this.employeemanagement.findEmployee(id);
  }

	@Override
	public EmployeeEto saveEmployee(EmployeeEto employee) {
      return this.employeemanagement.saveEmployee(employee);
  }

	@Override
	public void deleteEmployee(long id) {
    this.employeemanagement.deleteEmployee(id);
  }

	@Override
	public Page<EmployeeEto> findEmployees(EmployeeSearchCriteriaTo searchCriteriaTo) {
    return this.employeemanagement.findEmployees(searchCriteriaTo);
  }

	@Override
	public EmployeeCto getEmployeeCto(long id) {
    return this.employeemanagement.findEmployeeCto(id);
  }

	@Override
	public Page<EmployeeCto> findEmployeeCtos(EmployeeSearchCriteriaTo searchCriteriaTo) {
    return this.employeemanagement.findEmployeeCtos(searchCriteriaTo);
  }

}
