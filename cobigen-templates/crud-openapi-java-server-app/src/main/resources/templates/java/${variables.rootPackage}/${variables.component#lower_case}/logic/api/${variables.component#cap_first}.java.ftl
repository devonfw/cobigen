<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component?lower_case}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};

import java.util.List;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} extends UcFind${variables.entityName}, UcManage${variables.entityName} {

  <#list model.component.paths as path>
  	<#list path.operations as operation>
  		<#if !DevonfwUtil.isCrudOperation(operation.operationId, variables.entityName)>
        <#assign responses=operation.responses>
        <#list responses as response>

  		  <#assign hasEntity=hasResponseOfType(response, "Entity")>
	  		<#if hasResponseOfType(response "Paginated")>
	  			<#if hasEntity>
  	Page<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	Page<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
  				</#if>
  			<#elseif hasResponseOfType(response, "Array")>
  				<#if hasEntity>
  					<#assign returnEntityType = OpenApiUtil.toJavaType(response, true)>
  					<#if JavaUtil.equalsJavaPrimitiveOrWrapper(returnEntityType)>
  						List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#elseif returnEntityType?lower_case == "string">
  						List<String> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#else>
  						List<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					</#if>
  				<#else>
    List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    			</#if>
  			<#elseif hasResponseOfType(response "Void")>
  	void ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(<#rt>
  			<#else>  			
				<#if hasEntity>
  					<#assign returnEntityType = OpenApiUtil.toJavaType(response, true)>
  					
  					<#if JavaUtil.equalsJavaPrimitiveOrWrapper(returnEntityType)>
  						${returnEntityType} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(<#rt>
  					<#elseif returnEntityType?lower_case == "string">
  						String ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(<#rt>
  					<#else>
  						${returnEntityType}Eto ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(<#rt>
  					</#if>
  				<#else>
  	${getReturnType(operation,false)} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(<#rt>
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  					<#if parameter.isSearchCriteria>
  			${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}SearchCriteriaTo criteria<#if parameter?has_next>, </#if><#t>
  					<#elseif parameter.isEntity>
  		    ${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")?cap_first}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if><#t>
  		    	<#else>
  		    ${OpenApiUtil.toJavaType(parameter, true)?cap_first} ${parameter.name}<#if parameter?has_next>, </#if></#if></#list> );<#t>

  		  	</#list>
  		</#if>
  	</#list>
  </#list>
  
}