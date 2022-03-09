package ${variables.rootPackage}.${variables.component}.logic.api.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;
import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UcFind${variables.entityName} {

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

}
