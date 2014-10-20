package ${variables.rootPackage}.${variables.component}.logic.base;

import ${variables.rootPackage}.${variables.component}.service.api.to.${variables.entityName}Eto;

import java.util.List;

public interface UcFind${variables.entityName} {

  /**
   * Returns a ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${variables.entityName}Eto} with id 'id'
   */
  ${variables.entityName}Eto get${variables.entityName}(Long id);

  /**
   * Returns a list of all existing ${variables.entityName}s.
   *
   * @return {@link List} of all existing {@link ${variables.entityName}Eto}s
   */
  List<${variables.entityName}Eto> getAll${variables.entityName}s();

}
