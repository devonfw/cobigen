package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import org.springframework.validation.annotation.Validated;
import java.util.Objects;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>


/**
 * Use case implementation for modifying and deleting ${variables.entityName}s
 */
@Named
@Validated
@Transactional
public class UcManage${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcManage${variables.entityName} {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcManage${variables.entityName}Impl.class);

  @Override
  public boolean delete${variables.entityName}(<#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if> ${variables.entityName?uncap_first}Id) {

    ${variables.entityName}Entity ${variables.entityName?uncap_first} = get${variables.entityName}Repository().find(${variables.entityName?uncap_first}Id);
    get${variables.entityName}Repository().delete(${variables.entityName?uncap_first});
    LOG.debug("The ${variables.entityName?uncap_first} with id '{}' has been deleted.", ${variables.entityName?uncap_first}Id);
    return true;
  }

  @Override
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {

   Objects.requireNonNull(${variables.entityName?uncap_first}, "${variables.entityName?uncap_first}");

	 ${variables.entityName}Entity ${variables.entityName?uncap_first}Entity = getBeanMapper().map(${variables.entityName?uncap_first}, ${variables.entityName}Entity.class);

   //initialize, validate ${variables.entityName?uncap_first}Entity here if necessary
   ${variables.entityName}Entity resultEntity = get${variables.entityName}Repository().save(${variables.entityName?uncap_first}Entity);
   LOG.debug("${variables.entityName} with id '{}' has been created.",resultEntity.getId());
   return getBeanMapper().map(resultEntity, ${variables.entityName}Eto.class);
  }
}
