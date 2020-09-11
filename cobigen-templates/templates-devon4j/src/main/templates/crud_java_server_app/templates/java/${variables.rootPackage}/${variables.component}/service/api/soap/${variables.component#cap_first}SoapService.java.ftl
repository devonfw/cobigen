package ${variables.rootPackage}.${variables.component}.service.api.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import ${variables.rootPackage}.general.common.api.to.PaginatedListToWrapper;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;

import com.devonfw.module.jpa.common.api.to.PaginatedListTo;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

@WebService
public interface ${variables.component?cap_first}SoapService {

  @WebMethod
  @WebResult(name = "message")
  @GET
  public ${variables.entityName}Eto get${variables.entityName}(@WebParam(name = "id") <#if compositeIdTypeVar!="null">${compositeIdTypeVar}<#else>long</#if> id);

  @WebMethod
  @WebResult(name = "message")
  @POST
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case});

  @WebMethod
  @WebResult(name = "message")
  @DELETE
  public void delete${variables.entityName}(@WebParam(name = "id") <#if compositeIdTypeVar!="null">${compositeIdTypeVar}<#else>long</#if> id);

  @WebMethod
  @WebResult(name = "message")
  @POST
  public PaginatedListToWrapper<${variables.entityName}Eto> find${variables.entityName}sByPost(${variables.entityName}SearchCriteriaTo searchCriteriaTo);
}
