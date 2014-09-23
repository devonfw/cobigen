package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.common.base.AbstractLayerImpl;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.base.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.UcManage${variables.entityName};

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of component interface of ${variables.component}
 */
@Named
public class ${variables.component?cap_first}Impl extends AbstractLayerImpl implements ${variables.component?cap_first} {

    private UcFind${variables.entityName} ucFind${variables.entityName};

    private UcManage${variables.entityName} ucManage${variables.entityName};

    /**
     * The constructor.
     */
    public ${variables.component?cap_first}Impl() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ${variables.entityName}Eto get${variables.entityName}(Long id) {

      return this.ucFind${variables.entityName}.get${variables.entityName}(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<${variables.entityName}Eto> getAll${variables.entityName}s() {

      return this.ucFind${variables.entityName}.getAll${variables.entityName}s();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public ${variables.entityName}Eto create${variables.entityName}(${variables.entityName}Eto table) {

      return this.ucManage${variables.entityName}.create${variables.entityName}(table);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public boolean delete${variables.entityName}(Long id) {
  
      return this.ucManage${variables.entityName}.delete${variables.entityName}(id);
    }


    /**
     * Sets the field 'ucFind${variables.entityName}'.
     * @param ucFind${variables.entityName}
     *            New value for ucFind${variables.entityName}
     */
    @Inject
    public void setUcFind${variables.entityName}(UcFind${variables.entityName} ucFind${variables.entityName}) {
        this.ucFind${variables.entityName} = ucFind${variables.entityName};
    }

    /**
     * Sets the field 'ucManage${variables.entityName}'.
     * @param ucManage${variables.entityName}
     *            New value for ucManage${variables.entityName}
     */
    @Inject
    public void setUcManage${variables.entityName}(UcManage${variables.entityName} ucManage${variables.entityName}) {
        this.ucManage${variables.entityName} = ucManage${variables.entityName};
    }
}
