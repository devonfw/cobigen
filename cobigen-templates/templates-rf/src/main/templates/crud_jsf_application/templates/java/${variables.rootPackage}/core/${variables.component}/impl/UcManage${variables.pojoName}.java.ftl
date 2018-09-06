<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component}.impl;

import ${variables.rootPackage}.common.datatype.${pojo.name}State;
import ${variables.rootPackage}.common.exception.ValidationException;
import ${variables.rootPackage}.core.${variables.component}.common.Abstract${pojo.name}Uc;
import ${variables.rootPackage}.core.${variables.component}.common.${variables.component?cap_first}ExceptionKeys;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * Use case managing ${pojo.name} data.
 */
public class UcManage${pojo.name} extends Abstract${pojo.name}Uc {

    /**
     * Updates a new ${pojo.name}
     *
     * @param ${pojo.name?uncap_first} ${pojo.name} to be updated
     */
    public void save${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {

          this.${pojo.name?uncap_first}Dao.save(${pojo.name?uncap_first});
          <@defineAndRetrieveAllIds/>
          LOG.debug("${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' has been created.");
    }

    /**
     * Deletes a ${pojo.name} from the database by its id(s) <@insertIdParameter/>.
     * 
	 * @param id ${pojo.name} id
     * @return boolean {@link Boolean#TRUE} if the ${pojo.name?lower_case} can be deleted, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}} is unknown to the database (id unknown or ${pojo.name?lower_case}
     *         == null)
     */
    public boolean delete${pojo.name}(<@insertIdParameter/>) {

        validateProvidedEntity( ${pojo.name?uncap_first}, ${variables.component?cap_first}ExceptionKeys.NO_SUCH_${pojo.name?upper_case}, "${pojo.name} unknown to the database");

        this.${pojo.name?uncap_first}Dao.delete(<@insertIdParameterValues/>);
        LOG.debug("The ${pojo.name?uncap_first} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' has been deleted.");

        return true;
    }
}
