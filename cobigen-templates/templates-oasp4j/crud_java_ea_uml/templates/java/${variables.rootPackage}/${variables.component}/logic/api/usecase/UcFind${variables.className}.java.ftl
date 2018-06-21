package ${variables.rootPackage}.${variables.component}.logic.api.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}SearchCriteriaTo;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;

import java.util.List;

public interface UcFind${variables.className} {

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

}
