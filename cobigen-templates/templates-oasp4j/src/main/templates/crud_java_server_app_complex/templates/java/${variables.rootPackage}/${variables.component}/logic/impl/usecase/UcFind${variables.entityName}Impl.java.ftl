package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;


import java.util.List;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting ${variables.entityName}s
 */
@Named
@UseCase
@Validated
@Transactional
public class UcFind${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcFind${variables.entityName} {

	/** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UcFind${variables.entityName}Impl.class);


    @Override
    public ${variables.entityName}Eto find${variables.entityName}(Long id) {
        LOG.debug("Get ${variables.entityName} with id {} from database.", id);
        return getBeanMapper().map(get${variables.entityName}Dao().findOne(id), ${variables.entityName}Eto.class);
    }

    @Override
    public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteriaTo criteria) {
      criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
      PaginatedListTo<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Dao().find${variables.entityName}s(criteria);
      return mapPaginatedEntityList(${variables.entityName?lower_case}s, ${variables.entityName}Eto.class);
  }

  @Override
  public ${variables.entityName}Cto find${variables.entityName}Cto(Long id) {
    LOG.debug("Get ${variables.entityName}Cto with id {} from database.", id);
    ${variables.entityName}Entity entity = get${variables.entityName}Dao().findOne(id);
    ${variables.entityName}Cto cto = new ${variables.entityName}Cto();
    cto.set${variables.entityName?cap_first}(getBeanMapper().map(entity, ${variables.entityName}Eto.class));
    <#list pojo.fields as field>
      <#if field.type?ends_with("Entity")>
    cto.set${field.name?cap_first}(getBeanMapper().map(entity.get${field.name?cap_first}(), ${field.type?replace("Entity", "Eto")}.class));
      <#elseif field.type?contains("Entity") && JavaUtil.isCollection(classObject, field.name)>
    cto.set${field.name?cap_first}(getBeanMapper().mapList(entity.get${field.name?cap_first}(), ${OaspUtil.getListArgumentType(field, classObject)}Eto.class));
      </#if>
    </#list>
 
    return cto;
  }

  @Override
  public PaginatedListTo<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria) {
    criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
    PaginatedListTo<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Dao().find${variables.entityName}s(criteria);
    List<${variables.entityName}Cto> ctos = new ArrayList<>();
    for (${variables.entityName}Entity entity : ${variables.entityName?lower_case}s.getResult()) {
      ${variables.entityName}Cto cto = new ${variables.entityName}Cto();
      cto.set${variables.entityName?cap_first}(getBeanMapper().map(entity, ${variables.entityName}Eto.class));
      <#list pojo.fields as field>
        <#if field.type?ends_with("Entity")>
      cto.set${field.name?cap_first}(getBeanMapper().map(entity.get${field.name?cap_first}(), ${field.type?replace("Entity", "Eto")}.class));
        <#elseif field.type?contains("Entity") && JavaUtil.isCollection(classObject, field.name)>
      cto.set${field.name?cap_first}(getBeanMapper().mapList(entity.get${field.name?cap_first}(), ${OaspUtil.getListArgumentType(field, classObject)}Eto.class));
        </#if>
      </#list>
      ctos.add(cto);
      
    }
    PaginationResultTo pagResultTo = new PaginationResultTo(criteria.getPagination(), (long) ctos.size());
    PaginatedListTo<${variables.entityName}Cto> pagListTo = new PaginatedListTo(ctos, pagResultTo);
    return pagListTo;
  }

}
