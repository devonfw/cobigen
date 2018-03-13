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
	  		<#if operation.response.isPaginated>
	  			<#if operation.response.isEntity>
  	PaginatedListTo<${operation.response.type}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	PaginatedListTo<${operation.response.type}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
  				</#if>
  			<#elseif operation.response.isArray>
  				<#if operation.response.isEntity>
  	List<${operation.response.type}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
    List<${operation.response.type}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    			</#if>
  			<#elseif operation.response.isVoid>
  	void ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  			<#else>
  				<#if operation.response.isEntity>
  	${operation.response.type}Eto ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	${operation.response.type} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  					<#if parameter.isSearchCriteria>
  			${OpenApiUtil.toJavaType(parameter, false)}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  					<#elseif parameter.isEntity>
  		    ${OpenApiUtil.toJavaType(parameter, false)}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    	<#else>
  		    ${OpenApiUtil.toJavaType(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if>
  		    	</#if>
 				</#list>
 				);
  		</#if>
  	</#list>
  </#list>
}
