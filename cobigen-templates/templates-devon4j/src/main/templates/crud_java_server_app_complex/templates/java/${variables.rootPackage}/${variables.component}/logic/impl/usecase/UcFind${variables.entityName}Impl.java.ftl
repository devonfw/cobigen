package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

/**
 * Use case implementation for searching, filtering and getting ${variables.entityName}s
 */
@Named
@Validated
@Transactional
public class UcFind${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcFind${variables.entityName} {

	  /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UcFind${variables.entityName}Impl.class);


    @Override
    public ${variables.entityName}Eto find${variables.entityName}(<#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if>id) {
      LOG.debug("Get ${variables.entityName} with id {} from database.", id);
      Optional<${variables.entityName}Entity> foundEntity = get${variables.entityName}Repository().findById(id);
      if (foundEntity.isPresent())
        return getBeanMapper().map(foundEntity.get(), ${variables.entityName}Eto.class);
      else
        return null;
    }

    @Override
    public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {
      Page<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Repository().findByCriteria(criteria);
    return mapPaginatedEntityList(${variables.entityName?lower_case}s, ${variables.entityName}Eto.class);
  }

}
