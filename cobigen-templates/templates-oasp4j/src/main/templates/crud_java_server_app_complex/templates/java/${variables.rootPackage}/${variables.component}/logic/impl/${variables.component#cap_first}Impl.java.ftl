package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of component interface of ${variables.component}
 */
@Named
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {

    @Inject
    private UcFind${variables.entityName} ucFind${variables.entityName};

    @Inject
    private UcManage${variables.entityName} ucManage${variables.entityName};

    /**
     * The constructor.
     */
    public ${variables.component?cap_first}Impl() {
        super();
    }

    @Override
    public ${variables.entityName}Eto find${variables.entityName}(Long id) {

      return this.ucFind${variables.entityName}.find${variables.entityName}(id);
    }

    @Override
    public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteriaTo criteria) {
      return this.ucFind${variables.entityName}.find${variables.entityName}Etos(criteria);
    }

    @Override
    public ${variables.entityName}Cto find${variables.entityName}Cto(Long id) {

      return this.ucFind${variables.entityName}.find${variables.entityName}Cto(id);
    }

    @Override
    public PaginatedListTo<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria) {
      return this.ucFind${variables.entityName}.find${variables.entityName}Ctos(criteria);
    }

    @Override
    public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {

      return this.ucManage${variables.entityName}.save${variables.entityName}(${variables.entityName?lower_case});
    }

    @Override
    public boolean delete${variables.entityName}(Long id) {

      return this.ucManage${variables.entityName}.delete${variables.entityName}(id);
    }
}
