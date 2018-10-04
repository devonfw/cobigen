package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.general.common.api.constants.PermissionConstants;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.security.RolesAllowed;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of component interface of ${variables.component}
 */
@Named
@Transactional
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {
	
	/** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}Impl.class);

  /** @see #get${variables.entityName}Repository() */
  @Inject
  private ${variables.entityName}Repository ${variables.entityName?uncap_first}Dao;
  
	@Override
	public ${variables.entityName}Eto find${variables.entityName}(long id) {
		LOG.debug("Get ${variables.entityName} with id {} from database.", id);
		return getBeanMapper().map(get${variables.entityName}Dao().findById(id), ${variables.entityName}Eto.class);
	}

	@Override
	public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {
		Page<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Repository().findByCriteria(criteria);
		return mapPaginatedEntityList(${variables.entityName?lower_case}s, ${variables.entityName}Eto.class);
	}

	@Override
	public boolean delete${variables.entityName}(long ${variables.entityName?uncap_first}Id) {
		${variables.entityName}Entity ${variables.entityName?uncap_first} = get${variables.entityName}Repository().find(${variables.entityName?uncap_first}Id);
		get${variables.entityName}Dao().delete(${variables.entityName?uncap_first});
		LOG.debug("The ${variables.entityName?uncap_first} with id '{}' has been deleted.", ${variables.entityName?uncap_first}Id);
		return true;
	}

	@Override
	public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		Objects.requireNonNull(${variables.entityName?uncap_first}, "${variables.entityName?uncap_first}");
		${variables.entityName}Entity ${variables.entityName?uncap_first}Entity = getBeanMapper().map(${variables.entityName?uncap_first}, ${variables.entityName}Entity.class);

		//initialize, validate ${variables.entityName?uncap_first}Entity here if necessary
		${variables.entityName}Entity resultEntity = get${variables.entityName}Repository().save(${variables.entityName?uncap_first}Entity);
		LOG.debug("${variables.entityName} with id '{}' has been created.", resultEntity.getId());

		return getBeanMapper().map(resultEntity, ${variables.entityName}Eto.class);
	}

	/**
	 * Returns the field '${variables.entityName?uncap_first}repository'.
	 * @return the {@link ${variables.component?cap_first}Repository instance.
	 */
	public ${variables.entityName}Repository get${variables.entityName}Repository() {

		return this.${variables.entityName?uncap_first}Repository;
	}
		
	@Override
  public ${variables.entityName}Cto find${variables.entityName}Cto(Long id) {
    LOG.debug("Get ${variables.entityName}Cto with id {} from database.", id);
    ${variables.entityName}Entity entity = get${variables.entityName}Dao().find(id);
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
  public Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria) {

    Page<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Dao().findByCriteria(criteria);
    List<${variables.entityName}Cto> ctos = new ArrayList<>();
    for (${variables.entityName}Entity entity : ${variables.entityName?lower_case}s.getContent()) {
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
    Pageable pagResultTo = PageRequest.of(criteria.getPageable().getPageNumber(), ctos.size());
    Page<${variables.entityName}Cto> pagListTo = new PageImpl<>(ctos, pagResultTo, pagResultTo.getPageSize());
    return pagListTo;
  }


}