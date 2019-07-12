package ${variables.rootPackage}.${variables.component}.service.api.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
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

/**
 * The service interface for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Path("/${variables.component}/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ${variables.component?cap_first}RestService {
    
  /**
  * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}Cto}.
  *
  * @param id the ID of the {@link ${variables.entityName}Cto}
  * @return the {@link ${variables.entityName}Cto}
  */
  @GET
  @Path("/${variables.entityName?lower_case}/cto/{id}/")
  public ${variables.entityName}Cto get${variables.entityName}Cto(@PathParam("id") long id);

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}Ctos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.entityName?lower_case}s.
   * @return the {@link Page list} of matching {@link ${variables.entityName}Cto}s.
   */
  @Path("/${variables.entityName?lower_case}/cto/search")
  @POST
  public Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo searchCriteriaTo);

}