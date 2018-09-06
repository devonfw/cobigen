<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.save${pojo.name?lower_case};

import javax.faces.application.FacesMessage;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.gui.${variables.component}.common.Abstract${pojo.name}Controller;


/**
 * Controller class for creating ${pojo.name} data.
 */
public class Create${pojo.name}Controller extends Abstract${pojo.name}Controller {

    /**
     * Creates a new ${pojo.name?lower_case}.
     *
     * @param model
     *            the {@link Create${pojo.name}Model} used to save the ${pojo.name?lower_case} object
     * @return the saved ${pojo.name}.
     */
    public ${pojo.name} create${pojo.name}(Create${pojo.name}Model model) {

	      ${pojo.name} ${pojo.name?uncap_first} = new ${pojo.name}();
		    <#list doc["/doc/pojo/fields"] as attr>
        ${pojo.name?uncap_first}.set${attr.name?cap_first}(model.get${attr.name?cap_first}());
        </#list>
        this.coreWrapper.save${pojo.name}(${pojo.name?uncap_first});

        <@defineAndRetrieveAllIds/>
        displayFacesMessage(FacesMessage.SEVERITY_INFO, "${pojo.name?lower_case} with id '" + <@insertIdParameterValuesAsStringList/> + "' created.");
        return true;
    }
}
