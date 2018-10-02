<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api.to;

import io.oasp.module.basic.common.api.query.StringSearchConfigTo;

/**
 * {@link SearchCriteriaTo} to find instances of {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 */
public class ${variables.entityName}SearchCriteriaTo extends SearchCriteriaTo {


  private static final long serialVersionUID = 1L;

	<@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=true/>


  <@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=false  implementsInterface=false isSearchCriteria=true/>

}
