package com.example.domain.myapp.employeemanagement.common.api;

import com.example.domain.myapp.general.common.api.ApplicationEntity;
import javax.annotation.Generated;

public interface Employee extends ApplicationEntity {

    @Generated(value={"com.devon.CobiGen"},
        date="2022-07-05")
    private string field;

    public Employee() {}

    @Generated(value={"com.devon.CobiGen"},
        date="2022-05-05")
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