<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component}.impl;

import java.util.List;

import ${variables.rootPackage}.common.AbstractLayerImpl;
import ${variables.rootPackage}.common.datatype.${pojo.name}State;
import ${variables.rootPackage}.core.${variables.component}.${variables.component?cap_first};
import ${variables.rootPackage}.core.${variables.component}.to.${pojo.name}To;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * Implementation of the compnent interface of component ${variables.component?cap_first}.
 */
public class ${variables.component?cap_first}Impl extends AbstractLayerImpl implements ${variables.component?cap_first} {

    private UcFind${pojo.name} ucFind${pojo.name};

    private UcManage${pojo.name} ucManage${pojo.name};

    /**
     * The constructor.
     */
    public ${variables.component?cap_first}Impl() {
        super();
    }

    @Override
    public ${pojo.name}To get${pojo.name}(<@insertIdParameter/>) {
        ${pojo.name} persistence${pojo.name} = ucFind${pojo.name}.get${pojo.name}(<@insertIdParameterValues/>);
        ${pojo.name}To core${pojo.name} = mapInitialToTargetLayerEntity(persistence${pojo.name},
                ${pojo.name}To.class);

        return core${pojo.name};
    }

    @Override
    public List<${pojo.name}To> getAll${pojo.name}s() {
        List<${pojo.name}> persistence${pojo.name}s = ucFind${pojo.name}.getAll${pojo.name}s();
        List<${pojo.name}To> core${pojo.name}s = mapInitialToTargetLayerEntity(persistence${pojo.name}s,
                ${pojo.name}To.class);

        return core${pojo.name}s;
    }

    @Override
    public void save${pojo.name}(${pojo.name}To ${pojo.name?uncap_first}) {
        ${pojo.name} persistence${pojo.name} =
            this.mapInitialToTargetLayerEntity(${pojo.name?uncap_first},
                ${pojo.name}.class);
        this.ucManage${pojo.name}.save${pojo.name}(persistence${pojo.name});
    }

    @Override
    public boolean delete${pojo.name}(<@insertIdParameter/>) {
        return this.ucManage${pojo.name}.delete${pojo.name}(<@insertIdParameterValues/>);
    }
	
	    /**
     * Sets the field 'ucFind${pojo.name}'.
     * @param ucFind${pojo.name}
     *            New value for ucFind${pojo.name}
     */
    public void setUcFind${pojo.name}(UcFind${pojo.name} ucFind${pojo.name}) {
        this.ucFind${pojo.name} = ucFind${pojo.name};
    }

    /**
     * Sets the field 'ucManage${pojo.name}'.
     * @param ucManage${pojo.name}
     *            New value for ucManage${pojo.name}
     */
    public void setUcManage${pojo.name}(UcManage${pojo.name} ucManage${pojo.name}) {
        this.ucManage${pojo.name} = ucManage${pojo.name};
    }

}
