<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.corewrapper;

import java.util.List;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};

/**
 * Wrapper interface '${pojo.name}CoreWrapper' for the core component '${variables.component}'.
 * @generated
 */
public interface ${pojo.name}ManagementCoreWrapper {

    /**
     * Creates a new ${pojo.name}
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be created
     * @return boolean true if the creation was successful, otherwise false
     */
    public boolean create${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Updates the given ${pojo.name}
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be updated
     * @return boolean true if the update was successful, otherwise false
     */
    public boolean update${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

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
