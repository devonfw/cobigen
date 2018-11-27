package ${variables.rootPackage}.core.${variables.component}.common;

import ${variables.rootPackage}.core.common.AbstractUc;
import ${variables.rootPackage}.persistence.${variables.component}.dao.${pojo.name}Dao;

/**
 * Common use case for all use cases accessing ${pojo.name} data.
 */
public class Abstract${pojo.name}Uc extends AbstractUc {

    protected ${pojo.name}Dao ${pojo.name?uncap_first}Dao;

    /**
     * Sets the field '${pojo.name?uncap_first}Dao'.
     * @param ${pojo.name?uncap_first}Dao
     *            New value for ${pojo.name?uncap_first}Dao
     */
    public void set${pojo.name}Dao(${pojo.name}Dao ${pojo.name?uncap_first}Dao) {
        this.${pojo.name?uncap_first}Dao = ${pojo.name?uncap_first}Dao;
    }

}
