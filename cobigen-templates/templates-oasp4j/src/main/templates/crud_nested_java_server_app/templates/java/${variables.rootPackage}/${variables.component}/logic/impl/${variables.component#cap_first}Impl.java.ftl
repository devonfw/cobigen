package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.general.common.api.constants.PermissionConstants;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Cto;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of component interface of ${variables.component}
 */
@Transactional
@Named
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {
	
	/** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}Impl.class);

  /** @see #get${variables.entityName}Dao() */
  @Inject
  private ${variables.entityName}Dao ${variables.entityName?uncap_first}Dao;

  /**
   * The constructor.
   */
  public ${variables.component?cap_first}Impl() {
      super();
  }
  
	@Override
	public ${variables.entityName}Eto find${variables.entityName}(Long id) {
		LOG.debug("Get ${variables.entityName} with id {} from database.", id);
		return getBeanMapper().map(get${variables.entityName}Dao().findOne(id), ${variables.entityName}Eto.class);
	}

	@Override
	public PaginatedListTo<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteriaTo criteria) {
		criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
		PaginatedListTo<${variables.entityName}Entity> ${variables.entityName?lower_case}s = get${variables.entityName}Dao().find${variables.entityName}s(criteria);
		return mapPaginatedEntityList(${variables.entityName?lower_case}s, ${variables.entityName}Eto.class);
	}

	@Override
	public boolean delete${variables.entityName}(Long ${variables.entityName?uncap_first}Id) {
		${variables.entityName}Entity ${variables.entityName?uncap_first} = get${variables.entityName}Dao().find(${variables.entityName?uncap_first}Id);
		get${variables.entityName}Dao().delete(${variables.entityName?uncap_first});
		LOG.debug("The ${variables.entityName?uncap_first} with id '{}' has been deleted.", ${variables.entityName?uncap_first}Id);
		return true;
	}

	@Override
	public ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first}) {
		Objects.requireNonNull(${variables.entityName?uncap_first}, "${variables.entityName?uncap_first}");
		${variables.entityName}Entity ${variables.entityName?uncap_first}Entity = getBeanMapper().map(${variables.entityName?uncap_first}, ${variables.entityName}Entity.class);

		//initialize, validate ${variables.entityName?uncap_first}Entity here if necessary
		get${variables.entityName}Dao().save(${variables.entityName?uncap_first}Entity);
		LOG.debug("${variables.entityName} with id '{}' has been created.", ${variables.entityName?uncap_first}Entity.getId());

		return getBeanMapper().map(${variables.entityName?uncap_first}Entity, ${variables.entityName}Eto.class);
	}

	/**
	 * Returns the field '${variables.entityName?uncap_first}Dao'.
	 * @return the {@link ${variables.entityName}Dao} instance.
	 */
	public ${variables.entityName}Dao get${variables.entityName}Dao() {

		return this.${variables.entityName?uncap_first}Dao;
	}

  @Override
  public ${variables.entityName}Cto find${variables.entityName}Cto(Long id) {
  
    ${variables.entityName}Cto ${variables.entityName?uncap_first}Cto =new ${variables.entityName}Cto();
    ${variables.entityName}Eto ${variables.entityName?uncap_first}Eto=this.find${variables.entityName}(id); 
    ${variables.entityName?uncap_first}Cto.set${variables.entityName}(${variables.entityName?uncap_first}Eto);
    LOG.debug("Cto created for id ", id);
    return ${variables.entityName?uncap_first}Cto;
  }
}