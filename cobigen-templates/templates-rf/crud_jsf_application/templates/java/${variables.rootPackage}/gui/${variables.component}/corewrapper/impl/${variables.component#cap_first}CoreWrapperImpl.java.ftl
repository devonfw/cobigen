<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.corewrapper.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ${variables.rootPackage}.core.${variables.component}.${variables.component?cap_first};
import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};
import ${variables.rootPackage}.gui.${variables.component}.corewrapper.${variables.component?cap_first}CoreWrapper;

/**
 * Implementation of the ${variables.component} wrapper interface. This class communications this the application
 * core component '${variables.component?cap_first}'.
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
public class ${variables.component?cap_first}CoreWrapperImpl implements ${variables.component?cap_first}CoreWrapper {

    private final Logger LOG = Logger.getLogger(getClass());

    private ${variables.component?cap_first} ${pojo.name?uncap_first}Management;

    @Override
    public ${pojo.name} save${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {
        ${pojo.name?uncap_first}Management.update${pojo.name}(${pojo.name?uncap_first});
        <@defineAndRetrieveAllIds/>
        LOG.debug("${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' will be updated.");
        return true;
    }

    @Override
    public boolean delete${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {
        <@defineAndRetrieveAllIds/>
        LOG.debug("${pojo.name} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "' will be deleted.");
        return ${pojo.name?uncap_first}Management.delete${pojo.name}(${pojo.name?uncap_first});
    }

    @Override
    public List<${pojo.name}> getAll${pojo.name}s() {
        LOG.debug("Get all ${pojo.name?lower_case}s");
        return ${pojo.name?uncap_first}Management.getAll${pojo.name}s();
    }

    @Override
    public ${pojo.name} get${pojo.name}(<@insertIdParameter/>) {
        LOG.debug("Get ${pojo.name?lower_case} with id(s) '" + <@insertIdParameterValuesAsStringList/> + "'.");
        return ${pojo.name?uncap_first}Management.get${pojo.name}(<@insertIdParameterValues/>);
    }

	    /**
     * Sets the field '${pojo.name?lower_case}Management'.
     * @param ${pojo.name?lower_case}sManagement
     *            New value for ${pojo.name?lower_case}Management
     */
    public void set${variables.component?cap_first}(${variables.component?cap_first} ${pojo.name?lower_case}Management) {
        this.${pojo.name?uncap_first}Management = ${pojo.name?lower_case}Management;
    }
}
