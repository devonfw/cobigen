<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
package ${variables.rootPackage}.${variables.component}.service.api.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import ${variables.rootPackage}.general.common.api.to.PaginatedListToWrapper;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;


@WebService
public interface ${variables.component?cap_first}SoapService {

  @WebMethod
  @WebResult(name = "message")
  @GET
  public ${variables.className}Eto get${variables.className}(@WebParam(name = "id") long id);

  @WebMethod
  @WebResult(name = "message")
  @POST
  public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?lower_case});

  @WebMethod
  @WebResult(name = "message")
  @DELETE
  public void delete${variables.className}(@WebParam(name = "id") long id);

  @WebMethod
  @WebResult(name = "message")
  @POST
  public PaginatedListToWrapper<${variables.className}Eto> find${variables.className}sByPost(${variables.className}SearchCriteriaTo searchCriteriaTo);
}
</#compress>