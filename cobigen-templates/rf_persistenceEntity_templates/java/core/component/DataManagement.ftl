<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component};

import java.util.List;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.common.exception.ValidationException;

/**
 * Interface for ${pojo.name}Management component.
 * @generated
 */
public interface ${pojo.name}Management {

	/**
     * Get a ${pojo.name}.
     *
     <@insertIdParameterAsJavaDoc/>
     * @return {@link ${pojo.name}} object
     */
	public ${pojo.name} get${pojo.name}(<@insertIdParameter/>);

	/**
     * Returns a list of all existing ${pojo.name}s.
     *
     * @return {@link List} of {@link ${pojo.name}}s with all existing ${pojo.name} objects
     */
    public List<${pojo.name}> getAll${pojo.name}s();

    /**
     * Creates a new ${pojo.name}
     *
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be created
     * @return {@link Boolean#TRUE} if successful, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}} is also in the database
     */
    public boolean create${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Updates the given ${pojo.name}
     *
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be updated
     * @return {@link Boolean#TRUE} if successful, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}} is also in the database
     */
    public boolean update${pojo.name}(${pojo.name} ${pojo.name?uncap_first});

    /**
     * Deletes a ${pojo.name}
     *
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be deleted
     * @return {@link Boolean#TRUE} if successful, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}} is unknown to the database (id unknown or ${pojo.name}
     *         == null)
     */
    public boolean delete${pojo.name}(${pojo.name} ${pojo.name?uncap_first});
}
