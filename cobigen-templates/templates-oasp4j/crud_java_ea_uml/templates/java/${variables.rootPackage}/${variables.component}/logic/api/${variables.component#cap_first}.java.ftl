package ${variables.rootPackage}.${variables.component}.logic.api;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

/**
 * Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.component?cap_first} {
  
  /**
   * Returns a ${variables.className} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.className}.
   * @return The {@link ${variables.className}Eto} with id 'id'
   */
  ${variables.className}Eto find${variables.className}(Long id);
  
	/**
   * Returns a paginated list of ${variables.className}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.className}SearchCriteriaTo}.
   * @return the {@link List} of matching {@link ${variables.className}Eto}s.
   */
	PaginatedListTo<${variables.className}Eto> find${variables.className}Etos(${variables.className}SearchCriteriaTo criteria);
	
	/**
   * Deletes a ${variables.className?uncap_first} from the database by its id '${variables.className?uncap_first}Id'.
   *
   * @param ${variables.className?uncap_first}Id Id of the ${variables.className?uncap_first} to delete
   * @return boolean <code>true</code> if the ${variables.className?uncap_first} can be deleted, <code>false</code> otherwise
   */
  boolean delete${variables.className}(Long ${variables.className?uncap_first}Id);
  
	/**
   * Saves a ${variables.className?uncap_first} and store it in the database.
   *
   * @param ${variables.className?uncap_first} the {@link ${variables.className}Eto} to create.
   * @return the new {@link ${variables.className}Eto} that has been saved with ID and version.
   */
  ${variables.className}Eto save${variables.className}(${variables.className}Eto ${variables.className?uncap_first});
  
}