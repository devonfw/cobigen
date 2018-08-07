package ${variables.rootPackage}.${variables.component}.service.impl.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
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
  public ${variables.entityName}Eto get${variables.entityName}(long id) {
    return this.${variables.component?uncap_first}.find${variables.entityName}(id);
  }

  @Override
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {
      return this.${variables.component?uncap_first}.save${variables.entityName}(${variables.entityName?lower_case});
  }

  @Override
  public void delete${variables.entityName}(long id) {
    this.${variables.component?uncap_first}.delete${variables.entityName}(id);
  }

  @Override
  public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}sByPost(${variables.entityName}SearchCriteriaTo searchCriteriaTo) {
    return this.${variables.component?uncap_first}.find${variables.entityName}Etos(searchCriteriaTo);
  }
  
  @Override
  public ${variables.entityName}Cto get${variables.entityName}Cto(long id) {
    return this.${variables.component?uncap_first}.find${variables.entityName}Cto(id);
  }

  @Override
  public PaginatedListTo<${variables.entityName}Cto> find${variables.entityName}CtosByPost(${variables.entityName}SearchCriteriaTo searchCriteriaTo) {
    return this.${variables.component?uncap_first}.find${variables.entityName}Ctos(searchCriteriaTo);
  }
}