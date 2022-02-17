<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component?lower_case}.service.impl.rest;

import javax.inject.Inject;
import javax.inject.Named;

import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName?cap_first}Eto;
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName?cap_first}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component?lower_case}.service.api.rest.${variables.component?cap_first}RestService;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * The service implementation for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Named("${variables.component?cap_first}RestService")
public class ${variables.component?cap_first}RestServiceImpl implements ${variables.component?cap_first}RestService {

  @Inject
  private ${variables.component?cap_first} ${variables.component?lower_case};

  @Override
  public ${variables.entityName?cap_first}Eto get${variables.entityName?cap_first}(long id) {

    return this.${variables.component?lower_case}.find${variables.entityName?cap_first}(id);
  }

  @Override
  public ${variables.entityName?cap_first}Eto save${variables.entityName?cap_first}(${variables.entityName?cap_first}Eto ${variables.entityName?lower_case}) {

    return this.${variables.component?lower_case}.save${variables.entityName?cap_first}(${variables.entityName?lower_case});
  }

  @Override
  public void delete${variables.entityName?cap_first}(long id) {

    this.${variables.component?lower_case}.delete${variables.entityName?cap_first}(id);
  }

  @Override
  public Page<${variables.entityName?cap_first}Eto> find${variables.entityName?cap_first}sByPost(${variables.entityName?cap_first}SearchCriteriaTo searchCriteriaTo) {

    return this.${variables.component?lower_case}.find${variables.entityName?cap_first}s(searchCriteriaTo);
  }
  
  <#list model.component.paths as path>
	<#list path.operations as operation>
	    <#if !DevonfwUtil.isCrudOperation(operation.operationId!null, variables.entityName?cap_first)> 
  			<#assign returnType = getReturnType(operation,true)>
  @Override
  public ${returnType?replace("Entity", "Eto")} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  			<#list operation.parameters as parameter>
  				<#if parameter.isSearchCriteria>
  			${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  				<#elseif parameter.isEntity>
  		  ${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    <#else>
  		  ${OpenApiUtil.toJavaType(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if>
  		   	</#if>
 				</#list>
  			) {

    <#if returnType != "void">return</#if> this.${variables.component?lower_case}.${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    		<#list operation.parameters as parameter>
  				<#if parameter.isSearchCriteria>
  			criteria<#if parameter?has_next>, </#if>
  				<#elseif parameter.isEntity>
  		  ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		   	<#else>
  		  ${parameter.name}<#if parameter?has_next>, </#if>
  		   	</#if>
 			</#list>
 			);
  }
  		</#if>
  	</#list>
 </#list>

}