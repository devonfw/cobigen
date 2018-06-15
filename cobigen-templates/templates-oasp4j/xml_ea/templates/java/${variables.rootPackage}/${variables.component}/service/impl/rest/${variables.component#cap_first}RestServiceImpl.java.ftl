<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
package ${variables.rootPackage}.${variables.component}.service.impl.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.className};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component}.service.api.rest.${variables.component?cap_first}RestService;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * The service implementation for REST calls in order to execute the logic of component {@link ${variables.component?cap_first}}.
 */
@Named("${variables.component?cap_first}RestService")
public class ${variables.component?cap_first}RestServiceImpl implements ${variables.component?cap_first}RestService{

  @Inject
  private ${variables.component?cap_first} ${variables.component?lower_case};

  @Override
  public ${variables.className}Eto get${variables.className}(long id) {
    return this.${variables.component?uncap_first}.find${variables.className}(id);
  }

  @Override
  public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?lower_case}) {
      return this.${variables.component?uncap_first}.save${variables.className}(${variables.className?lower_case});
  }

  @Override
  public void delete${variables.className}(long id) {
    this.${variables.component?uncap_first}.delete${variables.className}(id);
  }

  @Override
  public PaginatedListTo<${variables.className}Eto> find${variables.className}sByPost(${variables.className}SearchCriteriaTo searchCriteriaTo) {
    return this.${variables.component?uncap_first}.find${variables.className}Etos(searchCriteriaTo);
  }

}
</#compress>