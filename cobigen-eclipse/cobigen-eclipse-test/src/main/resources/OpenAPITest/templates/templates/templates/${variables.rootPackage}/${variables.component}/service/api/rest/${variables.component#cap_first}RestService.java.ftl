package ${variables.rootPackage}.${variables.component}.service.api.rest;

import java.awt.PageAttributes.MediaType;

public interface ${variables.component?cap_first}RestService {
  
<#list model.component.paths as path>
	<#list path.operations as operation>
	    <#if !OaspUtil.commonCRUDOperation(operation.operationId, variables.entityName?cap_first)> 
  			<#if operation.type == "get">
  @GET
  			<#elseif operation.type == "post">
  @POST
  			<#elseif operation.type == "put">
  @PUT
  			<#else>
  @DELETE
  			</#if>
  @Path("${path.pathURI}")
  <#list operation.parameters as parameter>
    <#if parameter.mediaType??>
 @Consumes(MediaType.${OaspUtil.getSpringMediaType(parameter.mediaType)})
  	</#if>
  </#list>
  <#if hasMediaTypeInResponses(operation)>
  @Produces(${getDistinctMediaTypes(operation)})
  </#if>
  <#assign returnType = assignReturnType(operation)>
  public ${returnType?replace("Entity", "Eto")} ${operation.operationId}(
    <#list operation.parameters as parameter>
    	<#if parameter.inPath>
    	@PathParam("${parameter.name}")</#if>${OaspUtil.getJAVAConstraint(parameter.constraints)}${OaspUtil.getOaspTypeFromOpenAPI(parameter, true)}<#if parameter.isSearchCriteria>SearchCriteriaTo<#elseif parameter.isEntity>Eto</#if> ${parameter.name}<#if parameter?has_next>, </#if></#list>);
  		</#if>
  	</#list>
 </#list>
}

<#function assignReturnType operation>
  <#assign result=[]>
  <#assign differentMediaTypes="">
  <#list operation.responses as response>
    <#list response.mediaTypes as mt>
      <#if !(differentMediaTypes?contains(mt))>
        <#assign result=result+[response]>
      </#if>
    </#list>
  </#list>
  <#if result?size gte 2>
    <#return "Object">
  <#else>
    <#if result?size lt 1>
      <#return "void">
    <#else>
      <#return OaspUtil.returnType(result?first)>
    </#if>
  </#if>
</#function>

<#function getDistinctMediaTypes operation>
  <#assign result=" ">
  <#assign amountOfTypes=0>
  <#list operation.responses as response>
    <#list response.mediaTypes as mt>
      <#if !(result?contains(mt))>
        <#if !(result==" ")>
          <#assign result=result+",">
        </#if>
        <#assign result=result?trim+"MediaType."+OaspUtil.getSpringMediaType(mt)>
      </#if>
    </#list>
    </#list>
  <#return result>
</#function>

<#function hasMediaTypeInResponses operation>
  <#list operation.responses as response>
    <#if response.mediaTypes?size gt 0>
      <#return true>
    </#if>
  </#list>
  <#return false>
</#function>