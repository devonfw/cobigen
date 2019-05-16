package com.devonfw.poc.jwtsample.employeemanagement.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractEto;
import com.devonfw.poc.jwtsample.employeemanagement.common.api.Employee;
import java.util.List;
import java.util.Set;

/**
 * Entity transport object of Employee
 */
public class EmployeeEto extends AbstractEto implements Employee {

	private static final long serialVersionUID = 1L;

	private Long employeeId;

	private String name;

	private String surname;

	private String email;

	@Override
	public Long getEmployeeId() {
		return employeeId;
	}

	@Override
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getSurname() {
		return surname;
	}

	@Override
	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
					result = prime * result + ((this.employeeId == null) ? 0 : this.employeeId.hashCode());
					result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
					result = prime * result + ((this.surname == null) ? 0 : this.surname.hashCode());
					result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        return result;
    }

	@Override
	public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    // class check will be done by super type EntityTo!
    if (!super.equals(obj)) {
      return false;
    }
    EmployeeEto other = (EmployeeEto) obj;
		if (this.employeeId == null) {
		  if (other.employeeId != null) {
			return false;
		  }
		} else if(!this.employeeId.equals(other.employeeId)){
		  return false;
		}
		if (this.name == null) {
		  if (other.name != null) {
			return false;
		  }
		} else if(!this.name.equals(other.name)){
		  return false;
		}
		if (this.surname == null) {
		  if (other.surname != null) {
			return false;
		  }
		} else if(!this.surname.equals(other.surname)){
		  return false;
		}
		if (this.email == null) {
		  if (other.email != null) {
			return false;
		  }
		} else if(!this.email.equals(other.email)){
		  return false;
		}
    return true;
  }

}
