package x_rootpackage_x.x_component_x.service.x_scope_x.x_detail_x;

import java.net.URI;
import java.util.Objects;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_XEto;
import x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcDeleteX_EntityName_X;
import x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcFindX_EntityName_X;
import x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcSaveX_EntityName_X;

/**
 * REST-service for {@link X_EntityName_XEto}.
 */
@Path("/x_entity-name_x")
@CobiGenTemplate(value = CobiGenJavaIncrements.REST)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_IMPL),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class X_EntityName_XRestService {

  @Inject
  private UcFindX_EntityName_X ucFind;

  @Inject
  private UcSaveX_EntityName_X ucSave;

  @Inject
  private UcDeleteX_EntityName_X ucDelete;

  /**
   * @param id the {@link X_EntityName_XEto#getId() primary key} of the requested {@link X_EntityName_XEto}.
   * @return the {@link X_EntityName_XEto} for the given {@code id}.
   */
  @GET
  @Path("/{id}")
  public X_EntityName_XEto findEto(@PathParam("id") Long id) {

    X_EntityName_XEto task = this.ucFind.findX_EntityName_X(id);
    if (task == null) {
      throw new NotFoundException("X_EntityName_X with id " + id + " does not exist.");
    }
    return task;
  }

  /**
   * @param eto the {@link X_EntityName_XEto} to save.
   * @return the restful {@link Response}.
   */
  @POST
  @Path("/")
  public Response saveEto(@Valid X_EntityName_XEto eto) {

    Long id = this.ucSave.saveX_EntityName_X(eto);
    Long etoId = eto.getId();
    if (etoId == null || Objects.equals(etoId, id)) {
      return Response.created(URI.create("/x_entity-name_x/" + id)).build();
    }
    return Response.ok().build();
  }

  /**
   * @param id the {@link X_EntityName_XEto#getId() primary key} of the {@link X_EntityName_XEto} to delete.
   */
  @DELETE
  @Path("/{id}")
  public void deleteEto(@PathParam("id") Long id) {

    this.ucDelete.deleteX_EntityName_X(id);
  }
}
