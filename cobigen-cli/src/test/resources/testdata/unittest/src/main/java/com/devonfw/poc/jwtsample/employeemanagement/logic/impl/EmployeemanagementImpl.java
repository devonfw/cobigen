package com.devonfw.poc.jwtsample.employeemanagement.logic.impl;

import com.devonfw.poc.jwtsample.general.logic.base.AbstractComponentFacade;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.Employeemanagement;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeEto;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.usecase.UcFindEmployee;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.usecase.UcManageEmployee;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeSearchCriteriaTo;
import org.springframework.data.domain.Page;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of component interface of employeemanagement
 */
@Named
public class EmployeemanagementImpl extends AbstractComponentFacade implements Employeemanagement {

	@Inject
	private UcFindEmployee ucFindEmployee;

	@Inject
	private UcManageEmployee ucManageEmployee;

	@Override
	public EmployeeEto findEmployee(long id) {

      return this.ucFindEmployee.findEmployee(id);
    }

	@Override
	public Page<EmployeeEto> findEmployees(EmployeeSearchCriteriaTo criteria) {
      return this.ucFindEmployee.findEmployees(criteria);
    }

	@Override
	public EmployeeEto saveEmployee(EmployeeEto employee) {

      return this.ucManageEmployee.saveEmployee(employee);
    }

	@Override
	public boolean deleteEmployee(long id) {

      return this.ucManageEmployee.deleteEmployee(id);
    }

	@Override
	public EmployeeCto findEmployeeCto(long id) {
    
      return ucFindEmployee.findEmployeeCto(id);
    }

	@Override
	public Page<EmployeeCto> findEmployeeCtos(EmployeeSearchCriteriaTo criteria) {
    
      return ucFindEmployee.findEmployeeCtos(criteria);
    }

}
