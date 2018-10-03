package ${variables.rootPackage}.${variables.component}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} {
  
  /**
   * Returns a ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${variables.entityName}Eto} with id 'id'
   */
  ${variables.entityName}Eto find${variables.entityName}(long id);
  
	/**
   * Returns a paginated list of ${variables.entityName}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo}.
   * @return the {@link List} of matching {@link ${variables.entityName}Eto}s.
   */
	Page<${variables.entityName}Eto> find${variables.entityName}s(${variables.entityName}SearchCriteriaTo criteria);
	
	/**
   * Deletes a ${variables.entityName?uncap_first} from the database by its id '${variables.entityName?uncap_first}Id'.
   *
   * @param ${variables.entityName?uncap_first}Id Id of the ${variables.entityName?uncap_first} to delete
   * @return boolean <code>true</code> if the ${variables.entityName?uncap_first} can be deleted, <code>false</code> otherwise
   */
  boolean delete${variables.entityName}(long ${variables.entityName?uncap_first}Id);
  
	/**
   * Saves a ${variables.entityName?uncap_first} and store it in the database.
   *
   * @param ${variables.entityName?uncap_first} the {@link ${variables.entityName}Eto} to create.
   * @return the new {@link ${variables.entityName}Eto} that has been saved with ID and version.
   */
  ${variables.entityName}Eto save${variables.entityName}(${variables.entityName} ${variables.entityName?uncap_first});
  
    /**
   * Returns a composite ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${variables.entityName}Cto} with id 'id'
   */
  ${variables.entityName}Cto find${variables.entityName}Cto(long id);
  
  /**
   * Returns a paginated list of composite ${variables.entityName}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.entityName}SearchCriteriaTo}.
   * @return the {@link List} of matching {@link ${variables.entityName}Cto}s.
   */
  Page<${variables.entityName}Cto> find${variables.entityName}Ctos(${variables.entityName}SearchCriteriaTo criteria);
  
}