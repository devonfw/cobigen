package ${variables.rootPackage}.${variables.component}.service.impl.rest;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first}Mybatis;
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
@Named("${variables.component?cap_first}MybatisRestService")
public class ${variables.component?cap_first}MybatisRestServiceImpl implements ${variables.component?cap_first}MybatisRestService{

  @Inject
  private ${variables.component?cap_first}Mybatis ${variables.component?lower_case};

  @Override
  public ${variables.entityName}Eto get${variables.entityName}(long id) {
    return this.${variables.component}.find${variables.entityName}(id);
  }

  @Override
  public void save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {
      this.${variables.component}.save${variables.entityName}(${variables.entityName?lower_case});
  }

  @Override
  public void delete${variables.entityName}(long id) {
    this.${variables.component}.delete${variables.entityName}(id);
  }

  @Override
  public PaginationResults<${variables.entityName}Eto> find${variables.entityName}sByPost(${variables.entityName}SearchCriteria searchCriteria) {
    return this.${variables.component}.find${variables.entityName}Etos(searchCriteria);
  }

}