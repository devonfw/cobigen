<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case};

import java.util.List;

import org.springframework.faces.model.OneSelectionTrackingListDataModel;

import ${variables.rootPackage}.common.datatype.${pojo.name}State;
import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.gui.${variables.component}.common.Abstract${pojo.name}Controller;

/**
 * Controller for the dialog managing ${pojo.name} data.
 */
public class Manage${pojo.name}Controller extends Abstract${pojo.name}Controller {

    /**
     * Updates the current ${pojo.name}
     *
     * @param model
     *            the {@link Manage${pojo.name}Model} used to save the ${pojo.name}
     * @return the saved ${pojo.name}.
     */
    public ${pojo.name} update${pojo.name}(Manage${pojo.name}Model model) {
        ${pojo.name} ${pojo.name?uncap_first} = model.getSelected${pojo.name}();
        this.coreWrapper.save${pojo.name}(${pojo.name?uncap_first});

        <@defineAndRetrieveAllIds/>
        displayFacesMessage(FacesMessage.SEVERITY_INFO, "${pojo.name?lower_case} with id '" + <@insertIdParameterValuesAsStringList/> + "' updated.");
        return true;
    }

}
