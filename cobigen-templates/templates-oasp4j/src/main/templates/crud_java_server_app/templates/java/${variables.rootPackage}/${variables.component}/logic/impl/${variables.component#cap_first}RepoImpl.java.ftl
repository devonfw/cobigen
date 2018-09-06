package ${variables.rootPackage}.${variables.component}.logic.impl;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}EntityRegistrationBean;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

@Named
@Transactional
public class ${variables.component?cap_first}RepoImpl extends AbstractComponentFacade implements ${variables.component?cap_first} {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}RepoImpl.class);
  
  private ${variables.entityName}EntityRegistrationBean ${variables.entityName?lower_case}RegistrationBean;

  @Override
  public ${variables.entityName}Eto find${variables.entityName}(Long id) {

    LOG.debug("Get ${variables.entityName} with id '" + id + "' from database.");
    return getBeanMapper().map(get${variables.entityName}RegistrationBean().get${variables.entityName}Repo().findOne(id), ${variables.entityName}Eto.class);
  }

  @Override
  public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteriaTo criteria) {

    criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
    return get${variables.entityName}RegistrationBean().get${variables.entityName}Repo().find${variables.entityName}s(criteria);
  }

  @Override
  public boolean delete${variables.entityName}(Long ${variables.entityName?lower_case}Id) {

    get${variables.entityName}RegistrationBean().get${variables.entityName}Repo().delete(${variables.entityName?lower_case}Id);
    return true;
  }
  
  @Override
  public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?lower_case}) {
    ${variables.entityName}Eto resultEntity = get${variables.entityName}RegistrationBean().get${variables.entityName}Repo().save(getBeanMapper().map(${variables.entityName?lower_case}, ${variables.entityName}Entity.class));
    LOG.debug("${variables.entityName} with id '{}' has been created.", resultEntity.getId());
    return getBeanMapper().map(resultEntity, ${variables.entityName}Eto.class);
  }

  /**
   * @return ${variables.entityName?lower_case}EntityRegistrationBean
   */
  @Inject
  public ${variables.entityName}EntityRegistrationBean get${variables.entityName}RegistrationBean() {

    return this.${variables.entityName?lower_case}RegistrationBean;
  }
  

  /**
   * @param ${variables.entityName?lower_case}EntityRegistrationBean the ${variables.entityName?lower_case}RegistrationBean to set
   */
  public void set${variables.entityName}RegistrationBean(${variables.entityName}EntityRegistrationBean ${variables.entityName?lower_case}RegistrationBean) {

    this.${variables.entityName?lower_case}RegistrationBean = ${variables.entityName?lower_case}RegistrationBean;
  }

}