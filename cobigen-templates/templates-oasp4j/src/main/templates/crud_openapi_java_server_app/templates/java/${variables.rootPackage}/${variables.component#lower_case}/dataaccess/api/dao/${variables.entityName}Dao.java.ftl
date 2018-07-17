package ${variables.rootPackage}.${variables.component?lower_case}.dataaccess.api.dao;

import ${variables.rootPackage}.general.dataaccess.api.dao.ApplicationDao;
import ${variables.rootPackage}.${variables.component?lower_case}.dataaccess.api.${variables.entityName}Entity;
import ${variables.rootPackage}.${variables.component?lower_case}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 * Data access interface for ${variables.entityName} entities
 */
public interface ${variables.entityName}Dao extends ApplicationDao<${variables.entityName}Entity> {
  
  /**
   * Finds the {@link ${variables.entityName}Entity ${variables.entityName?lower_case}s} matching the given {@link ${variables.entityName}SearchCriteriaTo}.
   *
   * @param criteria is the {@link ${variables.entityName}SearchCriteriaTo}.
   * @return the {@link PaginatedListTo} with the matching {@link ${variables.entityName}Entity} objects.
   */
  PaginatedListTo<${variables.entityName}Entity> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria);
}
