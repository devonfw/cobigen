package ${variables.rootPackage}.${variables.component}.dataaccess.api.dao;

import org.springframework.data.domain.Page;

import ${variables.rootPackage}.general.dataaccess.api.dao.ApplicationDao;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.${pojo.name};
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;

/**
 * Data access interface for ${variables.entityName} entities
 */
public interface ${variables.entityName}Dao extends ApplicationDao<${pojo.name}> {

  /**
   * Finds the {@link ${variables.entityName}Entity ${variables.entityName?lower_case}s} matching the given {@link ${variables.entityName}SearchCriteriaTo}.
   *
   * @param criteria is the {@link ${variables.entityName}SearchCriteriaTo}.
   * @return the {@link Page} with the matching {@link ${variables.entityName}Entity} objects.
   */
  Page<${variables.entityName}Entity> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria);
}