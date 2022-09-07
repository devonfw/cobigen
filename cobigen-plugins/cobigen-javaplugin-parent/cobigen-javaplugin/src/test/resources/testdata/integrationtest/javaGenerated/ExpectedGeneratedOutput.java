package com.example.domain.myapp.employeemanagement.common.api.;

import com.example.domain.myapp.general.common.api.ApplicationEntity;

public interface Employee extends ApplicationEntity {

  @Generated(value = { "CobiGen" }, date = "2022-08-31T01:10:20+0000")
  private string field;

  @Generated(value = { "CobiGen" }, date = "2022-08-31T01:10:20+0000")
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
   }

}
