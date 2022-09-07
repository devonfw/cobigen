<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import com.devonfw.module.basic.common.api.query.StringSearchConfigTo;
import ${variables.rootPackage}.general.common.api.to.AbstractSearchCriteriaTo;

<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>
<#if compositeIdTypeVar!="null">
import ${variables.rootPackage}.${variables.component}.common.api.${compositeIdTypeVar};
</#if>
/**
 * {@link SearchCriteriaTo} to find instances of {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 */
public class ${variables.entityName}SearchCriteriaTo extends AbstractSearchCriteriaTo {

  private static final long serialVersionUID = 1L;

	<@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=true/>

  <@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=false  implementsInterface=false isSearchCriteria=true/>

}
