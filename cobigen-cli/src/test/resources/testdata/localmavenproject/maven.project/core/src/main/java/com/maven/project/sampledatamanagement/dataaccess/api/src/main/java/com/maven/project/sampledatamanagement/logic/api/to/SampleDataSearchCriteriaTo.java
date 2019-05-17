package com.maven.project.sampledatamanagement.logic.api.to;

import com.devonfw.module.basic.common.api.query.StringSearchConfigTo;
import com.maven.project.general.common.api.to.AbstractSearchCriteriaTo;

/**
 * {@link SearchCriteriaTo} to find instances of {@link com.maven.project.sampledatamanagement.common.api.SampleData}s.
 */
public class SampleDataSearchCriteriaTo extends AbstractSearchCriteriaTo {

  private static final long serialVersionUID = 1L;

	private String name;
	private String surname;
	private Integer age;
	private String mail;
			private StringSearchConfigTo nameOption;
			private StringSearchConfigTo surnameOption;
			private StringSearchConfigTo mailOption;

      /**
      * @return nameId
      */
  
	public String getName()  {
		return name;
	}
  /**
   * @param name
   *            setter for name attribute
   */
	
	public void setName(String name) {
		this.name = name;
	}
      /**
      * @return surnameId
      */
  
	public String getSurname()  {
		return surname;
	}
  /**
   * @param surname
   *            setter for surname attribute
   */
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
      /**
      * @return ageId
      */
  
	public Integer getAge()  {
		return age;
	}
  /**
   * @param age
   *            setter for age attribute
   */
	
	public void setAge(Integer age) {
		this.age = age;
	}
      /**
      * @return mailId
      */
  
	public String getMail()  {
		return mail;
	}
  /**
   * @param mail
   *            setter for mail attribute
   */
	
	public void setMail(String mail) {
		this.mail = mail;
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
	* @return the {@link StringSearchConfigTo} used to search for {@link #getMail() mail}.
	*/
	public StringSearchConfigTo getMailOption() {

		return this.mailOption;
	}

	/**
	* @param mailOption new value of {@link #getMailOption()}.
	*/
	public void setMailOption(StringSearchConfigTo mailOption) {

		this.mailOption =mailOption;
	}

}
