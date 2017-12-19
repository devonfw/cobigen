package ${variables.rootPackage}.${variables.component}.logic.impl;

import java.util.Objects;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.demo.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 * Implementation of component interface of ${variables.component?cap_first}
 */
@Named
@Transactional
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}Impl.class);

    /**
     * @see #get${variables.entityName?cap_first}Dao()
     */
    @Inject
    private ${variables.entityName?cap_first}Dao ${variables.entityName?lower_case}Dao;

    /**
     * The constructor.
     */
    public ${variables.component?cap_first}Impl() {
      super();
    }
  
	@Override
	public ${variables.entityName?cap_first}Eto find${variables.entityName?cap_first}(Long id) {

	    LOG.debug("Get ${variables.entityName?cap_first} with id {} from database.", id);
	    return getBeanMapper().map(get${variables.entityName?cap_first}Dao().findOne(id), ${variables.entityName?cap_first}Eto.class);
  	}

    @Override
    public PaginatedListTo<${variables.entityName?cap_first}Eto> find${variables.entityName?cap_first}Etos(${variables.entityName?cap_first}SearchCriteriaTo criteria) {

      criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
      PaginatedListTo<${variables.entityName?cap_first}Entity> ${variables.entityName?lower_case}s = get${variables.entityName?cap_first}Dao().find${variables.entityName?cap_first}s(criteria);
      return mapPaginatedEntityList(${variables.entityName?lower_case}s, ${variables.entityName?cap_first}Eto.class);
    }

    @Override
    public boolean delete${variables.entityName?cap_first}(Long ${variables.entityName?lower_case}Id) {

      ${variables.entityName?cap_first}Entity ${variables.entityName?lower_case} = get${variables.entityName?cap_first}Dao().find(${variables.entityName?lower_case}Id);
      get${variables.entityName?cap_first}Dao().delete(${variables.entityName?lower_case});
      LOG.debug("The ${variables.entityName?lower_case} with id '{}' has been deleted.", ${variables.entityName?lower_case}Id);
      return true;
    }

    @Override
    public ${variables.entityName?cap_first}Eto save${variables.entityName?cap_first}(${variables.entityName?cap_first}Eto ${variables.entityName?lower_case}) {

      Objects.requireNonNull(${variables.entityName?lower_case}, "${variables.entityName?lower_case}");
      ${variables.entityName?cap_first}Entity ${variables.entityName?lower_case}Entity = getBeanMapper().map(${variables.entityName?lower_case}, ${variables.entityName?cap_first}Entity.class);

      // initialize, validate ${variables.entityName?lower_case}Entity here if necessary
      ${variables.entityName?cap_first}Entity resultEntity = get${variables.entityName?cap_first}Dao().save(${variables.entityName?lower_case}Entity);
      LOG.debug("${variables.entityName?cap_first} with id '{}' has been created.", resultEntity.getId());

      return getBeanMapper().map(resultEntity, ${variables.entityName?cap_first}Eto.class);
    }
    
    <#list model.component.paths as path>
  	<#list path.operations as operation>
  		<#if !OaspUtil.commonCRUDOperation(operation.operationId, variables.entityName?cap_first)>
  	@Override
	  		<#if operation.response.isPaginated>
	  			<#if operation.response.isEntity>
  	public PaginatedListTo<${operation.response.type}Eto> ${operation.operationId}(
  				<#else>
  	public PaginatedListTo<${operation.response.type}> ${operation.operationId}( 
  				</#if>
  			<#elseif operation.response.isArray>
  				<#if operation.response.isEntity>
  	public List<${operation.response.type}Eto> ${operation.operationId}(
  				<#else>
    public List<${operation.response.type}> ${operation.operationId}(
    			</#if>
  			<#elseif operation.response.isVoid>
  	public void ${operation.operationId}(
  			<#else>
  				<#if operation.response.isEntity>
  	public ${operation.response.type}Eto ${operation.operationId}(
  				<#else>
  	public ${operation.response.type} ${operation.operationId}(
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  				<#if parameter.isSearchCriteria>
  			${OaspUtil.getOaspTypeFromOpenAPI(parameter, false)}SearchCriteriaTo criteria<#if parameter?has_next>, <#else>) {</#if>
  				<#elseif parameter.isEntity>
  		    ${OaspUtil.getOaspTypeFromOpenAPI(parameter, false)}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, <#else>) {</#if>
  		    	<#else>
  		    ${OaspUtil.getOaspTypeFromOpenAPI(parameter, true)} ${parameter.name}<#if parameter?has_next>, <#else>) {</#if>
  		    	</#if>
  			</#list>
  		// TODO ${operation.operationId}
  			<#if !operation.response.isVoid>
  				<#if operation.response.type == "boolean">
  		return false;
  				<#elseif operation.response.type == "integer">
  		return 0;
  				<#else>
  		return null;
  				</#if>
  			</#if>		
  	}	
  		</#if>
  	</#list>
  </#list>
  
    /**
    * Returns the field '${variables.entityName?lower_case}Dao'.
    *
    * @return the {@link ${variables.entityName?cap_first}Dao} instance.
    */
    public ${variables.entityName?cap_first}Dao get${variables.entityName?cap_first}Dao() {
      return this.${variables.entityName?lower_case}Dao;
    }
}
