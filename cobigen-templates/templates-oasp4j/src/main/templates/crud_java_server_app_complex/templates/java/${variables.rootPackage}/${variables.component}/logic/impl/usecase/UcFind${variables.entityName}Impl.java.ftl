package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.entityName};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.entityName}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;


import java.util.List;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting ${variables.entityName}s
 */
@Named
@UseCase
@Validated
@Transactional
public class UcFind${variables.entityName}Impl extends Abstract${variables.entityName}Uc implements UcFind${variables.entityName} {

	/** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UcFind${variables.entityName}Impl.class);


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

}
