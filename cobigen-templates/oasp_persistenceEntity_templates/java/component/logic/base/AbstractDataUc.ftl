package ${variables.rootPackage}.${variables.component}.logic.base;

import ${variables.rootPackage}.general.logic.base.AbstractUc;
import ${variables.rootPackage}.${variables.component}.persistence.api.dao.${variables.entityName}Dao;

/**
 * Abstract use case for ${variables.entityName}s, which provides access to the commonly necessary data access objects.
 */
public class Abstract${variables.entityName}Uc extends AbstractUc {

	/** @see #get${variables.entityName}Dao() */
    protected ${variables.entityName}Dao ${variables.entityName?uncap_first}Dao;
    
    /**
     * Returns the field '${variables.entityName?uncap_first}Dao'.
     * @return the {@link ${variables.entityName}Dao} instance.
     */
    public ${variables.entityName}Dao get${variables.entityName}Dao() {

      return this.${variables.entityName?uncap_first}Dao;
    }

    /**
     * Sets the field '${variables.entityName?uncap_first}Dao'.
     * @param ${variables.entityName?uncap_first}Dao
     *            New value for ${variables.entityName?uncap_first}Dao
     */
    public void set${variables.entityName}Dao(${variables.entityName}Dao ${variables.entityName?uncap_first}Dao) {
        this.${variables.entityName?uncap_first}Dao = ${variables.entityName?uncap_first}Dao;
    }

}
