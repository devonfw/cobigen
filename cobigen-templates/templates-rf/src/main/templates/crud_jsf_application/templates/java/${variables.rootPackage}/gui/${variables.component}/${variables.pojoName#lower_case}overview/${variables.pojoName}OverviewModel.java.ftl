<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.${pojo.name?lower_case}overview;

import java.io.Serializable;

import javax.faces.model.DataModel;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};

/**
 * Model for the overview dialog of ${pojo.name} data.
 */
public class ${pojo.name}OverviewModel implements Serializable {

    private DataModel<${pojo.name}> ${pojo.name?uncap_first}s;

    private ${pojo.name} selected${pojo.name};

    /**
     * Returns the field '${pojo.name?uncap_first}s'.
     * @return Value of ${pojo.name?uncap_first}s
     */
    public DataModel<${pojo.name}> get${pojo.name}s() {
        return ${pojo.name?uncap_first}s;
    }

    /**
     * Sets the field '${pojo.name?uncap_first}s'.
     * @param ${pojo.name?uncap_first}s
     *            New value for ${pojo.name?uncap_first}s
     */
    public void set${pojo.name}s(DataModel<${pojo.name}> ${pojo.name?uncap_first}s) {
        this.${pojo.name?uncap_first}s = ${pojo.name?uncap_first}s;
    }

    /**
     * Returns the field 'selected${pojo.name}'.
     * @return Value of selected${pojo.name}
     */
    public ${pojo.name} getSelected${pojo.name}() {
        return selected${pojo.name};
    }

    /**
     * Sets the field 'selected${pojo.name}'.
     * @param selected${pojo.name}
     *            New value for selected${pojo.name}
     */
    public void setSelected${pojo.name}(${pojo.name} selected${pojo.name}) {
        this.selected${pojo.name} = selected${pojo.name};
    }
}
