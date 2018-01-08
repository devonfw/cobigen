package ${variables.rootPackage}.${variables.component}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
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
  		<#if !OaspUtil.commonCRUDOperation(operation.operationId, variables.entityName)>
	  		<#if operation.response.isPaginated>
	  			<#if operation.response.isEntity>
  	PaginatedListTo<${operation.response.type}Eto> ${operation.operationId}(
  				<#else>
  	PaginatedListTo<${operation.response.type}> ${operation.operationId}( 
  				</#if>
  			<#elseif operation.response.isArray>
  				<#if operation.response.isEntity>
  	List<${operation.response.type}Eto> ${operation.operationId}(
  				<#else>
    List<${operation.response.type}> ${operation.operationId}(
    			</#if>
  			<#elseif operation.response.isVoid>
  	void ${operation.operationId}(
  			<#else>
  				<#if operation.response.isEntity>
  	${operation.response.type}Eto ${operation.operationId}(
  				<#else>
  	${operation.response.type} ${operation.operationId}(
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  					<#if parameter.isSearchCriteria>
  			${OaspUtil.getOaspTypeFromOpenAPI(parameter, false)}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  					<#elseif parameter.isEntity>
  		    ${OaspUtil.getOaspTypeFromOpenAPI(parameter, false)}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    	<#else>
  		    ${OaspUtil.getOaspTypeFromOpenAPI(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if>
  		    	</#if>
 				</#list>
 				);
  		</#if>
  	</#list>
  </#list>
}
