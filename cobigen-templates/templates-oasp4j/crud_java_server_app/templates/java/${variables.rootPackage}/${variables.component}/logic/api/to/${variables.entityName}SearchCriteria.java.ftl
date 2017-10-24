<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import com.capgemini.devonfw.module.mybatis.common.SearchCriteria;

/**
 * This is the {@link SearchCriteria search criteria} 
 * used to find {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 *
 */
public class ${variables.entityName}SearchCriteria extends SearchCriteria {

   private static final long serialVersionUID = 1L;

  <@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion/>

  <@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=false  implementsInterface=false/>

 

 
  

}
