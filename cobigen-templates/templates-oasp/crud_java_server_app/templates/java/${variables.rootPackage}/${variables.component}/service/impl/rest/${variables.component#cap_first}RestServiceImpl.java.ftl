package ${variables.rootPackage}.${variables.component}.service.impl.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service class for REST calls in order to execute the methods in {@link ${variables.component?cap_first}}.
 *
 */
@Path("/${variables.component}/v1")
@Named("${variables.component?cap_first}RestService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class ${variables.component?cap_first}RestServiceImpl {

  @Inject
  private ${variables.component?cap_first} ${variables.component?lower_case};

  /**
  * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}}.
  *
  * @param id the ID of the {@link ${variables.entityName}Eto}
  * @return the {@link ${variables.entityName}Eto}
  */
  @GET
  @Path("/${variables.entityName?lower_case}/{id}/")
  public ${variables.entityName}Eto get${variables.entityName}(@PathParam("id") String id) {

    Long idAsLong;

    if (id == null) {
      throw new BadRequestException("missing id");
    }

    ${variables.entityName}Eto ${variables.entityName?lower_case}Eto = null;

    try {
      idAsLong = Long.parseLong(id);

      ${variables.entityName?lower_case}Eto = this.${variables.component}.find${variables.entityName}(idAsLong);

      if(${variables.entityName?lower_case}Eto == null)
        throw new NotFoundException("${variables.entityName?lower_case} not found");

    } catch (NumberFormatException e) {
      throw new BadRequestException("id is not a number");
    } catch (NotFoundException e) {
      throw new BadRequestException("${variables.entityName?lower_case} not found");
    }
    return ${variables.entityName?lower_case}Eto;
  }

  /**
   * Delegates to {@link ${variables.component?cap_first}#save${variables.entityName}}.
   *
   * @param ${variables.entityName?lower_case} the {@link ${variables.entityName}Eto} to be saved
   * @return the recently created {@link ${variables.entityName}Eto}
   */
  @POST
  @Path("/${variables.entityName?lower_case}/")
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {
      return this.${variables.component}.save${variables.entityName}(${variables.entityName?lower_case});
  }


  /**
   * Delegates to {@link ${variables.component?cap_first}#delete${variables.entityName}}.
   *
   * @param id ID of the {@link ${variables.entityName}Eto} to be deleted
   */
  @DELETE
  @Path("/${variables.entityName?lower_case}/{id}/")
  public void delete${variables.entityName}(@PathParam("id") String id) {
    Long idAsLong;

    if (id == null) {
      throw new BadRequestException("missing id");
    }

    ${variables.entityName}Eto ${variables.entityName?lower_case}Eto = null;

    try {
      idAsLong = Long.parseLong(id);

      ${variables.entityName?lower_case}Eto = this.${variables.component}.delete${variables.entityName}(idAsLong);

      if(${variables.entityName?lower_case}Eto == null)
        throw new NotFoundException("${variables.entityName?lower_case} not found");

    } catch (NumberFormatException e) {
      throw new BadRequestException("id is not a number");
    } catch (NotFoundException e) {
      throw new BadRequestException("${variables.entityName?lower_case} not found");
    }
    return ${variables.entityName?lower_case}Eto;
  }

  /**
   * Delegates to {@link ${variables.component?cap_first}#find${variables.entityName}Etos}.
   *
   * @param searchCriteriaTo the pagination and search criteria to be used for finding ${variables.entityName?lower_case}s.
   * @return the {@link PaginatedListTo list} of matching {@link ${variables.entityName}Eto}s.
   */
  @Path("/${variables.entityName?lower_case}/search")
  @POST
  public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}sByPost(${variables.entityName}SearchCriteriaTo searchCriteriaTo) {
    return this.${variables.component}.find${variables.entityName}Etos(searchCriteriaTo);
  }

}