package ${variables.rootPackage}.${variables.component}.service.api.rest;

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
  <#if OaspUtil.hasMediaTypeInResponse(operations)>
  @Produces(${OaspUtil.getDistinctMediaTypes(operation)})
  </#if>
  <#assign returnType = OaspUtil.assignReturnType(operation)>
  public ${returnType?replace("Entity", "Eto")} ${operation.operationId}(
    <#list operation.parameters as parameter>
    	<#if parameter.inPath>
    	@PathParam("${parameter.name}")</#if>${OaspUtil.getJAVAConstraint(parameter.constraints)}${OaspUtil.getOaspTypeFromOpenAPI(parameter, true)}<#if parameter.isSearchCriteria>SearchCriteriaTo<#elseif parameter.isEntity>Eto</#if> ${parameter.name}<#if parameter?has_next>, </#if></#list>);
  		</#if>
  	</#list>
 </#list>
}