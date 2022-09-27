package ${variables.rootPackage}.${variables.component}.service.api.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>


/**
 * The service interface for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Path("/${variables.component}/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ${variables.component?cap_first}RestService {

  /**
  * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}}.
  *
  * @param id the ID of the {@link ${variables.entityName}Eto}
  * @return the {@link ${variables.entityName}Eto}
  */
  @GET
  @Path("/${variables.entityName?lower_case}/{id}/")
  public ${variables.entityName}Eto get${variables.entityName}(@PathParam("id") <#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if> id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#save${variables.entityName}}.
   *
   * @param ${variables.entityName?lower_case} the {@link ${variables.entityName}Eto} to be saved
   * @return the recently created {@link ${variables.entityName}Eto}
   */
  @POST
  @Path("/${variables.entityName?lower_case}/")
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case});


  /**
   * Delegates to {@link ${variables.component?cap_first}#delete${variables.entityName}}.
   *
   * @param id ID of the {@link ${variables.entityName}Eto} to be deleted
   */
  @DELETE
  @Path("/${variables.entityName?lower_case}/{id}/")
  public void delete${variables.entityName}(@PathParam("id") <#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if> id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}Etos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.entityName?lower_case}s.
   * @return the {@link Page list} of matching {@link ${variables.entityName}Eto}s.
   */
  @Path("/${variables.entityName?lower_case}/search")
  @POST
  public Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo searchCriteriaTo);

}