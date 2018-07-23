<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api;

import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} {
    /**
    * Returns a ${variables.entityName} by its id 'id'.
    *
    * @param id The id 'id' of the ${variables.entityName}.
    * @return The {@link ${variables.entityName}Eto} with id 'id'
    */
    ${variables.entityName}Eto find${variables.entityName}(Long id);
  
	/**
    * Returns a paginated list of ${variables.entityName}s matching the search criteria.
    *
    * @param criteria the {@link ${variables.entityName}SearchCriteriaTo}.
    * @return the {@link List} of matching {@link ${variables.entityName}Eto}s.
    */
	PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteriaTo criteria);
	
	/**
    * Deletes a ${variables.entityName?uncap_first} from the database by its id '${variables.entityName?uncap_first}Id'.
    *
    * @param ${variables.entityName?uncap_first}Id Id of the ${variables.entityName?uncap_first} to delete
    * @return boolean <code>true</code> if the ${variables.entityName?uncap_first} can be deleted, <code>false</code> otherwise
    */
    boolean delete${variables.entityName}(Long ${variables.entityName?uncap_first}Id);
  
	/**
    * Saves a ${variables.entityName?uncap_first} and store it in the database.
    *
    * @param ${variables.entityName?uncap_first} the {@link ${variables.entityName}Eto} to create.
    * @return the new {@link ${variables.entityName}Eto} that has been saved with ID and version.
    */
    ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first});

  <#list model.component.paths as path>
  	<#list path.operations as operation>
  		<#if !OaspUtil.isCrudOperation(operation.operationId, variables.entityName)>
        <#assign responses=operation.responses>
  		  <#assign hasEntity=hasResponseOfType(responses, "Entity")>
	  		<#if hasResponseOfType(responses "Paginated")>
	  			<#if hasEntity>
  	PaginatedListTo<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	PaginatedListTo<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
  				</#if>
  			<#elseif hasResponseOfType(responses "Array")>
  				<#if hasEntity>
  	List<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
    List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    			</#if>
  			<#elseif hasResponseOfType(responses "Void")>
  	void ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  			<#else>
  				<#if hasEntity>
  	${getReturnType(operation,false)}Eto ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	${getReturnType(operation,false)} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
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
 				</#list>
 				);
  		</#if>
  	</#list>
  </#list>
}
