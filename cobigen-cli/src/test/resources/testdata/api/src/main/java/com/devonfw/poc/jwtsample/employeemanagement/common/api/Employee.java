package com.devonfw.poc.jwtsample.employeemanagement.common.api;

import com.devonfw.poc.jwtsample.general.common.api.ApplicationEntity;

public interface Employee extends ApplicationEntity {

	/**
	 * @return employeeIdId
	 */
	public Long getEmployeeId();

	/**
	 * @param employeeId
setter for employeeId attribute
	 */
	public void setEmployeeId(Long employeeId);

	/**
	 * @return nameId
	 */
	public String getName();

	/**
	 * @param name
setter for name attribute
	 */
	public void setName(String name);

	/**
	 * @return surnameId
	 */
	public String getSurname();

	/**
	 * @param surname
setter for surname attribute
	 */
	public void setSurname(String surname);

	/**
	 * @return emailId
	 */
	public String getEmail();

	/**
	 * @param email
setter for email attribute
	 */
	public void setEmail(String email);

}
