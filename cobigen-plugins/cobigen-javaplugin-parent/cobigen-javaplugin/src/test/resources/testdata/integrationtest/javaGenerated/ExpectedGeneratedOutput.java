package com.example.domain.myapp.employeemanagement.common.api;

import com.example.domain.myapp.general.common.api.ApplicationEntity;

public interface Employee extends ApplicationEntity {

  @Generated( value= {"com.devonfw.cobigen"},
      date ="21/09/22")
  private string field;

  @Generated( value= {"com.devonfw.cobigen"},
      date ="21/09/22")
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
