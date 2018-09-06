<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.corewrapper;

import java.util.List;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};

/**
 * Wrapper interface for the core component '${variables.component?cap_first}'.
 */
public interface ${variables.component?cap_first}CoreWrapper {

    /**
     * Updates the given ${pojo.name}
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be updated
     * @return boolean true if the update was successful, otherwise false
     */
    public boolean save${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Deletes a ${pojo.name} from the database by its id 'id'.
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be deleted
     */
    public boolean delete${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Returns a list of all existing ${pojo.name}s.
     * @return List<${pojo.name}>
     *            list with all existing ${pojo.name} objects
     */
    public List<${pojo.name}> getAll${pojo.name}s();

    /**
     * Get a ${pojo.name}.
     <@insertIdParameterAsJavaDoc/>
     * @return ${pojo.name} ${pojo.name} for the given identifier
     */
    public ${pojo.name} get${pojo.name}(<@insertIdParameter/>);

}
