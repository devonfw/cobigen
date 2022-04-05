<#include "/functions.ftl">
package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcManage${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of component interface of ${variables.component}
 */
@Named
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {

    @Inject
    private UcFind${variables.entityName} ucFind${variables.entityName};

    @Inject
    private UcManage${variables.entityName} ucManage${variables.entityName};

    @Override
    public ${variables.entityName}Eto find${variables.entityName}(long id) {

      return this.ucFind${variables.entityName}.find${variables.entityName}(id);
    }

    @Override
    public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria) {
      return this.ucFind${variables.entityName}.find${variables.entityName}s(criteria);
    }

    @Override
    public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {

      return this.ucManage${variables.entityName}.save${variables.entityName}(${variables.entityName?lower_case});
    }

    @Override
    public boolean delete${variables.entityName}(long id) {

      return this.ucManage${variables.entityName}.delete${variables.entityName}(id);
    }
    
        <#list model.component.paths as path>
  	<#list path.operations as operation>
  		<#if !DevonfwUtil.isCrudOperation(operation.operationId, variables.entityName?cap_first)>
  	@Override
        <#assign responses=operation.responses>
        <#list responses as response>
        <#assign hasEntity=hasResponseOfType(response, "Entity")>
	  		<#if hasResponseOfType(response "Paginated")>
	  			<#if hasEntity>
  	public Page<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				<#else>
  	public Page<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}( 
  				</#if>
  			<#elseif hasResponseOfType(response,"Array")>
  				<#if hasEntity>
  				<#assign returnEntityType = OpenApiUtil.toJavaType(response, true)>
  					<#if JavaUtil.equalsJavaPrimitiveOrWrapper(returnEntityType)>
  					public List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#elseif returnEntityType?lower_case == "string">
  					public List<String> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#else>
  					public List<${getReturnType(operation,false)}Eto> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					</#if>
  				<#else>
    public List<${getReturnType(operation,false)}> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
    			</#if>
  			<#elseif hasResponseOfType(response,"Void")>
  	public void ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  			<#else>
  				<#if hasEntity>
  				<#assign returnEntityType = OpenApiUtil.toJavaType(response, true)>
  					<#if JavaUtil.equalsJavaPrimitiveOrWrapper(returnEntityType)>
  						public ${getReturnType(operation,false)} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#elseif returnEntityType?lower_case == "string">
  						public String ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					<#else>
  						public ${getReturnType(operation,false)}Eto ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  					</#if>
  				<#else>
  	public ${getReturnType(operation,false)} ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}(
  				</#if>
  			</#if>
  			<#list operation.parameters as parameter>
  				<#if parameter.isSearchCriteria>
  			${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}SearchCriteriaTo criteria<#if parameter?has_next>, </#if>
  				<#elseif parameter.isEntity>
  		    ${OpenApiUtil.toJavaType(parameter, false)?replace("Entity","")}Eto ${parameter.name?replace("Entity","")}<#if parameter?has_next>, </#if>
  		    	<#else>
  		    ${OpenApiUtil.toJavaType(parameter, true)} ${parameter.name}<#if parameter?has_next>, </#if>
  		    	</#if>
  			</#list>)
			<#compress> {
  		// TODO ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)}
  			<#if !hasResponseOfType(response,"Void")>
  				<#if getReturnType(operation,false) == "boolean">
  		return false;
  				<#elseif getReturnType(operation,false) == "integer">
  		return 0;
  				<#else>
  		return null;
  				</#if>
  			</#if>		
  	}		</#compress>
  	  		</#list>
  		</#if>
  		
  	</#list>
  </#list>
}
