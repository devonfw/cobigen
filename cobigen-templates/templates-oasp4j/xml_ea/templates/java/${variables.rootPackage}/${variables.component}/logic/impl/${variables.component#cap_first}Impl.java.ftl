package ${variables.rootPackage}.${variables.component}.logic.impl;

import ${variables.rootPackage}.general.logic.base.AbstractComponentFacade;
import ${variables.rootPackage}.general.common.api.constants.PermissionConstants;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.className}Dao;
import ${variables.rootPackage}.${variables.component}.logic.api.${variables.component?cap_first};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;

import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import java.util.List;
import java.util.Objects;

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
public class ${variables.component?cap_first}Impl extends AbstractComponentFacade implements ${variables.component?cap_first} {
	
	/** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(${variables.component?cap_first}Impl.class);

  /** @see #get${variables.className}Dao() */
  @Inject
  private ${variables.className}Dao ${variables.className?uncap_first}Dao;

  /**
   * The constructor.
   */
  public ${variables.component?cap_first}Impl() {
      super();
  }
  
	@Override
	public ${variables.className}Eto find${variables.className}(Long id) {
		LOG.debug("Get ${variables.className} with id {} from database.", id);
		return getBeanMapper().map(get${variables.className}Dao().findOne(id), ${variables.className}Eto.class);
	}

	@Override
	public PaginatedListTo<${variables.className}Eto> find${variables.className}Etos(${variables.className}SearchCriteriaTo criteria) {
		criteria.limitMaximumPageSize(MAXIMUM_HIT_LIMIT);
		PaginatedListTo<${variables.className}Entity> ${variables.className?lower_case}s = get${variables.className}Dao().find${variables.className}s(criteria);
		return mapPaginatedEntityList(${variables.className?lower_case}s, ${variables.className}Eto.class);
	}

	@Override
	public boolean delete${variables.className}(Long ${variables.className?uncap_first}Id) {
		${variables.className}Entity ${variables.className?uncap_first} = get${variables.className}Dao().find(${variables.className?uncap_first}Id);
		get${variables.className}Dao().delete(${variables.className?uncap_first});
		LOG.debug("The ${variables.className?uncap_first} with id '{}' has been deleted.", ${variables.className?uncap_first}Id);
		return true;
	}

	@Override
	public ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?uncap_first}) {
		Objects.requireNonNull(${variables.className?uncap_first}, "${variables.className?uncap_first}");
		${variables.className}Entity ${variables.className?uncap_first}Entity = getBeanMapper().map(${variables.className?uncap_first}, ${variables.className}Entity.class);

		//initialize, validate ${variables.className?uncap_first}Entity here if necessary
		${variables.className}Entity resultEntity = get${variables.className}Dao().save(${variables.className?uncap_first}Entity);
		LOG.debug("${variables.className} with id '{}' has been created.", resultEntity.getId());

		return getBeanMapper().map(resultEntity, ${variables.className}Eto.class);
	}

	/**
	 * Returns the field '${variables.className?uncap_first}Dao'.
	 * @return the {@link ${variables.className}Dao} instance.
	 */
	public ${variables.className}Dao get${variables.className}Dao() {

		return this.${variables.className?uncap_first}Dao;
	}

}