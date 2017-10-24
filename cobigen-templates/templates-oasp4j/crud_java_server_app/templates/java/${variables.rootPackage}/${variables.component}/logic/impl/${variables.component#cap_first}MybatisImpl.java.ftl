package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.general.common.api.constants.PermissionConstants;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.entityName}MybatisDao;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first}Mybatis;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteria;
import com.capgemini.devonfw.module.mybatis.common.PaginationResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.security.RolesAllowed;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of component interface of ${variables.component}
 */
@Named
@Transactional
public class ${variables.component?cap_first}MybatisImpl extends AbstractComponentFacade implements ${variables.component?cap_first}Mybatis {
  
  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}MybatisImpl.class);

  /** @see #get${variables.entityName}Dao() */
  @Autowired
  private ${variables.entityName}MybatisDao ${variables.entityName?uncap_first}MybatisDao;

  /**
   * The constructor.
   */
  public ${variables.component?cap_first}MybatisImpl() {
      super();
  }
  
  @Override
  public ${variables.entityName}Eto find${variables.entityName}(Long id) {
    LOG.debug("Get ${variables.entityName} with id {} from database.", id);
    return getBeanMapper().map(get${variables.entityName}Dao().fetchById(id), ${variables.entityName}Eto.class);
  }

  @Override
  public PaginationResults<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteria criteria) {
    PaginationResults<${pojo.name}> ${pojo.name?lower_case}Res = get${variables.entityName}Dao().fetch(criteria);
    List<${variables.entityName}Eto> etos = new ArrayList<>();
    for (Object object : ${pojo.name?lower_case}Res.getResults()) {
      ${pojo.name} ${pojo.name?lower_case} = (${pojo.name}) object;

      etos.add(getBeanMapper().map(${pojo.name?lower_case}, ${variables.entityName}Eto.class));

    }
    PaginationResults paginationResults = new PaginationResults();
    paginationResults.setResults(etos);
    paginationResults.setPagination(criteria.getPagination());
    return paginationResults;
  }

  @Override
  public boolean delete${variables.entityName}(Long ${variables.entityName?uncap_first}Id) {
    get${variables.entityName}Dao().delete(${variables.entityName?uncap_first}Id);
    LOG.debug("The ${variables.entityName?uncap_first} with id '{}' has been deleted.", ${variables.entityName?uncap_first}Id);
    return true;
  }

  @Override
  public void save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
    Objects.requireNonNull(${variables.entityName?uncap_first}, "${variables.entityName?uncap_first}");
    ${pojo.name} ${pojo.name?uncap_first} = getBeanMapper().map(${variables.entityName?uncap_first}, ${pojo.name}.class);

    //initialize, validate ${variables.entityName?uncap_first}Entity here if necessary
    get${variables.entityName}Dao().insert(${pojo.name?uncap_first});
  }

  /**
   * Returns the field '${variables.entityName?uncap_first}Dao'.
   * @return the {@link ${variables.entityName}Dao} instance.
   */
  public ${variables.entityName}MybatisDao get${variables.entityName}Dao() {

    return this.${variables.entityName?uncap_first}MybatisDao;
  }

}