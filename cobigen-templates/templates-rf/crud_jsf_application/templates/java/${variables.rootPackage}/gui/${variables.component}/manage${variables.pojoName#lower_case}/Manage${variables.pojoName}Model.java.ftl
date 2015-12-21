<#include '/makros.ftl'>
package ${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case};

import java.io.Serializable;

import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};

/**
 * Model for the dialoag managing ${pojo.name} data.
 */
public class Manage${pojo.name}Model implements Serializable {

    private ${pojo.name} selected${pojo.name};

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
