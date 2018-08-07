<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component?lower_case}.logic.impl;

import java.util.Objects;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component?lower_case}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component?lower_case}.dataaccess.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import java.math.BigDecimal;

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
  		<#if !OaspUtil.isCrudOperation(operation.operationId, variables.entityName?cap_first)>
  	@Override
        <#assign responses=operation.responses>
        <#assign hasEntity=hasResponseOfType(responses, "Entity")>
	  		<#if hasResponseOfType(responses "Paginated")>
	  			<#if hasEntity>
  	public PaginatedListTo<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	public PaginatedListTo<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
  				</#if>
  			<#elseif hasResponseOfType(responses,"Array")>
  				<#if hasEntity>
  	public List<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
    public List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    			</#if>
  			<#elseif hasResponseOfType(responses,"Void")>
  	public void ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  			<#else>
  				<#if hasEntity>
  	public ${getReturnType(operation,false)}Eto ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	public ${getReturnType(operation,false)} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  				<#if parameter.isSearchCriteria>
  			${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  				<#elseif parameter.isEntity>
  		    ${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","Eto")} ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    	<#else>
  		    ${OpenApiUtil.toJavaType(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if>
  		    	</#if>
  			</#list>) {
  		// TODO ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}
  			<#if !hasResponseOfType(responses,"Void")>
  				<#if getReturnType(operation,false) == "boolean">
  		return false;
  				<#elseif getReturnType(operation,false) == "integer">
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
