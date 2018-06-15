package ${variables.rootPackage}.${variables.component}.logic.impl.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.usecase.UcFind${variables.className};
import ${variables.rootPackage}.${variables.component}.logic.base.usecase.Abstract${variables.className}Uc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${variables.className}Entity;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;


import java.util.List;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting ${variables.className}s
 */
@Named
@UseCase
@Validated
@Transactional
public class UcFind${variables.className}Impl extends Abstract${variables.className}Uc implements UcFind${variables.className} {

	/** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UcFind${variables.className}Impl.class);


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

}
