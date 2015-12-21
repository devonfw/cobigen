<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component}.impl;

import java.util.List;

import ${variables.rootPackage}.core.${variables.component}.common.Abstract${pojo.name}Uc;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * Use case for retrieving ${pojo.name} data.
 */
public class UcFind${pojo.name} extends Abstract${pojo.name}Uc {

    /**
     * Returns a ${pojo.name} by its it
     *
	   <@insertIdParameterAsJavaDoc/>
     * @return The {@link ${pojo.name}} with the given parameters
     */
    public ${pojo.name} get${pojo.name}(<@insertIdParameter/>) {
        LOG.debug("Get ${pojo.name?lower_case} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' from database.");
        return this.${pojo.name?uncap_first}Dao.searchById(<@insertIdParameterValues/>);
    }

    /**
     * Returns a list of all existing ${pojo.name}s.
     *
     * @return {@link List} of all existing {@link ${pojo.name}}s
     */
    public List<${pojo.name}> getAll${pojo.name}s() {
        LOG.debug("Get all ${pojo.name?lower_case}s from database.");
        return this.${pojo.name?uncap_first}Dao.getAll${pojo.name}s();
    }

}
