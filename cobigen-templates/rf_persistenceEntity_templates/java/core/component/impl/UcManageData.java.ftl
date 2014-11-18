<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component}.impl;

import ${variables.rootPackage}.common.datatype.${pojo.name}State;
import ${variables.rootPackage}.common.exception.ValidationException;
import ${variables.rootPackage}.core.${variables.component}.common.Abstract${pojo.name}Uc;
import ${variables.rootPackage}.core.${variables.component}.common.ExceptionKeys;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * @generated
 */
public class UcManage${pojo.name} extends Abstract${pojo.name}Uc {

    /**
     * Creates a new ${pojo.name}
     *
     * @param ${pojo.name?uncap_first} ${pojo.name} to create
     * @return boolean {@link Boolean#TRUE} if the creation was successful, {@link Boolean#FALSE} if a ${pojo.name}
     *         with this number already exists or if the given id is not allowed
     */
    public boolean create${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {

        /*
         * Validation
         */
        <@defineAndRetrieveAllIds/>
        if (this.${pojo.name?uncap_first}Dao.searchById(<@insertIdParameterValues/>) == null) {

            // TODO implement further validation

            this.${pojo.name?uncap_first}Dao.save(${pojo.name?uncap_first});
            LOG.debug("${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' has been created.");
            return true;
        }

        // ${pojo.name} already exists
        throw new ValidationException(ExceptionKeys.${pojo.name?upper_case}_ALREADY_EXISTS, <@insertIdParameterAsListOfStrings/>);
    }

    /**
     * Updates a new ${pojo.name}
     *
     * @param ${pojo.name?uncap_first} ${pojo.name} to be updated
     * @return boolean {@link Boolean#TRUE} if the update was successful, {@link Boolean#FALSE} if a ${pojo.name}
     *        already exists or if the given id is not allowed
     */
    public boolean update${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {

          // TODO implement further validation

          this.${pojo.name?uncap_first}Dao.save(${pojo.name?uncap_first});
          <@defineAndRetrieveAllIds/>
          LOG.debug("${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' has been created.");
          return true;
    }

    /**
     * Deletes a ${pojo.name} from the database by its id(s) <@insertIdParameter/>.
     *
     * @param ${pojo.name?uncap_first} ${pojo.name} to delete
     * @return boolean {@link Boolean#TRUE} if the ${pojo.name?lower_case} can be deleted, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}} is unknown to the database (id unknown or ${pojo.name?lower_case}
     *         == null)
     */
    public boolean delete${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {

        validateProvidedEntity( ${pojo.name?uncap_first}, ExceptionKeys.NO_SUCH_${pojo.name?upper_case}, "${pojo.name} unknown to the database");

        //TODO implement further validation

        this.${pojo.name?uncap_first}Dao.delete(${pojo.name?uncap_first});
        <@defineAndRetrieveAllIds/>
        LOG.debug("The ${pojo.name?uncap_first} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' has been deleted.");

        return true;
    }
}
