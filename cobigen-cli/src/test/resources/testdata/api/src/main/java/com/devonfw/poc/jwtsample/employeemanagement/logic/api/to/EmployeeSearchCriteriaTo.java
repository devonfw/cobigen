package com.devonfw.poc.jwtsample.employeemanagement.logic.api.to;

import com.devonfw.module.basic.common.api.query.StringSearchConfigTo;
import com.devonfw.poc.jwtsample.general.common.api.to.AbstractSearchCriteriaTo;

/**
 * {@link SearchCriteriaTo} to find instances of {@link com.devonfw.poc.jwtsample.employeemanagement.common.api.Employee}s.
 */
public class EmployeeSearchCriteriaTo extends AbstractSearchCriteriaTo {

	private static final long serialVersionUID = 1L;

	private Long employeeId;

	private String name;

	private String surname;

	private String email;

	private StringSearchConfigTo nameOption;

	private StringSearchConfigTo surnameOption;

	private StringSearchConfigTo emailOption;

	/**
	 * @return employeeIdId
	 */
	public Long getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId
setter for employeeId attribute
	 */
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return nameId
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
setter for name attribute
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return surnameId
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname
setter for surname attribute
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return emailId
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
setter for email attribute
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the {@link StringSearchConfigTo} used to search for {@link #getName() name}.
	 */
	public StringSearchConfigTo getNameOption() {

		return this.nameOption;
	}

	/**
	 * @param nameOption new value of {@link #getNameOption()}.
	 */
	public void setNameOption(StringSearchConfigTo nameOption) {

		this.nameOption =nameOption;
	}

	/**
	 * @return the {@link StringSearchConfigTo} used to search for {@link #getSurname() surname}.
	 */
	public StringSearchConfigTo getSurnameOption() {

		return this.surnameOption;
	}

	/**
	 * @param surnameOption new value of {@link #getSurnameOption()}.
	 */
	public void setSurnameOption(StringSearchConfigTo surnameOption) {

		this.surnameOption =surnameOption;
	}

	/**
	 * @return the {@link StringSearchConfigTo} used to search for {@link #getEmail() email}.
	 */
	public StringSearchConfigTo getEmailOption() {

		return this.emailOption;
	}

	/**
	 * @param emailOption new value of {@link #getEmailOption()}.
	 */
	public void setEmailOption(StringSearchConfigTo emailOption) {

		this.emailOption =emailOption;
	}

}
