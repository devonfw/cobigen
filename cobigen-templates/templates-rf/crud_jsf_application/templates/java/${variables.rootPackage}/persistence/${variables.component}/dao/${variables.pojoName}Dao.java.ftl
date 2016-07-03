<#include '/makros.ftl'>
package ${variables.rootPackage}.persistence.${variables.component}.dao;

import java.util.List;

import ${variables.rootPackage}.persistence.common.DomainDao;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * Interface for the data access object implementation.
 */
public interface ${pojo.name}Dao extends DomainDao<${pojo.name}, <@insertIdObjectType/>> {

    /**
     * Deletes the given ${pojo.name}
     */
    void delete(<@insertIdParameter/>);

    /**
     * Returns a list of all existing ${pojo.name?uncap_first}s.
     *
     * @return {@link List} of all existing ${pojo.name}s.
     */
    List<${pojo.name}> getAll${pojo.name}s();

}
