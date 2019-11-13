<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.${pojo.name?lower_case}overview;

import javax.faces.application.FacesMessage;

import org.springframework.faces.model.OneSelectionTrackingListDataModel;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.gui.${variables.component}.common.Abstract${pojo.name}Controller;

/**
 * Controller for the overview dialog of ${pojo.name} data.
 */
public class ${pojo.name}OverviewController extends Abstract${pojo.name}Controller {

    /**
     * @param model
     *            {@link ${pojo.name}OverviewModel} used to create the ${pojo.name} overview
     */
    public void create${pojo.name}Overview(${pojo.name}OverviewModel model) {
        model.set${pojo.name}s(new OneSelectionTrackingListDataModel(this.coreWrapper.getAll${pojo.name}s()));
    }

    /**
     * Deletes a ${pojo.name}.
     *
     * @param model
     *            The {@link ${pojo.name}OverviewModel} .
     * @return {@link Boolean#TRUE} if the deleting the object was successful. {@link Boolean#FALSE}
     *         otherwise.
     */
    public Boolean delete${pojo.name}(${pojo.name}OverviewModel model) {
        ${pojo.name} ${pojo.name?uncap_first} = model.getSelected${pojo.name}();
        this.coreWrapper.delete${pojo.name}(${pojo.name?uncap_first});

        <@defineAndRetrieveAllIds/>
        displayFacesMessage(FacesMessage.SEVERITY_INFO, "${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' deleted.");
        return true;
    }
}
