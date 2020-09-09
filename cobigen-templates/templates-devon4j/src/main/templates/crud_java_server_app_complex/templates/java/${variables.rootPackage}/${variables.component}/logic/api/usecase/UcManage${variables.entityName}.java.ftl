package ${variables.rootPackage}.${variables.component}.logic.api.usecase;

import ${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}Eto;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>

/**
 * Interface of UcManage${variables.entityName} to centralize documentation and signatures of methods.
 */
public interface UcManage${variables.entityName} {

  /**
   * Deletes a ${variables.entityName?uncap_first} from the database by its id '${variables.entityName?uncap_first}Id'.
   *
   * @param ${variables.entityName?uncap_first}Id Id of the ${variables.entityName?uncap_first} to delete
   * @return boolean <code>true</code> if the ${variables.entityName?uncap_first} can be deleted, <code>false</code> otherwise
   */
  boolean delete${variables.entityName}(<#if compositeIdTypeVar!="null"> ${compositeIdTypeVar} <#else> long </#if>${variables.entityName?uncap_first}Id);

  /**
   * Saves a ${variables.entityName?uncap_first} and store it in the database.
   *
   * @param ${variables.entityName?uncap_first} the {@link ${variables.entityName}Eto} to create.
   * @return the new {@link ${variables.entityName}Eto} that has been saved with ID and version.
   */
  ${variables.entityName}Eto save${variables.entityName}(${variables.entityName}Eto ${variables.entityName?uncap_first});

}
