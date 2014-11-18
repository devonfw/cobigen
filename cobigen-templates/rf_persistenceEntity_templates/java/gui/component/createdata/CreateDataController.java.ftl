<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case};

import javax.faces.application.FacesMessage;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.gui.${variables.component}.common.Abstract${pojo.name}Controller;


/**
 * Controller class for create${pojo.name}
 *
 * @generated
 */
public class Create${pojo.name}Controller extends Abstract${pojo.name}Controller {

    /**
     * creates a new ${pojo.name?lower_case}
     *
     * @param model
     *            the {@link Create${pojo.name}Model} used to create the ${pojo.name?lower_case} object
     * @return {@link Boolean#TRUE} if the creation was successful. {@link Boolean#FALSE} otherwise.
     */
    public boolean create${pojo.name}(Create${pojo.name}Model model) {

	      ${pojo.name} ${pojo.name?uncap_first} = new ${pojo.name}();
		    <#list doc["/doc/pojo/attributes"] as attr>
        ${pojo.name?uncap_first}.set${attr.name?cap_first}(model.get${attr.name?cap_first}());
        </#list>
        this.coreWrapper.create${pojo.name}(${pojo.name?uncap_first});

        <@defineAndRetrieveAllIds/>
        displayFacesMessage(FacesMessage.SEVERITY_INFO, "${pojo.name?lower_case} with id '" + <@insertIdParameterValuesAsStringList/> + "' created.");
        return true;
    }
}
