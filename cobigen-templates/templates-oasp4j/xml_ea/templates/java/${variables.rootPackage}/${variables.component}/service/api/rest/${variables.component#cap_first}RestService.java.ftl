<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
package ${variables.rootPackage}.${variables.component}.service.api.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.className};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The service interface for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Path("/${variables.component}/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ${variables.component?cap_first}RestService {

  /**
  * Delegates to {@link ${variables.component?cap_first}#find${variables.className}}.
  *
  * @param id the ID of the {@link ${variables.className}Eto}
  * @return the {@link ${variables.className}Eto}
  */
  @GET
  @Path("/${variables.className?lower_case}/{id}/")
  public ${variables.className}Eto get${variables.className}(@PathParam("id") long id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#save${variables.className}}.
   *
   * @param ${variables.className?lower_case} the {@link ${variables.className}Eto} to be saved
   * @return the recently created {@link ${variables.className}Eto}
   */
  @POST
  @Path("/${variables.className?lower_case}/")
  public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?lower_case});


  /**
   * Delegates to {@link ${variables.component?cap_first}#delete${variables.className}}.
   *
   * @param id ID of the {@link ${variables.className}Eto} to be deleted
   */
  @DELETE
  @Path("/${variables.className?lower_case}/{id}/")
  public void delete${variables.className}(@PathParam("id") long id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.className}Etos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.className?lower_case}s.
   * @return the {@link PaginatedListTo list} of matching {@link ${variables.className}Eto}s.
   */
  @Path("/${variables.className?lower_case}/search")
  @POST
  public PaginatedListTo<${variables.className}Eto> find${variables.className}sByPost(${variables.className}SearchCriteriaTo searchCriteriaTo);
  /**
  * Delegates to {@link ${variables.component?cap_first}#find${variables.className}Cto}.
  *
  * @param id the ID of the {@link ${variables.className}Cto}
  * @return the {@link ${variables.className}Cto}
  */
  @GET
  @Path("/${variables.className?lower_case}/cto/{id}/")
  public ${variables.className}Cto get${variables.className}Cto(@PathParam("id") long id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.className}Ctos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.className?lower_case}s.
   * @return the {@link PaginatedListTo list} of matching {@link ${variables.className}Cto}s.
   */
  @Path("/${variables.className?lower_case}/cto/search")
  @POST
  public PaginatedListTo<${variables.className}Cto> find${variables.className}CtosByPost(${variables.className}SearchCriteriaTo searchCriteriaTo);
}

</#compress>