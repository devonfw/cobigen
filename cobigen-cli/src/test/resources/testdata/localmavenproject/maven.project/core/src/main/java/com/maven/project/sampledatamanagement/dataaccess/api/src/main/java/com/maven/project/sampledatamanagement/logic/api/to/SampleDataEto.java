package com.maven.project.sampledatamanagement.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractEto;
import com.maven.project.sampledatamanagement.common.api.SampleData;

import java.util.List;
import java.util.Set;

/**
 * Entity transport object of SampleData
 */
public class SampleDataEto extends AbstractEto implements SampleData {

	private static final long serialVersionUID = 1L;

	private String name;
	private String surname;
	private Integer age;
	private String mail;

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
	public Integer getAge() {
		return age;
	}
	@Override
	public void setAge(Integer age) {
		this.age = age;
	}
  @Override
	public String getMail() {
		return mail;
	}
	@Override
	public void setMail(String mail) {
		this.mail = mail;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
					result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
					result = prime * result + ((this.surname == null) ? 0 : this.surname.hashCode());
					result = prime * result + ((this.age == null) ? 0 : this.age.hashCode());
					result = prime * result + ((this.mail == null) ? 0 : this.mail.hashCode());
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
    SampleDataEto other = (SampleDataEto) obj;
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
		if (this.age == null) {
		  if (other.age != null) {
			return false;
		  }
		} else if(!this.age.equals(other.age)){
		  return false;
		}
		if (this.mail == null) {
		  if (other.mail != null) {
			return false;
		  }
		} else if(!this.mail.equals(other.mail)){
		  return false;
		}
    return true;
  }
}
