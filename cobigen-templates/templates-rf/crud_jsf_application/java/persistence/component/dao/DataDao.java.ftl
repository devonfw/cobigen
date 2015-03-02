<#include '/makros.ftl'>
package ${variables.rootPackage}.persistence.${variables.component}.dao;

import java.util.List;

import ${variables.rootPackage}.persistence.common.DomainDao;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * @generated
 */
public interface ${pojo.name}Dao extends DomainDao<${pojo.name}, <@insertIdObjectType/>> {

    /**
     * Deletes the given ${pojo.name}
     */
    void delete(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Returns a list of all existing ${pojo.name?uncap_first}s.
     *
     * @return {@link List} of all existing {@link ${pojo.name}}s.
     */
    List<${pojo.name}> getAll${pojo.name}s();

}
