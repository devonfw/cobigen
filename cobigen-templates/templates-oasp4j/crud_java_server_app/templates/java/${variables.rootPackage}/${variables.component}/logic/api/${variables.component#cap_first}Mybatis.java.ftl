package ${variables.rootPackage}.${variables.component}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteria;
import com.capgemini.devonfw.module.mybatis.common.PaginationResults;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first}Mybatis {
  
  /**
   * Returns a ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${variables.entityName}Eto} with id 'id'
   */
  ${variables.entityName}Eto find${variables.entityName}(Long id);
  
  /**
   * Returns a paginated list of ${variables.entityName}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.entityName}SearchCriteria}.
   * @return the {@link List} of matching {@link ${variables.entityName}Eto}s.
   */
  PaginationResults<${variables.entityName}Eto> find${variables.entityName}Etos(${variables.entityName}SearchCriteria criteria);
  
  /**
   * Deletes a ${variables.entityName?uncap_first} from the database by its id '${variables.entityName?uncap_first}Id'.
   *
   * @param ${variables.entityName?uncap_first}Id Id of the ${variables.entityName?uncap_first} to delete
   * @return boolean <code>true</code> if the ${variables.entityName?uncap_first} can be deleted, <code>false</code> otherwise
   */
  boolean delete${variables.entityName}(Long ${variables.entityName?uncap_first}Id);
  
  /**
   * Saves a ${variables.entityName?uncap_first} and store it in the database.
   *
   * @param ${variables.entityName?uncap_first} the {@link ${variables.entityName}Eto} to create.
   * @return the new {@link ${variables.entityName}Eto} that has been saved with ID and version.
   */
  void save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first});
  
}