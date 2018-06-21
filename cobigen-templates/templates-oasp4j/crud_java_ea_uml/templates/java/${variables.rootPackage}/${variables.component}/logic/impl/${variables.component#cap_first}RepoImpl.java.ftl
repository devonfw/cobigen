package ${variables.rootPackage}.${variables.component}.logic.impl;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}EntityRegistrationBean;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

@Named
@Transactional
public class ${variables.component?cap_first}RepoImpl extends AbstractComponentFacade implements ${variables.component?cap_first} {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}RepoImpl.class);
  
  private ${variables.className}EntityRegistrationBean ${variables.className?lower_case}RegistrationBean;

  @Override
  public ${variables.className}Eto find${variables.className}(Long id) {

    LOG.debug("Get ${variables.className} with id '" + id + "' from database.");
    return getBeanMapper().map(get${variables.className}RegistrationBean().get${variables.className}Repo().findOne(id), ${variables.className}Eto.class);
  }

  @Override
  public PaginatedListTo<${variables.className}Eto> find${variables.className}Etos(${variables.className}SearchCriteriaTo criteria) {

    criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
    return get${variables.className}RegistrationBean().get${variables.className}Repo().find${variables.className}s(criteria);
  }

  @Override
  public boolean delete${variables.className}(Long ${variables.className?lower_case}Id) {

    get${variables.className}RegistrationBean().get${variables.className}Repo().delete(${variables.className?lower_case}Id);
    return true;
  }
  
  @Override
  public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?lower_case}) {
    ${variables.className}Eto resultEntity = get${variables.className}RegistrationBean().get${variables.className}Repo().save(getBeanMapper().map(${variables.className?lower_case}, ${variables.className}Entity.class));
    LOG.debug("${variables.className} with id '{}' has been created.", resultEntity.getId());
    return getBeanMapper().map(resultEntity, ${variables.className}Eto.class);
  }

  /**
   * @return ${variables.className?lower_case}EntityRegistrationBean
   */
  @Inject
  public ${variables.className}EntityRegistrationBean get${variables.className}RegistrationBean() {

    return this.${variables.className?lower_case}RegistrationBean;
  }
  

  /**
   * @param ${variables.className?lower_case}EntityRegistrationBean the ${variables.className?lower_case}RegistrationBean to set
   */
  public void set${variables.className}RegistrationBean(${variables.className}EntityRegistrationBean ${variables.className?lower_case}RegistrationBean) {

    this.${variables.className?lower_case}RegistrationBean = ${variables.className?lower_case}RegistrationBean;
  }

}