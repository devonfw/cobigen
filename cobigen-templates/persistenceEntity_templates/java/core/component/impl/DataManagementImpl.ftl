<#-- Copyright © Capgemini 2013. All rights reserved. -->
<#include '/makros.ftl'>
package ${variables.rootPackage}.core.${variables.component}.impl;

import java.util.List;

import ${variables.rootPackage}.common.AbstractLayerImpl;
import ${variables.rootPackage}.common.datatype.${pojo.name}State;
import ${variables.rootPackage}.core.${variables.component}.${pojo.name}Management;
import ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name};

/**
 * @generated
 */
public class ${pojo.name}ManagementImpl extends AbstractLayerImpl implements ${pojo.name}Management {

    private UcFind${pojo.name} ucFind${pojo.name};

    private UcManage${pojo.name} ucManage${pojo.name};

    /**
     * The constructor.
     */
    public ${pojo.name}ManagementImpl() {
        super();
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ${pojo.name} get${pojo.name}(<@insertIdParameter/>) {
        ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name} persistence${pojo.name};
        ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name} core${pojo.name};

        persistence${pojo.name} = ucFind${pojo.name}.get${pojo.name}(<@insertIdParameterValues/>);
        core${pojo.name} = mapInitialToTargetLayerEntity(persistence${pojo.name},
                ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name}.class);

        return core${pojo.name};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<${pojo.name}> getAll${pojo.name}s() {
        List<${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name}> persistence${pojo.name}s;
        List<${variables.rootPackage}.core.${variables.component}.entity.${pojo.name}> core${pojo.name}s;

        persistence${pojo.name}s = ucFind${pojo.name}.getAll${pojo.name}s();
        core${pojo.name}s = mapInitialToTargetLayerEntity(persistence${pojo.name}s,
                ${variables.rootPackage}.core.${variables.component}.entity.${pojo.name}.class);

        return core${pojo.name}s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean create${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {
        ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name} persistence${pojo.name};

        persistence${pojo.name} =
            this.mapInitialToTargetLayerEntity(${pojo.name?uncap_first},
                ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name}.class);
        return this.ucManage${pojo.name}.create${pojo.name}(persistence${pojo.name});
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {
        ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name} persistence${pojo.name};

        persistence${pojo.name} =
            this.mapInitialToTargetLayerEntity(${pojo.name?uncap_first},
                ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name}.class);
        return this.ucManage${pojo.name}.update${pojo.name}(persistence${pojo.name});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete${pojo.name}(${pojo.name} ${pojo.name?uncap_first}) {
    ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name} persistence${pojo.name};

        persistence${pojo.name} =
            this.mapInitialToTargetLayerEntity(${pojo.name?uncap_first},
                ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name}.class);
        return this.ucManage${pojo.name}.delete${pojo.name}(persistence${pojo.name});
    }

}
