package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
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
    public ${variables.entityName}Cto find${variables.entityName}Cto(<#if compositeIdTypeVar!="null">${compositeIdTypeVar}<#else>long</#if> id) {
      LOG.debug("Get ${variables.entityName}Cto with id {} from database.", id);
      ${variables.entityName}Entity entity = get${variables.entityName}Repository().find(id);
      ${variables.entityName}Cto cto = new ${variables.entityName}Cto();
      cto.set${variables.entityName?cap_first}(getBeanMapper().map(entity, ${variables.entityName}Eto.class));
      <#list pojo.fields as field>
        <#if field.type?ends_with("Entity")>
      cto.set${field.name?cap_first}(getBeanMapper().map(entity.get${field.name?cap_first}(), ${field.type?replace("Entity", "Eto")}.class));
        <#elseif field.type?contains("Entity") && JavaUtil.isCollection(classObject, field.name)>
      cto.set${field.name?cap_first}(getBeanMapper().mapList(entity.get${field.name?cap_first}(), ${DevonfwUtil.getListArgumentType(field, classObject)}Eto.class));
        </#if>
      </#list>
   
      return cto;
    }
  
    @Override
    public Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria) {
  
      Page<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Repository().findByCriteria(criteria);
      List<${variables.entityName}Cto> ctos = new ArrayList<>();
      for (${variables.entityName}Entity entity : ${variables.entityName?lower_case}s.getContent()) {
        ${variables.entityName}Cto cto = new ${variables.entityName}Cto();
        cto.set${variables.entityName?cap_first}(getBeanMapper().map(entity, ${variables.entityName}Eto.class));
        <#list pojo.fields as field>
          <#if field.type?ends_with("Entity")>
        cto.set${field.name?cap_first}(getBeanMapper().map(entity.get${field.name?cap_first}(), ${field.type?replace("Entity", "Eto")}.class));
          <#elseif field.type?contains("Entity") && JavaUtil.isCollection(classObject, field.name)>
        cto.set${field.name?cap_first}(getBeanMapper().mapList(entity.get${field.name?cap_first}(), ${DevonfwUtil.getListArgumentType(field, classObject)}Eto.class));
          </#if>
        </#list>
        ctos.add(cto);
      }
      Pageable pagResultTo = PageRequest.of(criteria.getPageable().getPageNumber(), criteria.getPageable().getPageSize());
      
      return new PageImpl<>(ctos, pagResultTo, ${variables.entityName?lower_case}s.getTotalElements());
    }
}
