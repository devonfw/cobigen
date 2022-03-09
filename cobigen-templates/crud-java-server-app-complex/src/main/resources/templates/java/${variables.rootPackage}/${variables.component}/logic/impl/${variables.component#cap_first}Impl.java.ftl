package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

/**
 * Implementation of component interface of ${variables.component}
 */
@Named
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {

    @Inject
    private UcFind${variables.entityName} ucFind${variables.entityName};

    @Inject
    private UcManage${variables.entityName} ucManage${variables.entityName};

    @Override
    public ${variables.entityName}Eto find${variables.entityName}(<#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if> id) {

      return this.ucFind${variables.entityName}.find${variables.entityName}(id);
    }

    @Override
    public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {
      return this.ucFind${variables.entityName}.find${variables.entityName}s(criteria);
    }

    @Override
    public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {

      return this.ucManage${variables.entityName}.save${variables.entityName}(${variables.entityName?lower_case});
    }

    @Override
    public boolean delete${variables.entityName}(<#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if> id) {

      return this.ucManage${variables.entityName}.delete${variables.entityName}(id);
    }
}
