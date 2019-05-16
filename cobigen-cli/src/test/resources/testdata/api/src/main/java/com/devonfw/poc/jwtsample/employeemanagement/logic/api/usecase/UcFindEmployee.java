package com.devonfw.poc.jwtsample.employeemanagement.logic.api.usecase;

import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeEto;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeSearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UcFindEmployee {

	/**
	 * Returns a Employee by its id 'id'.
	 *
	 * @param id The id 'id' of the Employee.
	 * @return The {@link EmployeeEto} with id 'id'
	 */
	EmployeeEto findEmployee(long id);

	/**
	 * Returns a paginated list of Employees matching the search criteria.
	 *
	 * @param criteria the {@link EmployeeSearchCriteriaTo}.
	 * @return the {@link List} of matching {@link EmployeeEto}s.
	 */
	Page<EmployeeEto> findEmployees(EmployeeSearchCriteriaTo criteria);

}
