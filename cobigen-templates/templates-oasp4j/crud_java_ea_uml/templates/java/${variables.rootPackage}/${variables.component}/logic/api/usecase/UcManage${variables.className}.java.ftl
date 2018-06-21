package ${variables.rootPackage}.${variables.component}.logic.api.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.className}Eto;

/**
 * Interface of UcManage${variables.className} to centralize documentation and signatures of methods.
 */
public interface UcManage${variables.className} {

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
