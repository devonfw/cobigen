<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} extends UcFind${variables.entityName}, UcManage${variables.entityName} {

  <#list model.component.paths as path>
  	<#list path.operations as operation>
	        <#compress>
  		<#if !OaspUtil.isCrudOperation(operation.operationId, variables.entityName)>
        <#assign responses=operation.responses>
  		  <#assign hasEntity=hasResponseOfType(responses, "Entity")>
	  		<#if hasResponseOfType(responses "Paginated")>
	  			<#if hasEntity>
  	Page<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	Page<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
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
					Parameter.type = ${parameter.type}
					${parameter.mediaType}
  			${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  					<#elseif parameter.isEntity>
  		    ${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","Eto")} ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    	<#else>
  		    ${OpenApiUtil.toJavaType(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if></#if></#list> );
  		</#if>
		</#compress>
  	</#list>
  </#list>
  
}
