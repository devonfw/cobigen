package ${variables.rootPackage}.${variables.component}.service.api.rest;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName?cap_first}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName?cap_first}SearchCriteriaTo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 * The service interface for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Path("${variables.component}/v1")
public interface ${variables.component?cap_first}RestService {

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName?cap_first}}.
   *
   * @param id the ID of the {@link ${variables.entityName?cap_first}Eto}
   * @return the {@link ${variables.entityName?cap_first}Eto}
   */
  @GET
  @Path("/${variables.entityName?lower_case}/{id}/")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  public ${variables.entityName?cap_first}Eto get${variables.entityName?cap_first}(@PathParam("id") long id);
  
  /**
   * Delegates to {@link ${variables.component?cap_first}#save${variables.entityName?cap_first}}.
   *
   * @param ${variables.entityName?lower_case} the {@link ${variables.entityName?cap_first}Eto} to be saved
   * @return the recently created {@link ${variables.entityName?cap_first}Eto}
   */
  @POST
  @Path("/${variables.entityName?lower_case}/")
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  @Consumes(MediaType.APPLICATION_JSON_VALUE)
  public ${variables.entityName?cap_first}Eto save${variables.entityName?cap_first}(@Valid ${variables.entityName?cap_first}Eto ${variables.entityName?lower_case});

  /**
   * Delegates to {@link ${variables.component?cap_first}#delete${variables.entityName?cap_first}}.
   *
   * @param id ID of the {@link ${variables.entityName?cap_first}Eto} to be deleted
   */
  @DELETE
  @Path("/${variables.entityName?lower_case}/{id}/")
  public void delete${variables.entityName?cap_first}(@PathParam("id") long id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName?cap_first}Etos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.entityName?lower_case}s.
   * @return the {@link PaginatedListTo list} of matching {@link ${variables.entityName?cap_first}Eto}s.
   */
  @Path("/${variables.entityName?lower_case}/search")
  @POST
  @Produces(MediaType.APPLICATION_JSON_VALUE)
  @Consumes(MediaType.APPLICATION_JSON_VALUE)
  public PaginatedListTo<${variables.entityName?cap_first}Eto> find${variables.entityName?cap_first}sByPost(@Valid ${variables.entityName?cap_first}SearchCriteriaTo searchCriteriaTo);
  
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
  			<#assign returnType = OaspUtil.returnType(operation.response)>
  <#list operation.parameters as parameter>
    <#if parameter.mediaType??>
 @Consumes(MediaType.${OaspUtil.getSpringMediaType(parameter.mediaType)})
  	</#if>
  </#list>
  <#if operation.response.mediaType??>
  @Produces(MediaType.${OaspUtil.getSpringMediaType(operation.response.mediaType)})
  </#if>
  public ${returnType?replace("Entity", "Eto")} ${operation.operationId}(
    <#list operation.parameters as parameter>
    	<#if parameter.inPath>
    	@PathParam("${parameter.name}")</#if>${OaspUtil.getJAVAConstraint(parameter.constraints)}${OaspUtil.getOaspTypeFromOpenAPI(parameter, true)}<#if parameter.isSearchCriteria>SearchCriteriaTo<#elseif parameter.isEntity>Eto</#if> ${parameter.name}<#if parameter?has_next>, </#if></#list>);
  		</#if>
  	</#list>
 </#list>
}
