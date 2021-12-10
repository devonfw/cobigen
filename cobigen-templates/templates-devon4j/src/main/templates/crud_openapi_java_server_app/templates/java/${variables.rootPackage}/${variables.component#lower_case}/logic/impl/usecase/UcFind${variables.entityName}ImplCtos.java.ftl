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
    public ${variables.entityName}Cto find${variables.entityName}Cto(long id) {
      LOG.debug("Get ${variables.entityName}Cto with id {} from database.", id);
      ${variables.entityName}Entity entity = get${variables.entityName}Repository().find(id);
      ${variables.entityName}Cto cto = new ${variables.entityName}Cto();
      cto.set${variables.entityName?cap_first}(getBeanMapper().map(entity, ${variables.entityName}Eto.class));
      <#list model.properties as property>
        <#if property.type?ends_with("Entity")>
      cto.set${property.name?cap_first}(getBeanMapper().map(entity.get${property.name?cap_first}(), ${property.type?replace("Entity", "Eto")}.class));
        <#elseif property.type?contains("Entity") && JavaUtil.isCollection(classObject, property.name)>
      cto.set${property.name?cap_first}(getBeanMapper().mapList(entity.get${property.name?cap_first}(), ${DevonfwUtil.getListArgumentType(property, classObject)}Eto.class));
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
        <#list model.properties as property>
          <#if property.type?ends_with("Entity")>
        cto.set${property.name?cap_first}(getBeanMapper().map(entity.get${property.name?cap_first}(), ${property.type?replace("Entity", "Eto")}.class));
          <#elseif property.type?contains("Entity") && JavaUtil.isCollection(classObject, property.name)>
        cto.set${property.name?cap_first}(getBeanMapper().mapList(entity.get${property.name?cap_first}(), ${DevonfwUtil.getListArgumentType(property, classObject)}Eto.class));
          </#if>
        </#list>
        ctos.add(cto);
      }
      Pageable pagResultTo = PageRequest.of(criteria.getPageable().getPageNumber(), criteria.getPageable().getPageSize());
      
      return new PageImpl<>(ctos, pagResultTo, ${variables.entityName?lower_case}s.getTotalElements());
    }
}
