<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component};

import java.util.List;

import ${variables.rootPackage}.core.${variables.component}.to.${pojo.name}To;
import ${variables.rootPackage}.common.exception.ValidationException;

/**
 * Component interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} {

	/**
     * Get a ${pojo.name}.
     *
     <@insertIdParameterAsJavaDoc/>
     * @return {@link ${pojo.name}} object
     */
	public ${pojo.name}To get${pojo.name}(<@insertIdParameter/>);

	/**
     * Returns a list of all existing ${pojo.name}s.
     *
     * @return {@link List} of {@link ${pojo.name}}s with all existing ${pojo.name} objects
     */
    public List<${pojo.name}To> getAll${pojo.name}s();

    /**
     * Updates the given ${pojo.name}
     *
     * @param ${pojo.name?uncap_first}
     *            ${pojo.name} to be updated
     * @throws ValidationException The {@link ${pojo.name}To} is also in the database
     */
    public void save${pojo.name}(${pojo.name}To ${pojo.name?uncap_first});

    /**
     * Deletes a ${pojo.name}
     *
     * @param id
     *            ${pojo.name} id
     * @return {@link Boolean#TRUE} if successful, {@link Boolean#FALSE} otherwise
     * @throws ValidationException The {@link ${pojo.name}To} is unknown to the database (id unknown or ${pojo.name}
     *         == null)
     */
    public boolean delete${pojo.name}(<@insertIdParameter/>);
}
