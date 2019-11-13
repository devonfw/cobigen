package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;

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

    @Override
    public ${variables.entityName}Cto find${variables.entityName}Cto(long id) {
    
      return ucFind${variables.entityName}.find${variables.entityName}Cto(id);
    }
    
    @Override
    public Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria) {
    
      return ucFind${variables.entityName}.find${variables.entityName}Ctos(criteria);
    }
}
