package ${variables.rootPackage}.${variables.component}.common.api.${variables.detail};

import ${variables.rootPackage}.general.common.api.ApplicationEntity;

public interface ${variables.entityName} extends ApplicationEntity {

  private string field;


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
