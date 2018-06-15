<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.logic.api.to;

import io.oasp.module.jpa.common.api.to.SearchCriteriaTo;

/**
 * This is the {@link SearchCriteriaTo search criteria} {@link net.sf.mmm.util.transferobject.api.TransferObject TO}
 * used to find {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.className}}s.
 *
 */
public class ${variables.className}SearchCriteriaTo extends SearchCriteriaTo {


  private static final long serialVersionUID = 1L;

	<@generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=true/>

  /**
   * The constructor.
   */
  public ${variables.className}SearchCriteriaTo() {

    super();
  }

  <@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=false  implementsInterface=false isSearchCriteria=true/>

}
