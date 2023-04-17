package io.github.devonfw.cobigen.generator.service.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface JavaxAnnotationsInputRestService {

  /**
   * Delegates to nothing.
   *
   * @param id the ID of nothing
   * @return nothing really
   */
  @GET
  @Path("/test1/{id}/")
  public String getVisitor(long id);

  /**
   * Delegates to nothing.
   *
   * @param nothing actually
   * @return apparently a String
   */
  @POST
  @Path("/test/")
  public String saveVisitor(String test);

  /**
   * Delegates to nothing.
   *
   * @param id ID of nothing to be deleted
   */
  @DELETE
  @Path("/test2/{id}/")
  public void deleteVisitor(long id);

}