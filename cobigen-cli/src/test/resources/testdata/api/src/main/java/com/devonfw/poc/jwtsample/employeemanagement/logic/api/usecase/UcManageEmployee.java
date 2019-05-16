package com.devonfw.poc.jwtsample.employeemanagement.logic.api.usecase;

import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeEto;

/**
 * Interface of UcManageEmployee to centralize documentation and signatures of methods.
 */
public interface UcManageEmployee {

	/**
	 * Deletes a employee from the database by its id 'employeeId'.
	 *
	 * @param employeeId Id of the employee to delete
	 * @return boolean <code>true</code> if the employee can be deleted, <code>false</code> otherwise
	 */
	boolean deleteEmployee(long employeeId);

	/**
	 * Saves a employee and store it in the database.
	 *
	 * @param employee the {@link EmployeeEto} to create.
	 * @return the new {@link EmployeeEto} that has been saved with ID and version.
	 */
	EmployeeEto saveEmployee(EmployeeEto employee);

}
